/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGuiElement;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A clickable button element.<br>
 * <br>
 * <tt>"data"</tt> JSON format
 * <pre>
 *     {
 *         "size": [width:int, height:int],
 *         ("useVanillaTexture": b:boolean,)                  // default: true
 *         ("texture": location:string/resourcelocation,)     // only used if "useVanillaTexture" is false
 *         ("textureSize": [width:int, height:int],)          // default: [256, 256]
 *         ("uvSize": [width:int, height:int],)               // default: [200, 20]
 *         ("uvEnabled": [x:int, y:int],)                     // default: [0, 66]
 *         ("uvHover": [x:int, y:int],)                       // default: [uvEnabled.x, uvEnabled.y + uvSize.height]
 *         ("uvDisabled": [x:int, y:int],)                    // default: [uvEnabled.x, uvEnabled.y - uvSize.height]
 *         ("centralTextureSize": [width:int, height:int],)   // default: [190, 14]
 *         ("labelAlignment": [horiz:string(, vert:string)],) // default: ["center", "center"]
 *         ("label": string|JSON)                             // default: null
 *     }
 * </pre>
 *
 * <p>Note 1: properties in brackets (like <tt>("key": value)</tt>) are optional.</p>
 * <p>Note 2: <tt>labelAlignment</tt> uses {@link dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst.Justify} constant names as values.</p>
 * <p>Note 3: <tt>label</tt> can be either of type {@link String} or a {@link JsonObject}.<br>
 *            The <tt>String</tt> version will be rendered as simple text (translation keys supported).<br>
 *            With the <tt>JsonObject</tt> version, you can specify any type of element, be it text, images, etc.
 *            See {@link GuiElementInst} for information about the format.</p>
 */
