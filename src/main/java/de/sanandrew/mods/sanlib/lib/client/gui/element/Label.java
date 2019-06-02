package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.Range;

@SuppressWarnings("WeakerAccess")
public abstract class Label
        implements IGuiElement
{
    public BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.textRenderer = getTextElement(gui, data.get("textElement").getAsJsonObject());
        }
    }

    public abstract Text getTextElement(IGui gui, JsonObject data);

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        int screenX = gui.getScreenPosX();
        int screenY = gui.getScreenPosY();
        int absX = x + screenX;
        int absY = y + screenY;
        if( mouseX >= absX && mouseX < absX + this.data.size[0] && mouseY >= absY && mouseY < absY + this.data.size[1] ) {
            int textWidth = this.data.textRenderer.getTextWidth(gui);
            int xPos = mouseX - screenX + 12;
            int yPos = mouseY - screenY - 14;
            int height = this.data.textRenderer.getHeight();

            if( screenX + xPos + textWidth + 4 > gui.get().width ) {
                xPos -= textWidth + 28;
            }

            int bkgColor = 0xF0100010;
            int lightBg = 0x505000FF;
            int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

            GlStateManager.disableDepth();
            Gui.drawRect(xPos - 3,             yPos - 4,          xPos + textWidth + 3, yPos - 3,          bkgColor);
            Gui.drawRect(xPos - 3,             yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor);
            Gui.drawRect(xPos - 3,             yPos - 3,          xPos + textWidth + 3, yPos + height + 3, bkgColor);
            Gui.drawRect(xPos - 4,             yPos - 3,          xPos - 3,             yPos + height + 3, bkgColor);
            Gui.drawRect(xPos + textWidth + 3, yPos - 3,          xPos + textWidth + 4, yPos + height + 3, bkgColor);

            GuiUtils.drawGradientRect(xPos - 3,             yPos - 3 + 1,      1, height + 4, lightBg, darkBg, true);
            GuiUtils.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1,      1, height + 4, lightBg, darkBg, true);
            Gui.drawRect(xPos - 3,             yPos - 3,          xPos + textWidth + 3, yPos - 2,         lightBg);
            Gui.drawRect(xPos - 3,             yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg);

            GlStateManager.pushMatrix();
            GlStateManager.translate(xPos, yPos, 0.0F);

            this.data.textRenderer.render(gui, partTicks, 0, 1, mouseX, mouseY, data);

            GlStateManager.enableDepth();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    static final class BakedData
    {
        public int[] size;
        public Text textRenderer;
    }
}
