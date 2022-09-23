////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.commons.lang3.Range;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
@IGuiElement.Priority(value = EventPriority.HIGHEST, target = IGuiElement.PriorityTarget.KEY_INPUT)
public class TextField
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("textfield");

    private boolean enabled = true;
    
    protected int[] size;
    protected int textColor;
    protected int     disabledTextColor;
    protected int     placeholderTextColor;
    protected boolean canLoseFocus;
    protected boolean        drawBackground;
    protected ITextComponent placeholderText;
    protected FontRenderer   fontRenderer;

    private TextFieldWidget textfieldWidget;

    private int maxStringLength;
    private boolean isEditable = true;

    @SuppressWarnings("java:S107")
    public TextField(int[] size, int textColor, int disabledTextColor, int placeholderTextColor, boolean canLoseFocus, boolean drawBackground,
                     ITextComponent placeholderText, FontRenderer fontRenderer)
    {
        this.size = size;
        this.textColor = textColor;
        this.disabledTextColor = disabledTextColor;
        this.placeholderTextColor = placeholderTextColor;
        this.canLoseFocus = canLoseFocus;
        this.drawBackground = drawBackground;
        this.placeholderText = placeholderText;
        this.fontRenderer = fontRenderer;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.textfieldWidget = new TextFieldWidget(this.fontRenderer, 0, 0, this.size[0], this.size[1], StringTextComponent.EMPTY);
        this.textfieldWidget.setTextColor(this.textColor);
        this.textfieldWidget.setTextColorUneditable(this.disabledTextColor);
        this.textfieldWidget.setCanLoseFocus(this.canLoseFocus);
        this.textfieldWidget.setBordered(this.drawBackground);
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.textfieldWidget.tick();
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        stack.pushPose();
        this.textfieldWidget.x = gui.getScreenPosX() + x;
        this.textfieldWidget.y = gui.getScreenPosY() + y;
        stack.translate(x - (double) this.textfieldWidget.x, y - (double) this.textfieldWidget.y, 0);
        this.textfieldWidget.renderButton(stack, (int) mouseX, (int) mouseY, partTicks);
        this.textfieldWidget.x = x;
        this.textfieldWidget.y = y;
        stack.popPose();

        if( !this.isFocused() && Strings.isNullOrEmpty(this.getText()) && !Strings.isNullOrEmpty(this.placeholderText.getString()) ) {
            x += (this.drawBackground ? 4 : 0);
            y += (this.drawBackground ? (this.size[1] - 8) / 2 : 0);
            GuiUtils.enableScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.size[0] - (this.drawBackground ? 8 : 0), this.size[1]);
            this.fontRenderer.draw(stack, this.placeholderText, x, y, this.placeholderTextColor);
            GuiUtils.disableScissor();
        }
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.textfieldWidget.mouseClicked(mouseX - gui.getScreenPosX(), mouseY - gui.getScreenPosY(), button);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.textfieldWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.textfieldWidget.charTyped(typedChar, keyCode);
    }

    public void setText(String text) {
        this.textfieldWidget.setValue(text);
    }

    public String getText() {
        return this.textfieldWidget.getValue();
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        this.textfieldWidget.setMaxLength(length);
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
        this.textfieldWidget.setEditable(editable);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setFocused(boolean isFocused) {
        this.textfieldWidget.setFocus(isFocused);
    }

    public boolean isFocused() {
        return this.textfieldWidget.isFocused();
    }

    public void setCursorPosition(int pos) {
        this.textfieldWidget.setCursorPosition(pos);
    }

    public void setSelectionEnd(int pos) {
        this.textfieldWidget.setHighlightPos(pos);
    }

    public int[] getSelectionPos() {
        return new int[] { this.textfieldWidget.getCursorPosition(), this.textfieldWidget.getCursorPosition() + this.textfieldWidget.getHighlighted().length() };
    }

    public String getSelectedText() {
        return this.textfieldWidget.getHighlighted();
    }

    public void insertText(String text) {
        this.textfieldWidget.insertText(text);
    }

    public void setCursorPositionEnd() {
        this.textfieldWidget.moveCursorToEnd();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.textfieldWidget.setEditable(enabled);
        this.enabled = enabled;
    }

    public boolean canConsumeInput() {
        return this.textfieldWidget.canConsumeInput();
    }

    @Override
    public boolean isVisible() {
        return this.textfieldWidget != null && this.textfieldWidget.isVisible();
    }

    public void setVisible(boolean visible) {
        if( this.textfieldWidget != null ) {
            this.textfieldWidget.setVisible(visible);
        }
    }

    public void setValidator(Predicate<String> validator) {
        this.textfieldWidget.setFilter(validator);
    }

    public void setResponder(Consumer<String> responder) {
        this.textfieldWidget.setResponder(responder);
    }

    @Override
    public int getWidth() {
        return this.size[0] + (this.drawBackground ? 2 : 0);
    }

    @Override
    public int getHeight() {
        return this.size[1] + (this.drawBackground ? 2 : 0);
    }

    public static class Builder
            implements IBuilder<TextField>
    {
        public final int[] size;

        protected int            textColor = 0xFFE0E0E0;
        protected int            disabledTextColor = 0xFF707070;
        protected int            placeholderTextColor = 0xFFA0A0A0;
        protected boolean        canLoseFocus = true;
        protected boolean        drawBackground = true;
        protected ITextComponent placeholderText;
        protected FontRenderer   fontRenderer;

        public Builder(int[] size) {
            this.size = size;
        }

        public Builder placeholderText(ITextComponent text)   { this.placeholderText = text;          return this; }
        public Builder textColor(int color)                   { this.textColor = color;               return this; }
        public Builder disabledTextColor(int color)           { this.disabledTextColor = color;       return this; }
        public Builder placeholderTextColor(int color)        { this.placeholderTextColor = color;    return this; }
        public Builder font(FontRenderer fontRenderer)        { this.fontRenderer = fontRenderer;     return this; }
        public Builder canLoseFocus(boolean canLoseFocus)     { this.canLoseFocus = canLoseFocus;     return this; }
        public Builder drawBackground(boolean drawBackground) { this.drawBackground = drawBackground; return this; }

        public Builder placeholderText(String text)                         { return this.placeholderText(new TranslationTextComponent(text)); }
        public Builder textColor(String color)                              { return this.textColor(MiscUtils.hexToInt(color)); }
        public Builder disabledTextColor(String color)                      { return this.disabledTextColor(MiscUtils.hexToInt(color)); }
        public Builder placeholderTextColor(String color)                   { return this.placeholderTextColor(MiscUtils.hexToInt(color)); }
        public Builder font(IGui gui, Text.Font font)                       { return this.font(font.get(gui.get())); }
        public Builder font(IGui gui, Text.Font font, JsonObject glyphData) { return this.font(font.get(gui.get(), glyphData)); }

        @Override
        public void sanitize(IGui gui) {
            if( this.fontRenderer == null ) {
                this.fontRenderer = gui.get().getMinecraft().font;
            }

            if( this.placeholderText == null ) {
                this.placeholderText = StringTextComponent.EMPTY;
            }
        }

        @Override
        public TextField get(IGui gui) {
            this.sanitize(gui);

            return new TextField(this.size, this.textColor, this.disabledTextColor, this.placeholderTextColor, this.canLoseFocus, this.drawBackground, this.placeholderText,
                                 this.fontRenderer);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));

            JsonUtils.fetchString(data.get("placeholderText"), b::placeholderText);
            JsonUtils.fetchBool(data.get("canLoseFocus"), b::canLoseFocus);
            JsonUtils.fetchBool(data.get("drawBackground"), b::drawBackground);
            JsonUtils.fetchString(data.get("textColor"), b::textColor);
            JsonUtils.fetchString(data.get("disabledTextColor"), b::disabledTextColor);
            JsonUtils.fetchString(data.get("placeholderTextColor"), b::placeholderTextColor);

            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                b.font(gui, new Text.Font("standard"));
            } else {
                b.font(gui, JsonUtils.GSON.fromJson(cstFont, Text.Font.class), data.getAsJsonObject("glyphProvider"));
            }

            return b;
        }

        public static TextField fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
