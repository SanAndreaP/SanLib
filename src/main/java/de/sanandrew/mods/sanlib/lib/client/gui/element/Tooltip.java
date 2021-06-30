////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Tooltip
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("tooltip");

    public static final String CONTENT = "content";

    public int[] size;
    public int backgroundColor;
    public int borderTopColor;
    public int borderBottomColor;
    public int[] padding;

    protected GuiElementInst visibleFor;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.backgroundColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("backgroundColor"), "0xF0100010"));
        this.borderTopColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderTopColor"), "0x505000FF"));
        this.borderBottomColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderBottomColor"), "0x5028007F"));
        this.setPadding(JsonUtils.getIntArray(data.get("padding"), new int[0], Range.between(0, 4)));

        this.visibleFor = gui.getDefinition().getElementById(JsonUtils.getStringVal(data.get("for"), ""));

        super.bakeData(gui, data, inst);
    }

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        listToBuild.put(CONTENT, this.getContent(gui, data));
    }

    public GuiElementInst getContent(IGui gui, JsonObject data) {
        GuiElementInst lbl;
        if( data.has("content") ) {
            lbl = JsonUtils.GSON.fromJson(data.get("content"), GuiElementInst.class);
        } else if( data.has("text") ) {
            JsonObject cntData = new JsonObject();
            JsonUtils.addJsonProperty(cntData, "text", JsonUtils.getStringVal(data.get("text")));
            JsonUtils.addJsonProperty(cntData, "color", "0xFFFFFFFF");

            lbl = new GuiElementInst(new Text(), cntData);
        } else {
            throw new JsonParseException("No data property called \"content\" or \"text\" has been found.");
        }

        return lbl.initialize(gui);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]) ) {
            double locMouseX = mouseX - gui.getScreenPosX();
            double locMouseY = mouseY - gui.getScreenPosY();

            GuiElementInst inst = this.getChild(CONTENT);
            IGuiElement contentElem = inst.get();

            contentElem.renderTick(gui, stack, partTicks, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, inst.data);

            int width = contentElem.getWidth() + this.padding[1] + this.padding[3];
            int height = contentElem.getHeight() + this.padding[0] + this.padding[2];
            int xPos = (int) locMouseX + 12;
            int yPos = (int) locMouseY - 12;

            if( mouseX + width + 16 > gui.get().width ) {
                xPos -= width + 28;
            }

            stack.pushPose();
            stack.translate(0.0D, 0.0D, 400.0D);
            AbstractGui.fill(stack, xPos - 3, yPos - 4, xPos + width + 3, yPos - 3, this.backgroundColor);
            AbstractGui.fill(stack, xPos - 3,         yPos + height + 3, xPos + width + 3, yPos + height + 4, this.backgroundColor);
            AbstractGui.fill(stack, xPos - 3,         yPos - 3,          xPos + width + 3, yPos + height + 3, this.backgroundColor);
            AbstractGui.fill(stack, xPos - 4,         yPos - 3,          xPos - 3,         yPos + height + 3, this.backgroundColor);
            AbstractGui.fill(stack, xPos + width + 3, yPos - 3,          xPos + width + 4, yPos + height + 3, this.backgroundColor);

            GuiUtils.drawGradient(stack, xPos - 3, yPos - 2, 1, height + 4, this.borderTopColor, this.borderBottomColor, true);
            GuiUtils.drawGradient(stack, xPos + width + 2, yPos - 2, 1, height + 4, this.borderTopColor, this.borderBottomColor, true);
            AbstractGui.fill(stack, xPos - 3, yPos - 3,          xPos + width + 3, yPos - 2,          this.borderTopColor);
            AbstractGui.fill(stack, xPos - 3, yPos + height + 2, xPos + width + 3, yPos + height + 3, this.borderBottomColor);

            GuiDefinition.renderElement(gui, stack, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, partTicks, inst, false);

            stack.popPose();
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

    @Override
    public boolean isVisible() {
        return (this.visibleFor == null || this.visibleFor.isVisible());
    }

    public void setPadding(int[] padding) {
        if( padding == null || padding.length == 0 ) {
            this.padding = new int[] { 0, 0, 0, 0 };
            return;
        }

        switch( padding.length ) {
            case 1: this.padding = new int[] { padding[0], padding[0], padding[0], padding[0] }; break;
            case 2: this.padding = new int[] { padding[0], padding[1], padding[0], padding[1] }; break;
            case 3: this.padding = new int[] { padding[0], padding[1], padding[2], padding[1] }; break;
            case 4: this.padding = new int[] { padding[0], padding[1], padding[2], padding[3] }; break;
        }
    }
}
