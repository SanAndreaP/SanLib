package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

@GuiElement.Resizable
public class Texture
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("texture");

    protected ResourceLocation textureLocation;
    protected int              textureWidth;
    protected int              textureHeight;
    protected int              posU;
    protected int              posV;
    protected float            scaleX;
    protected float            scaleY;
    protected ColorObj         color;

    @Override
    public void update(IGui gui, boolean updateState) {
        // no-op
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) {
        gui.get().getMinecraft().getTextureManager().bind(this.textureLocation);
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        matrixStack.translate(x, y, 0.0D);
        matrixStack.scale(this.scaleX, this.scaleY, 1.0F);
        RenderSystem.color4f(this.color.fRed(), this.color.fGreen(), this.color.fBlue(), this.color.fAlpha());
        drawRect(gui, matrixStack);
        matrixStack.popPose();
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.textureLocation = guiDef.getTexture(data.get("texture"));
        this.textureWidth = JsonUtils.getIntVal(data.get("textureWidth"), 256);
        this.textureHeight = JsonUtils.getIntVal(data.get("textureHeight"), 256);
        this.posU = JsonUtils.getIntVal(data.get("u"), 0);
        this.posV = JsonUtils.getIntVal(data.get("v"), 0);
        this.scaleX = JsonUtils.getFloatVal(data.get("scaleX"), 1.0F);
        this.scaleY = JsonUtils.getFloatVal(data.get("scaleY"), 1.0F);
        this.color = new ColorObj(data.has("color") ? ColorDef.loadColor(data.get("color"), false).color : 0xFFFFFFFF);
    }

    @SuppressWarnings("unused")
    protected void drawRect(IGui gui, MatrixStack stack) {
        AbstractGui.blit(stack, 0, 0, this.posU, this.posV, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
    }
}
