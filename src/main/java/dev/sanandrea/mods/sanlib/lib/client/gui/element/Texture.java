package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@SuppressWarnings("unused")
@GuiElement.Resizable
public class Texture
        extends GuiElement
{
    @SuppressWarnings("java:S1192")
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("texture");

    public static final String JSON_IS_SPRITE = "isSprite";
    public static final String JSON_SCALE_X   = "scaleX";
    public static final String JSON_SCALE_Y   = "scaleY";
    public static final String JSON_COLOR     = "color";
    public static final String JSON_TEXTURE   = "texture";

    protected TextureData data;
    protected float       scaleX   = 1.0F;
    protected float       scaleY   = 1.0F;
    protected ColorData   color    = ColorData.WHITE;
    protected boolean     asSprite = false;

    public Texture(String id) {
        super(id);
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        boolean isDisabled = !this.isEnabled();
        boolean isHovering = this.isHovering();

        ColorObj               colorObj = ColorObj.fromARGB(this.color.getColor(isDisabled, isHovering));
        TextureData.TextureDef texture  = this.data.getTexture(isDisabled, isHovering);

        RenderSystem.setShaderColor(colorObj.fRed(), colorObj.fGreen(), colorObj.fBlue(), colorObj.fAlpha());
        drawRect(gui, graphics, x, y, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.data = TextureData.fromJson(guiDef, data.get(JSON_TEXTURE));
        this.asSprite = JsonUtils.getBoolVal(data.get(JSON_IS_SPRITE), false);
        this.scaleX = JsonUtils.getFloatVal(data.get(JSON_SCALE_X), 1.0F);
        this.scaleY = JsonUtils.getFloatVal(data.get(JSON_SCALE_Y), 1.0F);
        this.color = ColorData.loadColor(data.get(JSON_COLOR), false, ColorData.WHITE.color());
    }

    @SuppressWarnings("unused")
    protected void drawRect(IGui gui, GuiGraphics graphics, int x, int y, TextureData.TextureDef texture) {
        if( this.asSprite ) {
            graphics.blitSprite(texture.location(), x, y, this.getWidth(), this.getHeight());
        } else {
            graphics.blit(texture.location(), x, y, texture.posU(), texture.posV(), this.getWidth(), this.getHeight(),
                          this.data.textureWidth(), this.data.textureHeight());
        }
    }

    //region Getters & Setters

    public float getScaleX() {return this.scaleX;}

    public float getScaleY() {return this.scaleY;}

    public ColorData getColor() {return this.color;}

    public void setScaleX(float scaleX) {this.scaleX = scaleX;}

    public void setScaleY(float scaleY) {this.scaleY = scaleY;}

    public void setColor(int color) {this.color = new ColorData(color);}

    public void setColor(ColorData color) {this.color = color;}

    public TextureData getData() {return this.data;}

//endregion

    public static class Builder<T extends Texture>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {super(elem);}

        public Builder<T> withData(TextureData data) {
            this.elem.data = data;

            return this;
        }

        public Builder<T> withScale(float x, float y) {
            this.elem.scaleX = x;
            this.elem.scaleY = y;

            return this;
        }

        public Builder<T> withColor(ColorData color) {
            this.elem.color = color;

            return this;
        }

        public Builder<T> withColor(int color) {
            return this.withColor(new ColorData(color));
        }

        public Builder<T> asSprite() {
            this.elem.asSprite = true;

            return this;
        }

        public static Builder<Texture> createTexture() {
            return createTexture(UUID.randomUUID().toString());
        }

        public static Builder<Texture> createTexture(String id) {
            return new Builder<>(new Texture(id));
        }
    }
}
