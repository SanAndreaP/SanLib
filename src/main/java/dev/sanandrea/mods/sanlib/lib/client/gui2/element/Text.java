package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui2.Font;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableInt;

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

@SuppressWarnings("unused")
public class Text
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("text");

    public static final String DEFAULT_COLOR = "default";
    public static final String BORDER_COLOR  = "border";
    public static final String SHADOW_COLOR  = "shadow";
    public static final String DEFAULT_SHADOW_COLOR = "default_shadow";

    @Nonnull
    protected ITextComponent       bakedText       = StringTextComponent.EMPTY;
    protected Map<String, Integer> colors          = new HashMap<>();
    protected boolean              shadow          = true;
    protected int                  wrapWidth       = 0;
    protected FontRenderer         fontRenderer    = Minecraft.getInstance().font;
    protected int                  lineHeight      = 9;
    protected boolean              bordered        = false;
    protected boolean              justifyLastLine = false;

    private Consumer<Text> onTextChange;

    @Nonnull
    private BiFunction<IGui, ITextComponent, ITextComponent> getText = (gui, origText) -> origText;

    private       int                   currColor;
    private       int                   prevColor;
    private       ITextComponent        prevText;
    private final List<ITextProperties> renderedLines = new ArrayList<>();

    public Text(String id) {
        super(id);
    }

    @Override
    public void tick(IGui gui) {
        ITextComponent currText = this.getText.apply(gui, this.bakedText);
        if( prevText != currText ) {
            this.updateText(currText);
            ColorObj clr = new ColorObj(colors.get(DEFAULT_COLOR));
            colors.put(DEFAULT_SHADOW_COLOR, new ColorObj(clr.fRed()*0.25F, clr.fGreen()*0.25F, clr.fBlue()*0.25F, clr.fAlpha()).getColorInt());
        }
    }

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) {
        Iterator<ITextProperties> lines = this.renderedLines.listIterator();
        while( lines.hasNext() ) {
            ITextProperties line = lines.next();
            boolean lastLine = !lines.hasNext();

            boolean hasCustomShadowColor = this.colors.containsKey(SHADOW_COLOR);
            if( this.bordered ) {
                this.renderLineBordered(matrixStack, x, y, line, hasCustomShadowColor, lastLine);
            } else if( this.shadow ) {
                if( hasCustomShadowColor ) {
                    this.setColor(SHADOW_COLOR);
                } else {
                    this.setColor(DEFAULT_SHADOW_COLOR);
                }
                this.renderLine(matrixStack, line, x+1, y+1, lastLine);
                this.resetColor();
            }
            this.renderLine(matrixStack, line, x, y, lastLine);
            y += this.lineHeight;
        }
    }

    @Override
    @SuppressWarnings("java:S1192")
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.bakedText = data.has("text") ? new TranslationTextComponent(JsonUtils.getStringVal(data.get("text"))) : StringTextComponent.EMPTY;
        ColorDef.loadKeyedColors(data, this.colors, DEFAULT_COLOR, ColorObj.BLACK.getColorInt());
        this.shadow = JsonUtils.getBoolVal(data.get("shadow"), true);
        this.bordered = JsonUtils.getBoolVal(data.get("bordered"), false);
        this.justifyLastLine = JsonUtils.getBoolVal(data.get("justifyLastLine"), false);
        this.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
        this.lineHeight = JsonUtils.getIntVal(data.get("lineHeight"), 9);

        this.fontRenderer = Font.loadFont(data).get(Minecraft.getInstance());

        this.setColor(DEFAULT_COLOR);
    }

    public void setOnTextChange(Consumer<Text> onTextChange) {
        this.onTextChange = onTextChange;
    }

    public void setTextFunc(@Nonnull BiFunction<IGui, ITextComponent, ITextComponent> func) {
        this.getText = func;
    }

    public void updateText(ITextComponent text) {
        this.renderedLines.clear();

        this.renderedLines.addAll(Arrays.asList(this.fontRenderer.getSplitter()
                                                                 .splitLines(text, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, text.getStyle())
                                                                 .toArray(new ITextProperties[0])));

        this.width = this.hAlignment == Alignment.JUSTIFY && this.wrapWidth > 0
                     ? this.wrapWidth
                     : this.renderedLines.stream().map(l -> this.fontRenderer.width(l)).max(Integer::compareTo).orElse(0);

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
        this.prevColor = this.currColor;

        if( colorId == null || !this.colors.containsKey(colorId) ) {
            this.currColor = this.colors.getOrDefault(DEFAULT_COLOR, 0xFF000000);
        } else {
            this.currColor = this.colors.get(colorId);
        }
    }

    public void resetColor() {
        int clr = this.currColor;
        this.currColor = this.prevColor;
        this.prevColor = clr;
    }

    protected void renderLine(MatrixStack stack, ITextProperties s, int x, int y, boolean lastLine) {
        switch( this.hAlignment ) {
            case JUSTIFY:
                if( this.wrapWidth > 0 && (this.justifyLastLine || !lastLine) ) {
                    this.renderLineJustified(stack, s, x, y);
                    return;
                }
                break;
            case LEFT:
                break;
            case CENTER:
                x += (this.width - this.fontRenderer.width(s)) / 2;
                break;
            case RIGHT:
                x += this.width - this.fontRenderer.width(s);
                break;
            default:
        }

        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            ITextComponent tc = new StringTextComponent(str).withStyle(style);
            this.fontRenderer.draw(stack, tc, mx.getValue(), y, this.currColor);
            mx.add(this.fontRenderer.width(tc));

            return Optional.empty();
        }, Style.EMPTY);
    }

    protected void renderLineBordered(MatrixStack matrixStack, int x, int y, ITextProperties line, boolean hasCustomShadowColor, boolean lastLine) {
        if( this.shadow ) {
            if( hasCustomShadowColor ) {
                this.setColor(SHADOW_COLOR);
            } else {
                this.setColor(DEFAULT_SHADOW_COLOR);
            }
            this.renderLine(matrixStack, line, x+1, y+1, lastLine);
            this.renderLine(matrixStack, line, x+2, y+1, lastLine);
            this.renderLine(matrixStack, line, x+1, y+2, lastLine);
            this.resetColor();
        }
        this.setColor(BORDER_COLOR);
        this.renderLine(matrixStack, line, x + 1, y, lastLine);
        this.renderLine(matrixStack, line, x, y + 1, lastLine);
        this.renderLine(matrixStack, line, x - 1, y, lastLine);
        this.renderLine(matrixStack, line, x, y - 1, lastLine);
        this.resetColor();
    }

    protected void renderLineJustified(MatrixStack stack, ITextProperties s, int x, int y) {
        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            String[] words      = str.split("\\s");
            float    spaceDist  = this.wrapWidth;
            int[]    wordWidths = new int[words.length];

            for( int i = 0; i < words.length; i++ ) {
                wordWidths[i] = this.fontRenderer.width(words[i]);
                spaceDist -= wordWidths[i];
            }

            spaceDist /= words.length - 1;

            for( int i = 0; i < words.length; i++ ) {
                IReorderingProcessor irp = IReorderingProcessor.forward(words[i], style);
                this.fontRenderer.draw(stack, irp, mx.getValue(), y, this.currColor);
                mx.add(wordWidths[i] + spaceDist);
            }

            return Optional.empty();
        }, Style.EMPTY);
    }

    public static class Builder<T extends Text>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) { super(elem); }

        public Builder<T> withText(ITextComponent text) {
            this.elem.bakedText = text;

            return this;
        }

        public Builder<T> withTranslatedText(String key) {
            return this.withText(new TranslationTextComponent(key));
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

        public Builder<T> withWrapWidth(int wrapWidth) {
            this.elem.wrapWidth = wrapWidth;

            return this;
        }

        public Builder<T> withFontRenderer(FontRenderer fontRenderer) {
            this.elem.fontRenderer = fontRenderer;

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

        public static Builder<Text> create() {
            return create(UUID.randomUUID().toString());
        }

        public static Builder<Text> create(String id) {
            return new Builder<>(new Text(id));
        }
    }
}
