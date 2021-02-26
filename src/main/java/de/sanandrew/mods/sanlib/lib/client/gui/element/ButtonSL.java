////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.Range;

import java.util.Map;

@SuppressWarnings({ "WeakerAccess", "Duplicates", "unused" })
public class ButtonSL
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("button");

    public static final String LABEL = "label";

    public ResourceLocation texture;
    public int[]            size;
    public int[]            textureSize;
    public int[]            uvEnabled;
    public int[]            uvHover;
    public int[]            uvDisabled;
    public int[]            uvSize;
    public int              centralTextureWidth;
    public int              centralTextureHeight;
    public Button.IPressable buttonFunction = btn -> {};

    protected Button buttonDelegate;

    protected double     currMouseX;
    protected double     currMouseY;
    protected boolean isCurrHovering;

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        JsonElement lbl = data.get("label");
        GuiElementInst lblInst = null;
        if( lbl != null ) {
            lblInst = JsonUtils.GSON.fromJson(lbl, GuiElementInst.class).initialize(gui);
        } else {
            lbl = data.get("labelText");
            if( lbl != null ) {
                int[] lblPos = new int[] { this.size[0] / 2, this.size[1] / 2 };
                if( lbl.isJsonPrimitive() ) {
                    JsonObject colors = new JsonObject();
                    colors.addProperty("default", "0xFFFFFFFF");
                    colors.addProperty("hover", "0xFFFFFFA0");
                    colors.addProperty("disabled", "0xFFA0A0A0");

                    JsonObject lblData = new JsonObject();
                    JsonUtils.addJsonProperty(lblData, "text", lbl.getAsString());
                    lblData.add("color", colors);

                    lblInst = new GuiElementInst(lblPos, new Text(), lblData).initialize(gui);
                } else {
                    lblInst = new GuiElementInst(lblPos, new Text(), lbl.getAsJsonObject()).initialize(gui);
                }
            }
        }

        if( lblInst != null ) {
            lblInst.alignment = JsonUtils.getStringArray(data.get("alignLabel"), new String[] { "center", "center" });

            listToBuild.put(LABEL, lblInst);
        }
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( JsonUtils.getBoolVal(data.get("useVanillaTexture"), true) ) {
            this.texture = new ResourceLocation(JsonUtils.getStringVal(data.get("texture"), "textures/gui/widgets.png"));
        } else {
            this.texture = gui.getDefinition().getTexture(data.get("texture"));
        }
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.uvSize = JsonUtils.getIntArray(data.get("uvSize"), new int[] { 200, 20 }, Range.is(2));
        this.uvEnabled = JsonUtils.getIntArray(data.get("uvEnabled"), new int[] { 0, 66 }, Range.is(2));
        this.uvHover = JsonUtils.getIntArray(data.get("uvHover"), new int[] { this.uvEnabled[0], this.uvEnabled[1] + this.uvSize[1] }, Range.is(2));
        this.uvDisabled = JsonUtils.getIntArray(data.get("uvDisabled"), new int[] { this.uvEnabled[0], this.uvEnabled[1] - this.uvSize[1] }, Range.is(2));
        this.centralTextureWidth = JsonUtils.getIntVal(data.get("centralTextureWidth"), 190);
        this.centralTextureHeight = JsonUtils.getIntVal(data.get("centralTextureHeight"), 14);
        this.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] { 256, 256 }, Range.is(2));

        this.buttonDelegate = new Button(0, 0, 0, 0, new StringTextComponent(""), btn -> buttonFunction.onPress(btn));

        super.bakeData(gui, data, inst);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        this.currMouseX = mouseX;
        this.currMouseY = mouseY;

        this.isCurrHovering = isHovering(gui, x, y, mouseX, mouseY);
        boolean isActive = this.isActive();

        gui.get().getMinecraft().getTextureManager().bindTexture(this.texture);
        stack.push();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
        stack.translate(x, y, 0.0D);
        drawRect(stack, isActive, this.isCurrHovering);
        stack.pop();

        GuiElementInst label = this.getChild(LABEL);
        if( label != null ) {
            IGuiElement e = label.get();
            if( e instanceof Text ) {
                ((Text) e).setColor(isActive ? this.isCurrHovering ? "hover"
                                                                   : "default"
                                             : "disabled");
            }

            GuiDefinition.renderElement(gui, stack, x + label.pos[0], y + label.pos[1], mouseX, mouseY, partTicks, label);
        }
    }

    public void setFunction(Button.IPressable func) {
        this.buttonFunction = func;
    }

    public boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY) {
        return IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int mouseButton) {
        if( mouseButton == 0 && this.isActive() && this.isCurrHovering ) {
            Screen gs  = gui.get();
            Button btn = this.buttonDelegate;

            btn.playDownSound(gs.getMinecraft().getSoundHandler());
            this.buttonFunction.onPress(this.buttonDelegate);

            return true;
        }

        return super.mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    protected void drawRect(MatrixStack stack, boolean enabled, boolean hovered) {
        int[] uv = enabled
                   ? (hovered ? this.uvHover : this.uvEnabled)
                   : this.uvDisabled;

        if( this.uvSize[0] == this.size[0] && this.uvSize[1] == this.size[1] ) {
            AbstractGui.blit(stack, 0, 0, uv[0], uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        } else {
            int cornerWidth = (this.uvSize[0] - this.centralTextureWidth) / 2;
            int cornerHeight = (this.uvSize[1] - this.centralTextureHeight) / 2;

            AbstractGui.blit(stack, 0, 0,
                                                    uv[0], uv[1],
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            AbstractGui.blit(stack, 0, this.size[1] - cornerHeight,
                                                    uv[0], uv[1] + this.uvSize[1] - cornerHeight,
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            AbstractGui.blit(stack, this.size[0] - cornerWidth, 0,
                                                    uv[0] + this.uvSize[0] - cornerWidth, uv[1],
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            AbstractGui.blit(stack, this.size[0] - cornerWidth, this.size[1] - cornerHeight,
                                                    uv[0] + this.uvSize[0] - cornerWidth, uv[1] + this.uvSize[1] - cornerHeight,
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);

            drawTiledTexture(stack, 0, cornerHeight,
                             uv[0], uv[1] + cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, cornerWidth, 0,
                             uv[0] + cornerWidth, uv[1],
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, this.size[0] - cornerWidth, cornerHeight,
                             uv[0] + this.uvSize[0] - cornerWidth, uv[1] + cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, cornerWidth, this.size[1] - cornerHeight,
                             uv[0] + cornerWidth, uv[1] + this.uvSize[1] - cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);

            drawTiledTexture(stack, cornerWidth, cornerHeight,
                             uv[0] + cornerWidth, uv[1] + cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, this.uvSize[1] - cornerHeight * 2,
                             this.size[0] - cornerWidth * 2, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
        }
    }

    protected static void drawTiledTexture(MatrixStack stack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, int sheetWidth, int sheetHeight) {
        int txWidth = width;
        int txHeight = height;
        int uvX = Math.min(uWidth, width);
        int uvY = Math.min(vHeight, height);
        while( uvX > 0 ) {
            while( uvY > 0 ) {
                AbstractGui.blit(stack, x + txWidth - width, y + txHeight - height, u, v, uvX, uvY, sheetWidth, sheetHeight);

                height -= vHeight;
                uvY = Math.min(vHeight, height);
            }

            height = txHeight;
            uvY = Math.min(vHeight, height);

            width -= uWidth;
            uvX = Math.min(uWidth, width);
        }
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public void setActive(boolean active) {
        this.buttonDelegate.active = active;
    }

    public boolean isActive() {
        return this.buttonDelegate.active;
    }
}