@SuppressWarnings("unused")
public class ButtonSL
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("button");

    public static final String LABEL = "label";

    protected ResourceLocation  texture;
    protected int[]             size;
    protected int[]             textureSize;
    protected int[]             uvEnabled;
    protected int[]             uvHover;
    protected int[]             uvDisabled;
    protected int[]             uvSize;
    protected int[]             centralTextureSize;
    @Nonnull
    protected Button.IPressable buttonFunction = btn -> {};
    @Nonnull
    protected HoverCallback     hoverFunction  = (gui, x, y, mouseX, mouseY) -> IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]);

    protected final Button buttonDelegate;

    protected double  currMouseX;
    protected double  currMouseY;
    protected boolean isCurrHovering;

    /**
     * Creates a new button element.<br>
     * <br>
     * A builder pattern approach with {@link Builder} is preferred to this constructor.
     * @param texture the location of the background texture
     * @param size the size of the button
     * @param textureSize the size of the background texture, in pixels
     * @param uvEnabled the texture coordinates used if this button is enabled, in pixels
     * @param uvHover the texture coordinates used if this button is hovered over, in pixels
     * @param uvDisabled the texture coordinates used if this button is disabled, in pixels
     * @param uvSize The size of the rendered area on the texture image, in pixels.
     *               If <tt>null</tt>, this value will be <tt>size</tt>.
     * @param centralTextureSize The size of the texture's central part, in pixels.
     *                           Only used if texture is stitched together.
     *                           Can be <tt>null</tt> if <tt>uvSize</tt> equals <tt>size</tt>.
     * @param label the central element being rendered as the button's label
     *
     * @implNote If <tt>uvSize</tt> is a different value than <tt>size</tt>, the background texture is
     * stitched together from pieces to fit into the button. See {@link #drawRect(MatrixStack, boolean, boolean)}
     * for more details.
     */
    @SuppressWarnings("java:S107")
    public ButtonSL(ResourceLocation texture, int[] size, int[] textureSize, int[] uvEnabled, int[] uvHover, int[] uvDisabled,
                    @Nullable int[] uvSize, @Nullable int[] centralTextureSize, @Nonnull GuiElementInst label)
    {
        this.texture = texture;
        this.size = size;
        this.textureSize = textureSize;
        this.uvEnabled = uvEnabled;
        this.uvHover = uvHover;
        this.uvDisabled = uvDisabled;
        this.uvSize = MiscUtils.get(uvSize, size);
        this.centralTextureSize = centralTextureSize;
        
        this.buttonDelegate = new Button(0, 0, 0, 0, StringTextComponent.EMPTY, btn -> this.buttonFunction.onPress(btn));

        this.put(LABEL, label);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.currMouseX = mouseX;
        this.currMouseY = mouseY;

        this.isCurrHovering = isHovering(gui, x, y, mouseX, mouseY);
        boolean isActive = this.isActive();

        gui.get().getMinecraft().getTextureManager().bind(this.texture);
        stack.pushPose();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        stack.translate(x, y, 0.0D);
        this.drawRect(stack, isActive, this.isCurrHovering);
        stack.popPose();

        GuiElementInst label = this.get(LABEL);
        if( label != null ) {
            IGuiElement e = label.get();
            if( e instanceof Text ) {
                Text et = (Text) e;
                if( isActive ) {
                    et.setColor(this.isCurrHovering ? "hover" : "default");
                } else {
                    et.setColor("disabled");
                }
            }

            GuiDefinition.renderElement(gui, stack, x + label.pos[0], y + label.pos[1], mouseX, mouseY, partTicks, label);
        }
    }

    /**
     * Sets the function to be called once a user clicks on this button.
     *
     * @param func the function to be called after clicking the button
     */
    public void setFunction(@Nonnull Button.IPressable func) {
        this.buttonFunction = func;
    }

    /**
     * Sets the function determining if this button is being hovered over by the user.
     *
     * @param func the function to be checked for hovering over the button
     */
    public void setHoverCallback(@Nonnull HoverCallback func) {
        this.hoverFunction = func;
    }

    /**
     * Returns wether this button is currently being hovered over by the user.
     *
     * @param gui the GUI instance the button is a child of
     * @param x the left button position
     * @param y the top button position
     * @param mouseX the left mouse position
     * @param mouseY the top mouse position
     * @return <tt>true</tt> if the mouse is hovering over the button, <tt>false</tt> otherwise
     */
    public boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY) {
        return this.hoverFunction.check(gui, x, y, mouseX, mouseY);
    }

    /**
     * Returns wether the specified {@link Button} instance is from this button.
     *
     * @param b the Minecraft <tt>Button</tt> instance to be checked
     * @return <tt>true</tt> if the instance is held by this button, <tt>false</tt> otherwise
     */
    public boolean isButton(Button b) {
        return b == this.buttonDelegate;
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        if( button == 0 && this.isActive() && this.isCurrHovering ) {
            Screen gs  = gui.get();

            this.buttonDelegate.playDownSound(gs.getMinecraft().getSoundManager());
            this.buttonFunction.onPress(this.buttonDelegate);

            return true;
        }

        return super.mouseClicked(gui, mouseX, mouseY, button);
    }

    /**
     * Draws the texture of this button.<br>
     * <br>
     * If <tt>uvSize</tt> is different from <tt>size</tt>, this grabs parts from the texture image
     * and stitches those parts together to fit within <tt>size</tt>.
     * Below is how the texture is grabbed from the image:
     * <pre>
     * uvSize (entire square):
     * -------------------------------------------------------------
     * | cornerPieceTL |       edgePieceTop       | cornerPieceTR  |
     * -------------------------------------------------------------
     * |               |                          |                |
     * | edgePieceLeft |       centerPiece        | edgePieceRight |
     * |               |   (centralTextureSize)   |                |
     * |               |                          |                |
     * -------------------------------------------------------------
     * | cornerPieceBL |     edgePieceBottom      | cornerPieceBR  |
     * -------------------------------------------------------------
     * </pre>
     *
     * @param stack the render transform stack
     * @param enabled wether this button is enabled
     * @param hovered wether this button is hovered over
     */
    protected void drawRect(MatrixStack stack, boolean enabled, boolean hovered) {
        int[] uv = this.uvDisabled;
        if( enabled ) {
            uv = hovered ? this.uvHover : this.uvEnabled;
        }

        if( this.uvSize[0] == this.size[0] && this.uvSize[1] == this.size[1] ) {
            AbstractGui.blit(stack, 0, 0, uv[0], uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        } else {
            int cornerWidth = (this.uvSize[0] - this.centralTextureSize[0]) / 2;
            int cornerHeight = (this.uvSize[1] - this.centralTextureSize[1]) / 2;

            // draw cornerpieces
            if( cornerWidth != 0 && cornerHeight != 0 ) {
                AbstractGui.blit(stack, 0, 0,
                                 uv[0], uv[1],
                                 cornerWidth, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
                AbstractGui.blit(stack, 0, this.size[1] - cornerHeight,
                                 uv[0], uv[1] + this.uvSize[1] - (float) cornerHeight,
                                 cornerWidth, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
                AbstractGui.blit(stack, this.size[0] - cornerWidth, 0,
                                 uv[0] + this.uvSize[0] - (float) cornerWidth, uv[1],
                                 cornerWidth, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
                AbstractGui.blit(stack, this.size[0] - cornerWidth, this.size[1] - cornerHeight,
                                 uv[0] + this.uvSize[0] - (float) cornerWidth, uv[1] + this.uvSize[1] - (float) cornerHeight,
                                 cornerWidth, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
            }

            // draw edgepieces
            if( cornerWidth != 0 ) {
                drawTiledTexture(stack, 0, cornerHeight,
                                 uv[0], uv[1] + (float) cornerHeight,
                                 cornerWidth, this.uvSize[1] - cornerHeight * 2,
                                 cornerWidth, this.size[1] - cornerHeight * 2,
                                 this.textureSize[0], this.textureSize[1]);
                drawTiledTexture(stack, this.size[0] - cornerWidth, cornerHeight,
                                 uv[0] + this.uvSize[0] - (float) cornerWidth, uv[1] + (float) cornerHeight,
                                 cornerWidth, this.uvSize[1] - cornerHeight * 2,
                                 cornerWidth, this.size[1] - cornerHeight * 2,
                                 this.textureSize[0], this.textureSize[1]);
            }
            if( cornerHeight != 0 ) {
                drawTiledTexture(stack, cornerWidth, 0,
                                 uv[0] + (float) cornerWidth, uv[1],
                                 this.uvSize[0] - cornerWidth * 2, cornerHeight,
                                 this.size[0] - cornerWidth * 2, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
                drawTiledTexture(stack, cornerWidth, this.size[1] - cornerHeight,
                                 uv[0] + (float) cornerWidth, uv[1] + this.uvSize[1] - (float) cornerHeight,
                                 this.uvSize[0] - cornerWidth * 2, cornerHeight,
                                 this.size[0] - cornerWidth * 2, cornerHeight,
                                 this.textureSize[0], this.textureSize[1]);
            }

            // draw centerpiece
            drawTiledTexture(stack, cornerWidth, cornerHeight,
                             uv[0] + (float) cornerWidth, uv[1] + (float) cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, this.uvSize[1] - cornerHeight * 2,
                             this.size[0] - cornerWidth * 2, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
        }
    }

    /**
     * Draws a cut piece of the button texture.
     *
     * @param stack the render transform stack
     * @param x the left texture position
     * @param y the top texture position
     * @param u the left coordinate on the texture image
     * @param v the top coordinate on the texture image
     * @param uWidth the width of the rendered texture on the texture image
     * @param vHeight the height of the rendered texture on the texture image
     * @param width the width of the rendered texture
     * @param height the height of the rendered texture
     * @param textureWidth the width of the texture image
     * @param textureHeight the height of the texture image
     */
    protected static void drawTiledTexture(MatrixStack stack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height,
                                           int textureWidth, int textureHeight)
    {
        int txWidth = width;
        int txHeight = height;
        int uvX = Math.min(uWidth, width);
        int uvY = Math.min(vHeight, height);
        while( uvX > 0 ) {
            while( uvY > 0 ) {
                AbstractGui.blit(stack, x + txWidth - width, y + txHeight - height, u, v, uvX, uvY, textureWidth, textureHeight);

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

    /**
     * Sets wether this button is active/enabled or not.
     * Inactive/disabled buttons are still rendered, but (usually) differently and don't respond to events.<br>
     *
     * @param active if <tt>true</tt>, this button should be active/enabled
     */
    public void setActive(boolean active) {
        this.buttonDelegate.active = active;
    }

    /**
     * Returns wether this button is active/enabled or not
     * @return <tt>true</tt> if this button is active/enabled, <tt>false</tt> otherwise.
     */
    public boolean isActive() {
        return this.buttonDelegate.active;
    }
    @Override
    public boolean isVisible() {
        return this.buttonDelegate != null && this.buttonDelegate.visible;
    }

    /**
     * Sets wether this button is visible or not.
     * Invisible buttons are not rendered and don't respond to events.<br>
     *
     * @param visible if <tt>true</tt>, this button is visible
     */
    public void setVisible(boolean visible) {
        this.buttonDelegate.visible = visible;
    }

    /**
     * Creates a new {@link ButtonSL} element from various parameters.
     * Setting {@link #size(int[]) size} is mandatory.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            implements IGuiElement.IBuilder<ButtonSL>
    {
        protected static final ResourceLocation VANILLA_TEXTURE = new ResourceLocation("textures/gui/widgets.png");

        protected int[] size;
        protected ResourceLocation texture;
        protected int[]            textureSize;
        protected int[]            uvSize;
        protected int[]            uvEnabled;
        protected int[]            uvHover;
        protected int[]            uvDisabled;
        protected int[]            centralTextureSize;
        protected String[]         labelAlignment;
        protected GuiElementInst   label;

        protected final BiFunction<IGui, JsonElement, GuiElementInst> labelLoader;

        /**
         * Creates this builder for building a new {@link ButtonSL}.
         * This uses the default {@link #loadLabel(IGui, JsonElement)} method to load the label from JSON.
         */
        public Builder() {
            this.labelLoader = this::loadLabel;
        }

        /**
         * Creates this builder for building a new {@link ButtonSL}.
         * This uses the specified method to load the label from JSON.
         *
         * @param labelLoader the method called when loading the label from JSON
         */
        public Builder(BiFunction<IGui, JsonElement, GuiElementInst> labelLoader) {
            this.labelLoader = labelLoader;
        }

        public Builder size(int[] size)                        { this.size = size;                                                return this; }
        public Builder vanillaTexture()                        { this.texture = VANILLA_TEXTURE;                                  return this; }
        public Builder customTexture(ResourceLocation texture) { this.texture = texture;                                          return this; }
        public Builder textureSize(int[] size)                 { this.textureSize = size;                                         return this; }
        public Builder uvSize(int[] size)                      { this.uvSize = size;                                              return this; }
        public Builder uvEnabled(int[] uv)                     { this.uvEnabled = uv;                                             return this; }
        public Builder uvHover(int[] uv)                       { this.uvHover = uv;                                               return this; }
        public Builder uvDisabled(int[] uv)                    { this.uvDisabled = uv;                                            return this; }
        public Builder centralTextureSize(int[] size)          { this.centralTextureSize = size;                                  return this; }
        public Builder labelAlignment(String... align)         { this.labelAlignment = align;                                     return this; }
        public Builder label(GuiElementInst label)             { this.label = label;                                              return this; }

        public Builder size(int width, int height)               { return this.size(new int[] {width, height}); }
        public Builder textureSize(int width, int height)        { return this.textureSize(new int[] {width, height}); }
        public Builder uvSize(int width, int height)             { return this.uvSize(new int[] {width, height}); }
        public Builder uvEnabled(int u, int v)                   { return this.uvEnabled(new int[] {u, v}); }
        public Builder uvHover(int u, int v)                     { return this.uvHover(new int[] {u, v}); }
        public Builder uvDisabled(int u, int v)                  { return this.uvDisabled(new int[] {u, v}); }
        public Builder centralTextureSize(int width, int height) { return this.centralTextureSize(new int[] {width, height}); }

        public Builder labelAlignment(GuiElementInst.Justify... alignment) {
            return this.labelAlignment(Arrays.stream(alignment).map(a -> a.name().toLowerCase(Locale.ROOT)).toArray(String[]::new));
        }

        @Override
        public void sanitize(IGui gui) {
            if( this.texture == null ) {
                this.vanillaTexture();
            }

            if( this.textureSize == null ) {
                this.textureSize = new int[] { 256, 256 };
            }

            if( this.uvSize == null ) {
                this.uvSize = new int[] { 200, 20 };
            }

            if( this.uvEnabled == null ) {
                this.uvEnabled = new int[] { 0, 66 };
            }

            if( this.uvHover == null ) {
                this.uvHover = new int[] { this.uvEnabled[0], this.uvEnabled[1] + this.uvSize[1] };
            }

            if( this.uvDisabled == null ) {
                this.uvDisabled = new int[] { this.uvEnabled[0], this.uvEnabled[1] - this.uvSize[1] };
            }

            if( this.centralTextureSize == null ) {
                this.centralTextureSize = new int[] { 190, 14 };
            }

            if( this.label == null ) {
                this.label = GuiElementInst.EMPTY;
            }

            if( this.labelAlignment == null ) {
                this.labelAlignment = new String[] { "center", "center" };
            }
        }

        @Override
        public ButtonSL get(IGui gui) {
            if( this.size == null ) {
                throw new UnsupportedOperationException("cannot create button without size");
            }

            this.sanitize(gui);

            this.label.alignment = this.labelAlignment;

            return new ButtonSL(this.texture, this.size, this.textureSize, this.uvEnabled, this.uvHover, this.uvDisabled, this.uvSize,
                                this.centralTextureSize, this.label.initialize(gui));
        }

        protected GuiElementInst loadLabel(IGui gui, JsonElement lblData) {
            if( lblData != null ) {
                if( lblData.isJsonPrimitive() ) {
                    int[] lblPos = new int[] { this.size[0] / 2, this.size[1] / 2 };
                    return new GuiElementInst(lblPos, new Text.Builder(new TranslationTextComponent(lblData.getAsString()))
                                                                      .color(0xFFFFFFFF).color("hover", 0xFFFFFFA0).color("disabled", 0xFFA0A0A0)
                                                                      .get(gui));
                } else {
                    return JsonUtils.GSON.fromJson(lblData, GuiElementInst.class);
                }
            }

            return null;
        }

        @Override
        public Builder loadFromJson(IGui gui, JsonObject json) {
            this.size(JsonUtils.getIntArray(json.get("size"), Range.is(2)));

            if( JsonUtils.getBoolVal(json.get("useVanillaTexture"), true) ) {
                this.vanillaTexture();
            } else {
                this.customTexture(gui.getDefinition().getTexture(json.get("texture")));
            }

            JsonUtils.fetchIntArray(json.get("textureSize"), this::textureSize, Range.is(2));
            JsonUtils.fetchIntArray(json.get("uvSize"), this::uvSize, Range.is(2));
            JsonUtils.fetchIntArray(json.get("uvEnabled"), this::uvEnabled, Range.is(2));
            JsonUtils.fetchIntArray(json.get("uvHover"), this::uvHover, Range.is(2));
            JsonUtils.fetchIntArray(json.get("uvDisabled"), this::uvDisabled, Range.is(2));
            JsonUtils.fetchIntArray(json.get("centralTextureSize"), this::uvDisabled, Range.is(2));
            JsonUtils.fetchStringArray(json.get("labelAlignment"), this::labelAlignment, Range.between(0, 2));

            GuiElementInst lbl = MiscUtils.apply(this.labelLoader, lcf -> lcf.apply(gui, json.get(LABEL)));
            if( lbl != null ) {
                this.label(lbl);
            }

            return this;
        }
    }

    @FunctionalInterface
    public interface HoverCallback
    {
        boolean check(IGui gui, int x, int y, double mouseX, double mouseY);
    }
}
