/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib.client.lexicon.button;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.client.ClientTickHandler;
import de.sanandrew.mods.sanlib.client.lexicon.GuiLexicon;
import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiButtonGroup
        extends GuiButton
{
    private static final float TIME = 1.0F;

    public final ILexiconGroup group;
    private final GuiLexicon gui;

    private final ResourceLocation texture;

    private float lastTime;
    private float ticksHovered = -0.1F;
    private final OnMouseOverCallback onMouseOver;

    private void doBtnShader(int shader) {
        TextureManager texMgr = Minecraft.getInstance().getTextureManager();
        int heightMatchUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "heightMatch");
        int imageUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "image");
        int maskUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "mask");

        float heightMatch = this.ticksHovered / TIME;
        GlStateManager.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        GlStateManager.bindTexture(texMgr.getTexture(this.texture).getGlTextureId());
        ARBShaderObjects.glUniform1iARB(imageUniform, 0);

        GlStateManager.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
        GlStateManager.enableTexture2D();
//        GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D); ????
        ResourceLocation stencil = this.gui.lexicon.getGroupStencilTexture();
        texMgr.getTexture(stencil);
        ITextureObject stencilTex;
        texMgr.bindTexture(stencil);
        stencilTex = texMgr.getTexture(stencil);
        GlStateManager.bindTexture(stencilTex.getGlTextureId());
        ARBShaderObjects.glUniform1iARB(maskUniform, 7);

        ARBShaderObjects.glUniform1fARB(heightMatchUniform, heightMatch);
    }

    public GuiButtonGroup(GuiLexicon gui, int id, int x, int y, ILexiconGroup group, OnMouseOverCallback onMouseOver) {
        super(id, x, y, 32, 32, "");
        this.group = group;
        this.texture = group.getIcon();
        this.onMouseOver = onMouseOver;
        this.gui = gui;
    }

    @Override
    public void render(int mx, int my, float partTicks) {
        float gameTicks = ClientTickHandler.ticksInGame;
        float timeDelta = (gameTicks - this.lastTime) * partTicks;
        this.lastTime = gameTicks;

        if( mx >= this.x && my >= this.y && mx < this.x + this.width && my < this.y + this.height ) {
            if( this.ticksHovered <= TIME ) {
                this.ticksHovered = Math.min(TIME, this.ticksHovered + timeDelta);
            }
            if( this.onMouseOver != null ) {
                this.onMouseOver.accept(this.group, mx, my);
            }
        } else if( this.ticksHovered >= 0.0F ) {
            this.ticksHovered = Math.max(-0.1F, this.ticksHovered - timeDelta);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getInstance().getTextureManager().bindTexture(this.texture);

        int texture = 0;
        boolean shaders = ShaderHelper.areShadersEnabled();

        if( shaders ) {
            OpenGlHelper.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            ShaderHelper.useShader(this.gui.lexicon.getGroupStencilId(), this::doBtnShader);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0, this.zLevel * 2);
        Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, 32, 32, 32, 32);
        GlStateManager.popMatrix();

        if( shaders ) {
            ShaderHelper.releaseShader();
            GlStateManager.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB + ShaderHelper.getSecondaryTextureUnit());
            GlStateManager.bindTexture(texture);
            GlStateManager.activeTexture(ARBMultitexture.GL_TEXTURE0_ARB);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        List<ILexiconEntry> entries = this.group.getEntries();
        this.gui.changePage(this.group, entries.size() == 1 ? entries.get(0) : null, 0.0F, true);
    }

    @FunctionalInterface
    public interface OnMouseOverCallback {
        void accept(ILexiconGroup group, int mouseX, int mouseY);
    }
}
