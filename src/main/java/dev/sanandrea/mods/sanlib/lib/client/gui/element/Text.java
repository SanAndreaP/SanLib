package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class Text
        extends GuiElement
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("text");

    public static final String DEFAULT_COLOR  = "default";
    public static final String HOVER_COLOR    = "hover";
    public static final String DISABLED_COLOR = "disabled";

    public static final String BORDER_COLOR          = "border";
    public static final String HOVER_BORDER_COLOR    = "borderHover";
    public static final String DISABLED_BORDER_COLOR = "borderDisabled";

    public static final String SHADOW_COLOR          = "shadow";
    public static final String HOVER_SHADOW_COLOR    = "shadowHover";
    public static final String DISABLED_SHADOW_COLOR = "shadowDisabled";

    public static final String DEFAULT_SHADOW_COLOR          = "default_shadow";
    public static final String DEFAULT_HOVER_SHADOW_COLOR    = "default_shadowHover";
    public static final String DEFAULT_DISABLED_SHADOW_COLOR = "default_shadowDisabled";

    @Nonnull
    protected Component            bakedText       = Component.empty();
    protected Map<String, Integer> colors          = new HashMap<>();
    protected boolean              shadow          = true;
    protected int                  wrapWidth       = 0;
    protected int                  lineHeight      = 10;
    protected boolean              bordered        = false;
    protected boolean              justifyLastLine = false;
    protected ResourceLocation     globalFontID    = null;

    protected Consumer<Text> onTextChange;

    @Nonnull
    protected BiFunction<IGui, Component, Component> getText = (gui, origText) -> origText;

    protected       String              currColorId   = DEFAULT_COLOR;
    protected       int                 currColor;
    protected       int                 prevColor;
    protected       Component           prevText;
    protected final List<FormattedText> renderedLines = new ArrayList<>();
    protected final Font                font          = Minecraft.getInstance().font;

    public Text(String id) {
        super(id);
    }

    @Override
    public void tick(IGui gui) {
        Component currText = this.getText.apply(gui, this.bakedText);
        if( prevText != currText ) {
            this.updateText(currText);
        }
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        Iterator<FormattedText> lines = this.renderedLines.listIterator();

        switch( this.vAlignment ) {
            case CENTER:
                y -= this.height / 2;
                break;
            case BOTTOM:
                y -= this.height;
                break;
            default:
        }

        Matrix4f                       pose         = graphics.pose().last().pose();
        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
        while( lines.hasNext() ) {
            FormattedText line     = lines.next();
            boolean       lastLine = !lines.hasNext();

            if( this.bordered ) {
                this.renderLineBordered(line, x, y, pose, bufferSource, lastLine);
            } else if( this.shadow ) {
                this.renderLineShadow(line, x, y, pose, bufferSource, lastLine);
            }

            this.setColor(this.getTextColorKey(DISABLED_COLOR, HOVER_COLOR, this.currColorId, null, null), false);
            this.renderLine(line, x, y, pose, bufferSource, lastLine);

            y += this.lineHeight;
        }
    }

    @Override
    @SuppressWarnings("java:S1192")
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.bakedText = data.has("text") ? Component.translatable(JsonUtils.getStringVal(data.get("text"))) : Component.empty();
        ColorDef.loadKeyedColors(data, this.colors, DEFAULT_COLOR, ColorObj.BLACK.getColorInt());
        this.shadow = JsonUtils.getBoolVal(data.get("shadow"), true);
        this.bordered = JsonUtils.getBoolVal(data.get("bordered"), false);
        this.justifyLastLine = JsonUtils.getBoolVal(data.get("justifyLastLine"), false);
        this.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
        this.lineHeight = JsonUtils.getIntVal(data.get("lineHeight"), 9);
        this.globalFontID = JsonUtils.getLocation(data.get("font"), null);

        this.setColor(DEFAULT_COLOR);
    }

    public void setOnTextChange(Consumer<Text> onTextChange) {
        this.onTextChange = onTextChange;
    }

    public void setTextFunc(@Nonnull BiFunction<IGui, Component, Component> func) {
        this.getText = func;
    }

    public void updateText(Component text) {
        this.renderedLines.clear();

        this.renderedLines.addAll(Arrays.asList(this.font.getSplitter()
                                                         .splitLines(text, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, text.getStyle())
                                                         .toArray(new FormattedText[0])));

        this.width = this.hAlignment == Alignment.JUSTIFY && this.wrapWidth > 0
                     ? this.wrapWidth
                     : this.renderedLines.stream().map(this.font::width).max(Integer::compareTo).orElse(0);

        this.height = Math.max(1, this.renderedLines.size()) * this.lineHeight;
        if( this.bordered ) {
            this.height += 1;
        }
        if( this.shadow ) {
            this.height += 1;
        }
        this.height -= 2;

        this.prevText = text;

        if( this.onTextChange != null ) {
            this.onTextChange.accept(this);
        }
    }

    public void setColor(String colorId) {
        this.setColor(colorId, true);
    }

    protected void setColor(String colorId, boolean updateCurrColorId) {
        this.prevColor = this.currColor;

        if( colorId == null || !this.colors.containsKey(colorId) ) {
            if( updateCurrColorId ) {
                this.currColorId = DEFAULT_COLOR;
            }
            this.currColor = this.getColor(DEFAULT_COLOR);
        } else {
            if( updateCurrColorId ) {
                this.currColorId = colorId;
            }
            this.currColor = this.colors.get(colorId);
        }
    }

    public void resetColor() {
        int clr = this.currColor;
        this.currColor = this.prevColor;
        this.prevColor = clr;
    }

    public Font getFont() {
        return this.font;
    }

    protected void renderLine(FormattedText s, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine) {
        switch( this.hAlignment ) {
            case JUSTIFY:
                if( this.wrapWidth > 0 && (this.justifyLastLine || !lastLine) ) {
                    this.renderLineJustified(s, x, y, pose, bufferSource);
                    return;
                }
                break;
            case CENTER:
                x -= this.font.width(s) / 2;
                break;
            case RIGHT:
                x -= this.font.width(s);
                break;
            default:
        }

        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            Component tc = Component.literal(str).withStyle(MiscUtils.apply(globalFontID, style::withFont));
            this.font.drawInBatch(tc, mx.getValue(), y, this.currColor, false, pose, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
            mx.add(this.font.width(tc));

            return Optional.empty();
        }, Style.EMPTY);
    }

    protected void renderLineBordered(FormattedText line, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine) {
        if( this.shadow ) {
            renderLineShadow(line, x, y, pose, bufferSource, lastLine);
            renderLineShadow(line, x + 1, y, pose, bufferSource, lastLine);
            renderLineShadow(line, x, y + 1, pose, bufferSource, lastLine);
        }

        this.setColor(this.getTextColorKey(DISABLED_BORDER_COLOR, HOVER_BORDER_COLOR, BORDER_COLOR, null, null), false);
        this.renderLine(line, x + 1, y, pose, bufferSource, lastLine);
        this.renderLine(line, x, y + 1, pose, bufferSource, lastLine);
        this.renderLine(line, x - 1, y, pose, bufferSource, lastLine);
        this.renderLine(line, x, y - 1, pose, bufferSource, lastLine);
        this.resetColor();
    }

    protected String getTextColorKey(String disabledKey, String hoverKey, String regularKey,
                                     UnaryOperator<String> altDisabledComputor, UnaryOperator<String> altHoverComputor)
    {
        if( !this.isEnabled() ) {
            if( this.colors.containsKey(disabledKey) ) {
                return disabledKey;
            } else if( altDisabledComputor != null ) {
                return altDisabledComputor.apply(regularKey);
            } else {
                return regularKey;
            }
        } else if( this.isHovering() ) {
            if( this.colors.containsKey(hoverKey) ) {
                return hoverKey;
            } else if( altHoverComputor != null ) {
                return altHoverComputor.apply(regularKey);
            } else {
                return regularKey;
            }
        } else {
            return regularKey;
        }
    }

    protected int getShadowColor(String baseColorKey) {
        ColorObj clr = new ColorObj(this.getColor(baseColorKey));
        return new ColorObj(clr.fRed() * 0.25F, clr.fGreen() * 0.25F, clr.fBlue() * 0.25F, clr.fAlpha()).getColorInt();
    }

    public int getColor(String colorKey) {
        return colors.getOrDefault(colorKey, 0xFF000000);
    }

    protected void renderLineShadow(FormattedText line, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine) {
        String colorKey = this.getTextColorKey(DISABLED_SHADOW_COLOR, HOVER_SHADOW_COLOR, SHADOW_COLOR,
                                               regularKey -> {
                                                   if( this.colors.containsKey(DISABLED_COLOR) ) {
                                                       this.colors.computeIfAbsent(DEFAULT_DISABLED_SHADOW_COLOR, key -> this.getShadowColor(DISABLED_COLOR));
                                                       return DEFAULT_DISABLED_SHADOW_COLOR;
                                                   }
                                                   return regularKey;
                                               }, regularKey -> {
                    if( this.colors.containsKey(HOVER_COLOR) ) {
                        this.colors.computeIfAbsent(DEFAULT_HOVER_SHADOW_COLOR, key -> this.getShadowColor(HOVER_COLOR));
                        return DEFAULT_HOVER_SHADOW_COLOR;
                    }
                    return regularKey;
                });

        if( this.colors.containsKey(colorKey) ) {
            this.setColor(colorKey, false);
        } else {
            this.colors.computeIfAbsent(DEFAULT_SHADOW_COLOR, key -> this.getShadowColor(DEFAULT_COLOR));
            this.setColor(DEFAULT_SHADOW_COLOR, false);
        }

        this.renderLine(line, x + 1, y + 1, pose, bufferSource, lastLine);
        this.resetColor();
    }

    protected void renderLineJustified(FormattedText s, int x, int y, Matrix4f pose, MultiBufferSource bufferSource) {
        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            String[] words      = str.split("\\s");
            float    spaceDist  = this.wrapWidth;
            int[]    wordWidths = new int[words.length];

            for( int i = 0; i < words.length; i++ ) {
                wordWidths[i] = this.font.width(words[i]);
                spaceDist -= wordWidths[i];
            }

            spaceDist /= words.length - 1;

            for( int i = 0; i < words.length; i++ ) {
                this.font.drawInBatch(Component.literal(words[i]).withStyle(MiscUtils.apply(globalFontID, style::withFont)), mx.getValue(), y, this.currColor, false, pose, bufferSource,
                                      Font.DisplayMode.NORMAL, 0, 0xF000F0);
                mx.add(wordWidths[i] + spaceDist);
            }

            return Optional.empty();
        }, Style.EMPTY);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder<T extends Text>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {super(elem);}

        public Builder<T> withText(Component text) {
            this.elem.bakedText = text;

            return this;
        }

        public Builder<T> withTranslatedText(String key) {
            return this.withText(Component.translatable(key));
        }

        public Builder<T> withColor(String key, int color) {
            this.elem.colors.put(key, color);

            return this;
        }

        public Builder<T> withTextColor(int color) {
            return this.withColor(DEFAULT_COLOR, color);
        }

        public Builder<T> withShadowColor(int color) {
            return this.withColor(SHADOW_COLOR, color);
        }

        public Builder<T> withBorderColor(int color) {
            return this.withColor(BORDER_COLOR, color);
        }

        public Builder<T> withHoverColor(int color) {
            return this.withColor(HOVER_COLOR, color);
        }

        public Builder<T> withHoverShadowColor(int color) {
            return this.withColor(HOVER_SHADOW_COLOR, color);
        }

        public Builder<T> withHoverBorderColor(int color) {
            return this.withColor(HOVER_BORDER_COLOR, color);
        }

        public Builder<T> withDisabledColor(int color) {
            return this.withColor(DISABLED_COLOR, color);
        }

        public Builder<T> withDisabledShadowColor(int color) {
            return this.withColor(DISABLED_SHADOW_COLOR, color);
        }

        public Builder<T> withDisabledBorderColor(int color) {
            return this.withColor(DISABLED_BORDER_COLOR, color);
        }

        public Builder<T> withShadow() {
            this.elem.shadow = true;

            return this;
        }

        public Builder<T> withoutShadow() {
            this.elem.shadow = false;

            return this;
        }

        public Builder<T> withBorder() {
            this.elem.bordered = true;

            return this;
        }

        public Builder<T> withoutBorder() {
            this.elem.bordered = false;

            return this;
        }

        public Builder<T> withFont(ResourceLocation fontId) {
            this.elem.globalFontID = fontId;

            return this;
        }

        public Builder<T> withWrapWidth(int wrapWidth) {
            this.elem.wrapWidth = wrapWidth;

            return this;
        }

        public Builder<T> withLineHeight(int lineHeight) {
            this.elem.lineHeight = lineHeight;

            return this;
        }

        public Builder<T> withLastLineJustified() {
            this.elem.justifyLastLine = true;

            return this;
        }

        public Builder<T> withoutLastLineJustified() {
            this.elem.justifyLastLine = false;

            return this;
        }

        public static Builder<Text> createText() {
            return createText(UUID.randomUUID().toString());
        }

        public static Builder<Text> createText(String id) {
            return new Builder<>(new Text(id));
        }
    }
}
