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

@SuppressWarnings("unused")
@GuiElement.Resizable
public class Texture
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("texture");

    protected ResourceLocation textureLocation;
    protected int              textureWidth = 256;
    protected int              textureHeight = 256;
    protected int              posU = 0;
    protected int              posV = 0;
    protected float            scaleX = 1.0F;
    protected float            scaleY = 1.0F;
    protected ColorObj         color = ColorObj.WHITE;

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
        this.color = data.has("color") ? new ColorObj(ColorDef.loadColor(data.get("color"), false, null).color) : ColorObj.WHITE;
    }

    @SuppressWarnings("unused")
    protected void drawRect(IGui gui, MatrixStack stack) {
        AbstractGui.blit(stack, 0, 0, this.posU, this.posV, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
    }

//region Getters & Setters
    public int   getTextureWidth()  { return this.textureWidth; }
    public int   getTextureHeight() { return this.textureHeight; }
    public int   getPosU()          { return this.posU; }
    public int   getPosV()          { return this.posV; }
    public float getScaleX()        { return this.scaleX; }
    public float getScaleY()        { return this.scaleY; }
    public int   getColor()         { return this.color.getColorInt(); }

    public void setTextureWidth(int textureWidth)   { this.textureWidth = textureWidth; }
    public void setTextureHeight(int textureHeight) { this.textureHeight = textureHeight; }
    public void setPosU(int posU)                   { this.posU = posU; }
    public void setPosV(int posV)                   { this.posV = posV; }
    public void setScaleX(float scaleX)             { this.scaleX = scaleX; }
    public void setScaleY(float scaleY)             { this.scaleY = scaleY; }
    public void setColor(int color)                 { this.color = new ColorObj(color); }
//endregion

    public static class Builder<T extends Texture>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) { super(elem); }

        public Builder<T> withLocation(ResourceLocation texture) {
            this.elem.textureLocation = texture;

            return this;
        }

        public Builder<T> withTextureSize(int width, int height) {
            this.elem.textureWidth = width;
            this.elem.textureHeight = height;

            return this;
        }

        public Builder<T> withPosUV(int u, int v) {
            this.elem.posU = u;
            this.elem.posV = v;

            return this;
        }

        public Builder<T> withScale(float x, float y) {
            this.elem.scaleX = x;
            this.elem.scaleY = y;

            return this;
        }

        public Builder<T> withColor(ColorObj color) {
            this.elem.color = color;

            return this;
        }

        public Builder<T> withColor(int color) {
            return this.withColor(new ColorObj(color));
        }

        public static Builder<Texture> create() {
            return new Builder<>(new Texture());
        }
    }
}
