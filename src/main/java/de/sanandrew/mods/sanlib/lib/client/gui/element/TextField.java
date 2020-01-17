package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.apache.commons.lang3.Range;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@IGuiElement.Priority(value = EventPriority.HIGHEST, target = IGuiElement.PriorityTarget.KEY_INPUT)
public class TextField
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("textfield");

    public BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.text = JsonUtils.getStringVal(data.get("text"), "");
            this.data.placeholderText = JsonUtils.getStringVal(data.get("placeholderText"), "");
            this.data.shadow = JsonUtils.getBoolVal(data.get("shadow"), true);
            this.data.canLoseFocus = JsonUtils.getBoolVal(data.get("canLoseFocus"), true);
            this.data.drawBackground = JsonUtils.getBoolVal(data.get("drawBackground"), true);
            this.data.color = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("textColor"), "0xFFE0E0E0"));
            this.data.placeholderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("placeholderColor"), "0xFF707070"));
            this.data.disabledColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("disabledTextColor"), "0xFF707070"));

            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                this.data.fontRenderer = new Text.Font("standard").get(gui.get());
            } else {
                this.data.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Text.Font.class).get(gui.get());
            }

            this.data.textfield = new GuiTextField(0, new FontRendererTF(gui.get().mc, this.data.fontRenderer), 0, 0, this.data.size[0], this.data.size[1]);
            this.data.textfield.setText(LangUtils.translate(this.data.text));
            this.data.textfield.setTextColor(this.data.color);
            this.data.textfield.setDisabledTextColour(this.data.disabledColor);
            this.data.textfield.setCanLoseFocus(this.data.canLoseFocus);
            this.data.textfield.setEnableBackgroundDrawing(this.data.drawBackground);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.data.textfield.updateCursorCounter();
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.data.textfield.x = x;
        this.data.textfield.y = y;
        this.data.textfield.drawTextBox();
        if( !this.isFocused() && !Strings.isNullOrEmpty(this.data.placeholderText) && Strings.isNullOrEmpty(this.getText()) ) {
            x += (this.data.drawBackground ? 4 : 0);
            y += (this.data.drawBackground ? (this.data.size[1] - 8) / 2 : 0);
            this.data.fontRenderer.drawString(LangUtils.translate(this.data.placeholderText), x, y, this.data.placeholderColor, this.data.shadow);
        }
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) {
        return this.data.textfield.mouseClicked(mouseX - gui.getScreenPosX(), mouseY - gui.getScreenPosY(), mouseButton);
    }

    @Override
    public boolean keyTyped(IGui gui, char typedChar, int keyCode) {
        return this.data.textfield.textboxKeyTyped(typedChar, keyCode);
    }

    public void setText(String text) {
        this.data.textfield.setText(text);
    }

    public String getText() {
        return this.data.textfield.getText();
    }

    public void setMaxStringLength(int length) {
        this.data.textfield.setMaxStringLength(length);
    }

    public int getMaxStringLength() {
        return this.data.textfield.getMaxStringLength();
    }

    public void setFocused(boolean isFocused) {
        this.data.textfield.setFocused(isFocused);
    }

    public boolean isFocused() {
        return this.data.textfield.isFocused();
    }

    public void setCursorPosition(int pos) {
        this.data.textfield.setCursorPosition(pos);
    }

    public void setSelectionEnd(int pos) {
        this.data.textfield.setSelectionPos(pos);
    }

    public int[] getSelectionPos() {
        return new int[] { this.data.textfield.getCursorPosition(), this.data.textfield.getSelectionEnd() };
    }

    public String getSelectedText() {
        return this.data.textfield.getSelectedText();
    }

    public void insertText(String text) {
        this.data.textfield.writeText(text);
    }

    public void setCursorPositionEnd() {
        this.data.textfield.setCursorPositionEnd();
    }

    private boolean enabled = true;
    public void setEnabled(boolean enabled) {
        this.data.textfield.setEnabled(enabled);
        this.enabled = enabled;
    }

    public void setVisible(boolean enabled) {
        this.data.textfield.setVisible(enabled);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isVisible() {
        return this.data.textfield.getVisible();
    }

    public void setValidator(Predicate<String> validator) {
        this.data.textfield.setValidator(validator::test);
    }

    @Override
    public int getWidth() {
        return this.data.textfield.getWidth();
    }

    @Override
    public int getHeight() {
        return this.data.size[1] - (this.data.drawBackground ? 8 : 0);
    }

    @SuppressWarnings("WeakerAccess")
    public static final class BakedData
    {
        public int[]        size;
        public int          color;
        public int          disabledColor;
        public int          placeholderColor;
        public boolean      canLoseFocus;
        public boolean      drawBackground;
        public boolean      shadow;
        public String       text;
        public String       placeholderText;
        public FontRenderer fontRenderer;

        public GuiTextField textfield;
    }

    private final class FontRendererTF
            extends FontRenderer
    {
        private final FontRenderer fontRenderer;

        private FontRendererTF(Minecraft mc, FontRenderer orig) {
            super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
            this.fontRenderer = orig;
        }

        public void onResourceManagerReload(IResourceManager resourceManager) {
            this.fontRenderer.onResourceManagerReload(resourceManager);
        }

        public int drawStringWithShadow(String text, float x, float y, int color) {
            return this.drawString(text, x, y, color, TextField.this.data.shadow);
        }

        public int drawString(String text, int x, int y, int color) {
            return this.fontRenderer.drawString(text, x, y, color);
        }

        public int drawString(String text, float x, float y, int color, boolean dropShadow) {
            return this.fontRenderer.drawString(text, x, y, color, dropShadow);
        }

        public int getStringWidth(String text) {
            return this.fontRenderer.getStringWidth(text);
        }

        public int getCharWidth(char character) {
            return this.fontRenderer.getCharWidth(character);
        }

        public String trimStringToWidth(String text, int width) {
            return this.fontRenderer.trimStringToWidth(text, width);
        }

        public String trimStringToWidth(String text, int width, boolean reverse) {
            return this.fontRenderer.trimStringToWidth(text, width, reverse);
        }

        public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
            this.fontRenderer.drawSplitString(str, x, y, wrapWidth, textColor);
        }

        public int getWordWrappedHeight(String str, int maxLength) {
            return this.fontRenderer.getWordWrappedHeight(str, maxLength);
        }

        public void setUnicodeFlag(boolean unicodeFlagIn) {
            this.fontRenderer.setUnicodeFlag(unicodeFlagIn);
        }

        public boolean getUnicodeFlag() {
            return this.fontRenderer.getUnicodeFlag();
        }

        public void setBidiFlag(boolean bidiFlagIn) {
            this.fontRenderer.setBidiFlag(bidiFlagIn);
        }

        public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
            return this.fontRenderer.listFormattedStringToWidth(str, wrapWidth);
        }

        public boolean getBidiFlag() {
            return this.fontRenderer.getBidiFlag();
        }

        public int getColorCode(char character) {
            return this.fontRenderer.getColorCode(character);
        }
    }
}
