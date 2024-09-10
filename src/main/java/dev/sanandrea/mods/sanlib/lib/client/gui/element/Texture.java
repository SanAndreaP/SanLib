package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@SuppressWarnings("unused")
@GuiElement.Resizable
public class Texture
        extends GuiElement
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("texture");

    protected TextureData data;
    protected TextureData dataHover;
    protected TextureData dataDisabled;
    protected float       scaleX   = 1.0F;
    protected float       scaleY   = 1.0F;
    protected ColorObj    color    = ColorObj.WHITE;
    protected boolean     asSprite = false;

    public Texture(String id) {
        super(id);
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        boolean enabled    = this.dataDisabled == null || this.isEnabled();
        boolean isHovering = this.dataHover != null && enabled && this.isHovering();

        RenderSystem.setShaderColor(this.color.fRed(), this.color.fGreen(), this.color.fBlue(), this.color.fAlpha());
        drawRect(gui, graphics, x, y, enabled, isHovering);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.data = TextureData.parse(guiDef, data);
        this.dataHover = MiscUtils.apply(data.getAsJsonObject("hover"), d -> TextureData.parse(d, this.data));
        this.dataDisabled = MiscUtils.apply(data.getAsJsonObject("disabled"), d -> TextureData.parse(d, this.data));

        this.asSprite = JsonUtils.getBoolVal(data.get("isSprite"), false);

        this.scaleX = JsonUtils.getFloatVal(data.get("scaleX"), 1.0F);
        this.scaleY = JsonUtils.getFloatVal(data.get("scaleY"), 1.0F);
        this.color = data.has("color") ? new ColorObj(ColorDef.loadColor(data.get("color"), false, null).color) : ColorObj.WHITE;
    }

    @SuppressWarnings("unused")
    protected void drawRect(IGui gui, GuiGraphics graphics, int x, int y, boolean enabled, boolean isHovering) {
        TextureData data = this.getCurrentData(enabled, isHovering);

        if( this.asSprite ) {
            graphics.blitSprite(data.texture, x, y, this.getWidth(), this.getHeight());
        } else {
            graphics.blit(data.texture, x, y, data.posU, data.posV, this.getWidth(), this.getHeight(), data.textureWidth, data.textureHeight);
        }
    }

    //region Getters & Setters

    public float getScaleX() {return this.scaleX;}

    public float getScaleY() {return this.scaleY;}

    public int getColor() {return this.color.getColorInt();}

    public void setScaleX(float scaleX) {this.scaleX = scaleX;}

    public void setScaleY(float scaleY) {this.scaleY = scaleY;}

    public void setColor(int color) {this.color = new ColorObj(color);}

    public TextureData getData() {return this.data;}

    public TextureData getHoverData() {return this.dataHover;}

    public TextureData getDisabledData() {return this.dataDisabled;}

    protected TextureData getCurrentData(boolean enabled, boolean isHovering) {
        TextureData data = isHovering && this.dataHover != null ? this.dataHover : this.data;
        data = enabled || this.dataDisabled == null ? data : this.dataDisabled;

        return data;
    }
//endregion

    public static class Builder<T extends Texture>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {super(elem);}

        public Builder<T> withData(TextureData data) {
            this.elem.data = data;

            return this;
        }

        public Builder<T> withHoverData(TextureData data) {
            this.elem.dataHover = data;

            return this;
        }

        public Builder<T> withDisabledData(TextureData data) {
            this.elem.dataDisabled = data;

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

    public record TextureData(ResourceLocation texture, int posU, int posV, int textureWidth, int textureHeight)
    {
        public TextureData(ResourceLocation texture) {this(texture, 0, 0, 256, 256);}

        public TextureData(ResourceLocation texture, int posU, int posV) {this(texture, posU, posV, 256, 256);}

        public TextureData changeTexture(ResourceLocation texture) {return new TextureData(texture, this.posU, this.posV, this.textureWidth, this.textureHeight);}

        public TextureData changePosU(int posU) {return new TextureData(this.texture, posU, this.posV, this.textureWidth, this.textureHeight);}

        public TextureData changePosV(int posV) {return new TextureData(this.texture, this.posU, posV, this.textureWidth, this.textureHeight);}

        public TextureData changeTextureWidth(int textureWidth) {return new TextureData(this.texture, this.posU, this.posV, textureWidth, this.textureHeight);}

        public TextureData changeTextureHeight(int textureHeight) {return new TextureData(this.texture, this.posU, this.posV, this.textureWidth, textureHeight);}

        public static TextureData parse(GuiDefinition guiDef, JsonObject data) {
            return new TextureData(guiDef.getTexture(data.get("texture")),
                                   JsonUtils.getIntVal(data.get("u"), 0),
                                   JsonUtils.getIntVal(data.get("v"), 0),
                                   JsonUtils.getIntVal(data.get("textureWidth"), 256),
                                   JsonUtils.getIntVal(data.get("textureHeight"), 256));
        }

        public static TextureData parse(JsonObject data, TextureData base) {
            return new TextureData(JsonUtils.getLocation(data.get("texture"), base.texture),
                                   JsonUtils.getIntVal(data.get("u"), base.posU),
                                   JsonUtils.getIntVal(data.get("v"), base.posV),
                                   JsonUtils.getIntVal(data.get("textureWidth"), base.textureWidth),
                                   JsonUtils.getIntVal(data.get("textureHeight"), base.textureHeight));
        }
    }
}
