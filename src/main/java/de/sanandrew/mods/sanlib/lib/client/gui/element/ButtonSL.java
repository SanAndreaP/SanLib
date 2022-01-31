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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172"})
public class ButtonSL
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("button");

    public static final String LABEL = "label";

    protected ResourceLocation texture;
    protected int[]            size;
    protected int[]            textureSize;
    protected int[]            uvEnabled;
    protected int[]            uvHover;
    protected int[]            uvDisabled;
    protected int[]            uvSize;
    protected int[]            centralTextureSize;
    protected Button.IPressable buttonFunction = btn -> {};

    protected final Button buttonDelegate;

    protected double     currMouseX;
    protected double     currMouseY;
    protected boolean isCurrHovering;

    @SuppressWarnings("java:S107")
    public ButtonSL(ResourceLocation texture, int[] size, int[] textureSize, int[] uvEnabled, int[] uvHover, int[] uvDisabled, int[] uvSize, int[] centralTextureSize,
                    GuiElementInst label)
    {
        this.texture = texture;
        this.size = size;
        this.textureSize = textureSize;
        this.uvEnabled = uvEnabled;
        this.uvHover = uvHover;
        this.uvDisabled = uvDisabled;
        this.uvSize = uvSize;
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

    public void setFunction(Button.IPressable func) {
        this.buttonFunction = func;
    }

    public boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY) {
        return IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        if( button == 0 && this.isActive() && this.isCurrHovering ) {
            Screen gs  = gui.get();
            Button btn = this.buttonDelegate;

            btn.playDownSound(gs.getMinecraft().getSoundManager());
            this.buttonFunction.onPress(this.buttonDelegate);

            return true;
        }

        return super.mouseClicked(gui, mouseX, mouseY, button);
    }

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

            drawTiledTexture(stack, 0, cornerHeight,
                             uv[0], uv[1] + (float) cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, cornerWidth, 0,
                             uv[0] + (float) cornerWidth, uv[1],
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, this.size[0] - cornerWidth, cornerHeight,
                             uv[0] + this.uvSize[0] - (float) cornerWidth, uv[1] + (float) cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(stack, cornerWidth, this.size[1] - cornerHeight,
                             uv[0] + (float) cornerWidth, uv[1] + this.uvSize[1] - (float) cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);

            drawTiledTexture(stack, cornerWidth, cornerHeight,
                             uv[0] + (float) cornerWidth, uv[1] + (float) cornerHeight,
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

    @Override
    public boolean isVisible() {
        return this.buttonDelegate != null && this.buttonDelegate.visible;
    }

    public void setVisible(boolean visible) {
        this.buttonDelegate.visible = visible;
    }

    public static class Builder
            implements IBuilder<ButtonSL>
    {
        public final int[] size;

        protected ResourceLocation texture;
        protected int[]            textureSize;
        protected int[]            uvSize;
        protected int[]            uvEnabled;
        protected int[]            uvHover;
        protected int[]            uvDisabled;
        protected int[]            centralTextureSize;
        protected String[]         labelAlignment;
        protected GuiElementInst   label;

        public Builder(int[] size) {
            this.size = size;
        }

        public Builder vanillaTexture()                        { this.texture = new ResourceLocation("textures/gui/widgets.png"); return this; }
        public Builder customTexture(ResourceLocation texture) { this.texture = texture;                                          return this; }
        public Builder textureSize(int[] size)                 { this.textureSize = size;                                         return this; }
        public Builder uvSize(int[] size)                      { this.uvSize = size;                                              return this; }
        public Builder uvEnabled(int[] uv)                     { this.uvEnabled = uv;                                             return this; }
        public Builder uvHover(int[] uv)                       { this.uvHover = uv;                                               return this; }
        public Builder uvDisabled(int[] uv)                    { this.uvDisabled = uv;                                            return this; }
        public Builder centralTextureSize(int[] size)          { this.centralTextureSize = size;                                  return this; }
        public Builder labelAlignment(String[] align)          { this.labelAlignment = align;                                     return this; }
        public Builder label(GuiElementInst label)             { this.label = label;                                              return this; }

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
            this.sanitize(gui);

            this.label.alignment = this.labelAlignment;

            return new ButtonSL(this.texture, this.size, this.textureSize, this.uvEnabled, this.uvHover, this.uvDisabled, this.uvSize, this.centralTextureSize,
                                this.label.initialize(gui));
        }

        protected GuiElementInst loadLabel(IGui gui, JsonElement lblData) {
            if( lblData != null ) {
                if( lblData.isJsonPrimitive() ) {
                    int[] lblPos = new int[] { this.size[0] / 2, this.size[1] / 2 };
                    return new GuiElementInst(lblPos, new Text.Builder(new TranslationTextComponent(lblData.getAsString()))
                                                                      .color(0xFFFFFFFF).color("hover", 0xFFFFFFA0).color("disabled", 0xFFA0A0A0).get(gui));
                } else {
                    return JsonUtils.GSON.fromJson(lblData, GuiElementInst.class);
                }
            }

            return null;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadLabel);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<Builder, BiFunction<IGui, JsonElement, GuiElementInst>> loadLabelFunc) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));

            if( JsonUtils.getBoolVal(data.get("useVanillaTexture"), true) ) {
                b.vanillaTexture();
            } else {
                b.customTexture(gui.getDefinition().getTexture(data.get("texture")));
            }

            JsonUtils.fetchIntArray(data.get("textureSize"), b::textureSize, Range.is(2));
            JsonUtils.fetchIntArray(data.get("uvSize"), b::uvSize, Range.is(2));
            JsonUtils.fetchIntArray(data.get("uvEnabled"), b::uvEnabled, Range.is(2));
            JsonUtils.fetchIntArray(data.get("uvHover"), b::uvHover, Range.is(2));
            JsonUtils.fetchIntArray(data.get("uvDisabled"), b::uvDisabled, Range.is(2));
            JsonUtils.fetchIntArray(data.get("centralTextureSize"), b::uvDisabled, Range.is(2));
            JsonUtils.fetchStringArray(data.get("labelAlignment"), b::labelAlignment, Range.between(0, 2));

            GuiElementInst lbl = MiscUtils.apply(loadLabelFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data.get(LABEL))));
            if( lbl != null ) {
                b.label(lbl);
            }

            return b;
        }

        public static ButtonSL fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
