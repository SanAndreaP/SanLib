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
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
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

@SuppressWarnings({ "unused", "WeakerAccess" })
@IGuiElement.Priority(value = EventPriority.HIGHEST, target = IGuiElement.PriorityTarget.KEY_INPUT)
public class TextField
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("textfield");

    private boolean enabled = true;
    
    public int[]        size;
    public int          color;
    public int          disabledColor;
    public int          placeholderColor;
    public boolean      canLoseFocus;
    public boolean      drawBackground;
    public ITextComponent placeholderText;
    public FontRenderer   fontRenderer;

    private TextFieldWidget textfield;

    private int maxStringLength;
    private boolean isEditable = true;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.placeholderText = new TranslationTextComponent(JsonUtils.getStringVal(data.get("placeholderText"), ""));
        this.canLoseFocus = JsonUtils.getBoolVal(data.get("canLoseFocus"), true);
        this.drawBackground = JsonUtils.getBoolVal(data.get("drawBackground"), true);
        this.color = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("textColor"), "0xFFE0E0E0"));
        this.placeholderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("placeholderColor"), "0xFF707070"));
        this.disabledColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("disabledTextColor"), "0xFF707070"));

        JsonElement cstFont = data.get("font");
        if( cstFont == null ) {
            this.fontRenderer = Minecraft.getInstance().font;
        } else {
            this.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Text.Font.class).get(gui.get(), data.getAsJsonObject("glyphProvider"));
        }

        this.textfield = new TextFieldWidget(this.fontRenderer, 0, 0, this.size[0], this.size[1], StringTextComponent.EMPTY);
        this.textfield.setTextColor(this.color);
        this.textfield.setTextColorUneditable(this.disabledColor);
        this.textfield.setCanLoseFocus(this.canLoseFocus);
        this.textfield.setBordered(this.drawBackground);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.textfield.tick();
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        this.textfield.x = x;
        this.textfield.y = y;
        this.textfield.renderButton(stack, x, y, partTicks);
        if( !this.isFocused() && Strings.isNullOrEmpty(this.getText()) && !Strings.isNullOrEmpty(this.placeholderText.getString()) ) {
            x += (this.drawBackground ? 4 : 0);
            y += (this.drawBackground ? (this.size[1] - 8) / 2 : 0);
            this.fontRenderer.draw(stack, this.placeholderText, x, y, this.placeholderColor);
        }
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.textfield.mouseClicked(mouseX - gui.getScreenPosX(), mouseY - gui.getScreenPosY(), button);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.textfield.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setText(String text) {
        this.textfield.setValue(text);
    }

    public String getText() {
        return this.textfield.getValue();
    }

    public void setMaxStringLength(int length) {
        this.textfield.setMaxLength(this.maxStringLength = length);
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public void setEditable(boolean editable) {
        this.textfield.setEditable(this.isEditable = editable);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setFocused(boolean isFocused) {
        this.textfield.setFocus(isFocused);
    }

    public boolean isFocused() {
        return this.textfield.isFocused();
    }

    public void setCursorPosition(int pos) {
        this.textfield.setCursorPosition(pos);
    }

    public void setSelectionEnd(int pos) {
        this.textfield.setHighlightPos(pos);
    }

    public int[] getSelectionPos() {
        return new int[] { this.textfield.getCursorPosition(), this.textfield.getCursorPosition() + this.textfield.getHighlighted().length() };
    }

    public String getSelectedText() {
        return this.textfield.getHighlighted();
    }

    public void insertText(String text) {
        this.textfield.insertText(text);
    }

    public void setCursorPositionEnd() {
        this.textfield.moveCursorToEnd();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.textfield.setEditable(enabled);
        this.enabled = enabled;
    }

    @Override
    public boolean isVisible() {
        return this.textfield != null && this.textfield.isVisible();
    }

    public void setValidator(Predicate<String> validator) {
        this.textfield.setFilter(validator);
    }

    public void setResponder(Consumer<String> responder) {
        this.textfield.setResponder(responder);
    }

    @Override
    public int getWidth() {
        return this.textfield.getWidth();
    }

    @Override
    public int getHeight() {
        return this.size[1] - (this.drawBackground ? 8 : 0);
    }
}
