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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Text
        extends GuiElement
{
    public static final String           JSON_TEXT = "text";
    public static final ResourceLocation ID        = ResourceLocation.withDefaultNamespace(JSON_TEXT);

    public static final String JSON_COLOR             = "color";
    public static final String JSON_SHADOW_COLOR      = "shadowColor";
    public static final String JSON_BORDER_COLOR      = "borderColor";
    public static final String JSON_SHADOW            = "shadow";
    public static final String JSON_BORDERED          = "bordered";
    public static final String JSON_JUSTIFY_LAST_LINE = "justifyLastLine";
    public static final String JSON_WRAP_WIDTH        = "wrapWidth";
    public static final String JSON_LINE_HEIGHT       = "lineHeight";
    public static final String JSON_FONT              = "font";
    public static final String JSON_TEXT_ALIGN        = "textAlign";

    @Nonnull
    protected Component bakedText   = Component.empty();
    protected ColorData color       = ColorData.BLACK;
    protected ColorData shadowColor = new ColorData(ColorData.StatedColor.getShadowColor(color.color()));
    protected ColorData borderColor = new ColorData(ColorData.StatedColor.getBorderColor(color.color()));
    protected boolean   shadow      = true;
    protected int              wrapWidth       = 0;
    protected int              lineHeight      = 10;
    protected boolean          bordered        = false;
    protected boolean          justifyLastLine = false;
    protected ResourceLocation globalFontID    = null;
    protected Alignment        textAlign       = Alignment.LEFT;

    protected Consumer<Text> onTextChange;

    @Nonnull
    protected BiFunction<IGui, Component, Component> getText = (gui, origText) -> origText;

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
        boolean                 disabled = !this.isEnabled();
        boolean                 hovering = this.isHovering();
        Iterator<FormattedText> lines    = this.renderedLines.listIterator();

        Matrix4f                       pose         = graphics.pose().last().pose();
        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();

        int colorInt       = this.color.getColor(disabled, hovering);
        int shadowColorInt = this.shadow ? this.shadowColor.getColor(disabled, hovering) : 0;
        int borderColorInt = this.bordered ? this.borderColor.getColor(disabled, hovering) : 0;
        while( lines.hasNext() ) {
            FormattedText line     = lines.next();
            boolean       lastLine = !lines.hasNext();

            if( this.bordered ) {
                x += 1;
                y += 1;
                this.renderLineBordered(line, x, y, pose, bufferSource, lastLine, borderColorInt, shadowColorInt);
            } else if( this.shadow ) {
                this.renderLineShadow(line, x, y, pose, bufferSource, lastLine, shadowColorInt);
            }

            this.renderLine(line, x, y, pose, bufferSource, lastLine, colorInt);

            y += this.lineHeight;
        }

        graphics.flush();
    }

    @Override
    @SuppressWarnings("java:S1192")
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.bakedText = data.has(JSON_TEXT) ? Component.translatable(JsonUtils.getStringVal(data.get(JSON_TEXT))) : Component.empty();
        this.color = ColorData.loadColor(data.get(JSON_COLOR), false, ColorData.BLACK.color());
        this.shadowColor = ColorData.loadColor(data.get(JSON_SHADOW_COLOR), false, ColorData.StatedColor.getShadowColor(this.color.color()));
        this.borderColor = ColorData.loadColor(data.get(JSON_BORDER_COLOR), false, ColorData.StatedColor.getBorderColor(this.color.color()));
        this.shadow = JsonUtils.getBoolVal(data.get(JSON_SHADOW), true);
        this.bordered = JsonUtils.getBoolVal(data.get(JSON_BORDERED), false);
        this.justifyLastLine = JsonUtils.getBoolVal(data.get(JSON_JUSTIFY_LAST_LINE), false);
        this.wrapWidth = JsonUtils.getIntVal(data.get(JSON_WRAP_WIDTH), 0);
        this.lineHeight = JsonUtils.getIntVal(data.get(JSON_LINE_HEIGHT), 9);
        this.globalFontID = JsonUtils.getLocation(data.get(JSON_FONT), null);
        this.textAlign = Alignment.fromString(JsonUtils.getStringVal(data.get(JSON_TEXT_ALIGN), Alignment.LEFT.toString()));
    }

    public void setOnTextChange(Consumer<Text> onTextChange) {
        this.onTextChange = onTextChange;
    }

    public void setTextFunc(@Nonnull BiFunction<IGui, Component, Component> func) {
        this.getText = func;
    }

    public void updateText(Component text) {
        int prevWidth  = width;
        int prevHeight = height;

        this.renderedLines.clear();

        this.renderedLines.addAll(Arrays.asList(this.font.getSplitter()
                                                         .splitLines(text, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, text.getStyle())
                                                         .toArray(new FormattedText[0])));

        this.width = this.getTextAlignment() == Alignment.JUSTIFY && this.wrapWidth > 0
                     ? this.wrapWidth
                     : this.renderedLines.stream().map(this.font::width).max(Integer::compareTo).orElse(0) - (this.shadow ? 0 : 1);

        this.height = Math.max(1, this.renderedLines.size()) * this.lineHeight;
        if( this.bordered ) {
            this.width += 2;
            this.height += 2;
        }
        if( this.shadow ) {
            this.height += 1;
        }
        this.height -= 1;

        this.prevText = text;

        if( this.onTextChange != null ) {
            this.onTextChange.accept(this);
        }

        if( prevWidth != this.width || prevHeight != this.height ) {
            this.runGeometryListeners();
        }
    }

    public Font getFont() {
        return this.font;
    }

    public ColorData getColor() {
        return this.color;
    }

    public ColorData getShadowColor() {
        return this.shadowColor;
    }

    public ColorData getBorderColor() {
        return this.borderColor;
    }

    protected void renderLine(FormattedText s, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine, int color) {
        Alignment textAlignment = this.getTextAlignment();
        switch( textAlignment ) {
            case JUSTIFY:
                if( this.wrapWidth > 0 && (this.justifyLastLine || !lastLine) ) {
                    this.renderLineJustified(s, x, y, pose, bufferSource, color);
                    return;
                }
                break;
            case CENTER, RIGHT:
                x = textAlignment.shift(textAlignment.shift(x, this.font.width(s)), -this.width - (this.shadow ? 0 : 1));
                break;
            default:
        }

        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            Component tc = Component.literal(str).withStyle(MiscUtils.apply(globalFontID, style::withFont, style));
            this.font.drawInBatch(tc, mx.getValue(), y, color, false, pose, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
            mx.add(this.font.width(tc));

            return Optional.empty();
        }, Style.EMPTY);
    }

    protected void renderLineBordered(FormattedText line, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine, int color, int shadowColor) {
        if( this.shadow ) {
            renderLineShadow(line, x, y, pose, bufferSource, lastLine, shadowColor);
            renderLineShadow(line, x + 1, y, pose, bufferSource, lastLine, shadowColor);
            renderLineShadow(line, x, y + 1, pose, bufferSource, lastLine, shadowColor);
        }

        this.renderLine(line, x + 1, y, pose, bufferSource, lastLine, color);
        this.renderLine(line, x, y + 1, pose, bufferSource, lastLine, color);
        this.renderLine(line, x - 1, y, pose, bufferSource, lastLine, color);
        this.renderLine(line, x, y - 1, pose, bufferSource, lastLine, color);
    }

    public Alignment getTextAlignment() {
        return this.textAlign.forHorizontal ? this.textAlign : Alignment.LEFT;
    }

    protected void renderLineShadow(FormattedText line, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, boolean lastLine, int color) {
        this.renderLine(line, x + 1, y + 1, pose, bufferSource, lastLine, color);
    }

    protected void renderLineJustified(FormattedText s, int x, int y, Matrix4f pose, MultiBufferSource bufferSource, int color) {
        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            String[] words      = str.split("\\s");
            int      spaceDist  = this.wrapWidth;
            int[]    wordWidths = new int[words.length];
            int      lastId     = words.length - 1;

            for( int i = 0; i <= lastId; i++ ) {
                wordWidths[i] = this.font.width(words[i]) - (this.shadow ? 0 : 1);
                spaceDist -= wordWidths[i];
            }

            int totalSpace = spaceDist;
            spaceDist /= lastId;
            int lastDistAdd = totalSpace - (spaceDist * lastId);

            for( int i = 0; i <= lastId; i++ ) {
                this.font.drawInBatch(Component.literal(words[i]).withStyle(MiscUtils.apply(globalFontID, style::withFont, style)), mx.getValue(), y,
                                      color, false, pose, bufferSource,
                                      Font.DisplayMode.NORMAL, 0, 0xF000F0);
                mx.add(wordWidths[i] + spaceDist + (i == (lastId - 1) ? lastDistAdd : 0));
            }

            return Optional.empty();
        }, Style.EMPTY);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder<T extends Text>
            extends GuiElement.Builder<T>
    {
        private boolean customShadowColor = false;
        private boolean customBorderColor = false;

        protected Builder(T elem) {super(elem);}

        public Builder<T> withText(Component text) {
            this.elem.bakedText = text;

            return this;
        }

        public Builder<T> withTranslatedText(String key) {
            return this.withText(Component.translatable(key));
        }

        public Builder<T> withTextColor(ColorData.StatedColor color) {
            this.elem.color = new ColorData(color);

            return this;
        }

        public Builder<T> withShadowColor(ColorData.StatedColor color) {
            this.elem.shadowColor = new ColorData(color);
            this.customShadowColor = true;

            return this;
        }

        public Builder<T> withBorderColor(ColorData.StatedColor color) {
            this.elem.borderColor = new ColorData(color);
            this.customBorderColor = true;

            return this;
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

        @Override
        public T get() {
            if( !this.customShadowColor ) {
                this.elem.shadowColor = new ColorData(ColorData.StatedColor.getShadowColor(this.elem.color.color()));
            }
            if( !this.customBorderColor ) {
                this.elem.shadowColor = new ColorData(ColorData.StatedColor.getBorderColor(this.elem.color.color()));
            }

            return super.get();
        }

        public static Builder<Text> createText() {
            return createText(UUID.randomUUID().toString());
        }

        public static Builder<Text> createText(String id) {
            return new Builder<>(new Text(id));
        }
    }
}
