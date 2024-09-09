package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@SuppressWarnings("unused")
@GuiElement.Resizable
public class TiledTexture
        extends Texture
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("tiled_texture");

    protected int texTileWidth;
    protected int texTileHeight;
    protected int centralWidth;
    protected int centralHeight;

    public TiledTexture(String id) {
        super(id);
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        super.fromJson(gui, guiDef, data);

        this.texTileWidth = JsonUtils.getIntVal(data.get("tileTextureWidth"), this.width);
        this.texTileHeight = JsonUtils.getIntVal(data.get("tileTextureHeight"), this.height);
        this.centralWidth = JsonUtils.getIntVal(data.get("centralWidth"));
        this.centralHeight = JsonUtils.getIntVal(data.get("centralHeight"));
    }

    @Override
    protected void drawRect(IGui gui, GuiGraphics graphics, int x, int y, boolean enabled, boolean isHovering) {
        if( this.texTileWidth == this.width && this.texTileHeight == this.height ) {
            super.drawRect(gui, graphics, x, y, enabled, isHovering);
        } else {
            int u = this.getCurrentU(enabled, isHovering);
            int v = this.getCurrentV(enabled, isHovering);

            int cornerWidth  = (this.texTileWidth - this.centralWidth) / 2;
            int cornerHeight = (this.texTileHeight - this.centralHeight) / 2;

            // draw corner pieces
            if( cornerWidth != 0 && cornerHeight != 0 ) {
                this.drawCorner(graphics, cornerWidth, cornerHeight, u, v);
            }

            // draw edge pieces
            if( cornerWidth != 0 ) {
                this.drawVerticalEdges(graphics, cornerWidth, cornerHeight, u, v);
            }
            if( cornerHeight != 0 ) {
                this.drawHorizontalEdges(graphics, cornerWidth, cornerHeight, u, v);
            }

            // draw centerpiece
            this.drawTiledTexture(graphics, cornerWidth, cornerHeight,
                                  u + (float) cornerWidth, v + (float) cornerHeight,
                                  this.texTileWidth - cornerWidth * 2, this.texTileHeight - cornerHeight * 2,
                                  this.width - cornerWidth * 2, this.height - cornerHeight * 2);
        }
    }

    protected void drawCorner(GuiGraphics graphics, int cornerWidth, int cornerHeight, int u, int v) {
        graphics.blit(this.textureLocation, 0, 0,
                         u, v,
                         cornerWidth, cornerHeight,
                         this.textureWidth, this.textureHeight);
        graphics.blit(this.textureLocation, 0, this.height - cornerHeight,
                         u, v + this.texTileHeight - (float) cornerHeight,
                         cornerWidth, cornerHeight,
                         this.textureWidth, this.textureHeight);
        graphics.blit(this.textureLocation, this.width - cornerWidth, 0,
                         u + this.texTileWidth - (float) cornerWidth, v,
                         cornerWidth, cornerHeight,
                         this.textureWidth, this.textureHeight);
        graphics.blit(this.textureLocation, this.width - cornerWidth, this.height - cornerHeight,
                         u + this.texTileWidth - (float) cornerWidth, v + this.texTileHeight - (float) cornerHeight,
                         cornerWidth, cornerHeight,
                         this.textureWidth, this.textureHeight);
    }

    protected void drawHorizontalEdges(GuiGraphics graphics, int cornerWidth, int cornerHeight, int u, int v) {
        this.drawTiledTexture(graphics, cornerWidth, 0,
                              u + (float) cornerWidth, v,
                              this.texTileWidth - cornerWidth * 2, cornerHeight,
                              this.width - cornerWidth * 2, cornerHeight);
        this.drawTiledTexture(graphics, cornerWidth, this.height - cornerHeight,
                              u + (float) cornerWidth, v + this.texTileHeight - (float) cornerHeight,
                              this.texTileWidth - cornerWidth * 2, cornerHeight,
                              this.width - cornerWidth * 2, cornerHeight);
    }

    protected void drawVerticalEdges(GuiGraphics graphics, int cornerWidth, int cornerHeight, int u, int v) {
        this.drawTiledTexture(graphics, 0, cornerHeight,
                              u, v + (float) cornerHeight,
                              cornerWidth, this.texTileHeight - cornerHeight * 2,
                              cornerWidth, this.height - cornerHeight * 2);
        this.drawTiledTexture(graphics, this.width - cornerWidth, cornerHeight,
                              u + this.texTileWidth - (float) cornerWidth, v + (float) cornerHeight,
                              cornerWidth, this.texTileHeight - cornerHeight * 2,
                              cornerWidth, this.height - cornerHeight * 2);
    }

    /**
     * Draws a cut piece of the button texture.
     *
     * @param x the left texture position
     * @param y the top texture position
     * @param u the left coordinate on the texture image
     * @param v the top coordinate on the texture image
     * @param uWidth the width of the rendered texture on the texture image
     * @param vHeight the height of the rendered texture on the texture image
     * @param width the width of the rendered texture
     * @param height the height of the rendered texture
     */
    @SuppressWarnings("java:S107")
    protected void drawTiledTexture(GuiGraphics graphics, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height)
    {
        int txWidth = width;
        int txHeight = height;
        int uvX = Math.min(uWidth, width);
        int uvY = Math.min(vHeight, height);
        while( uvX > 0 ) {
            while( uvY > 0 ) {
                graphics.blit(this.textureLocation, x + txWidth - width, y + txHeight - height, u, v, uvX, uvY, this.textureWidth, this.textureHeight);

                height -= vHeight;
                uvY = Math.min(vHeight, height);
            }

            height = txHeight;
            uvY = Math.min(vHeight, height);

            width -= uWidth;
            uvX = Math.min(uWidth, width);
        }
    }

//region Getters & Setters
    public int getTileTextureWidth() { return this.texTileWidth; }
    public int getTileTextureHeight() { return this.texTileHeight; }
    public int getCentralWidth() { return this.centralWidth; }
    public int getCentralHeight() { return this.centralHeight; }

    public void setTileTextureWidth(int width) { this.texTileWidth = width; }
    public void setTileTextureHeight(int height) { this.texTileHeight = height; }
    public void setCentralWidth(int width) { this.centralWidth = width; }
    public void setCentralHeight(int height) { this.centralHeight = height; }
//endregion
    
    public static class Builder<T extends TiledTexture>
            extends Texture.Builder<T>
    {
        protected Builder(T elem) { super(elem); }

        public Builder<T> withCentralTextureSize(int width, int height) {
            this.elem.centralWidth = width;
            this.elem.centralHeight = height;
            
            return this;
        }
        
        public Builder<T> withTileTextureSize(int width, int height) {
            this.elem.texTileWidth = width;
            this.elem.texTileHeight = height;

            return this;
        }

        public static Builder<TiledTexture> createTiledTexture() {
            return createTiledTexture(UUID.randomUUID().toString());
        }

        public static Builder<TiledTexture> createTiledTexture(String id) {
            return new Builder<>(new TiledTexture(id));
        }
    }
}
