/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib.client.lexicon.button;

import de.sanandrew.mods.sanlib.client.lexicon.GuiLexicon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonEntryDivider
        extends GuiButton
{
    public GuiButtonEntryDivider(GuiLexicon gui, int id, int x, int y) {
        super(id, x, y, gui.lexicon.getEntryWidth() - 4, 5, "");
    }

    @Override
    public void render(int mx, int my, float partTicks) {
        this.enabled = false;
        if( this.visible ) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            int color1 = 0xC0FFFFFF;
            int color2 = 0x80FFFFFF;

            this.drawGradientRect(this.x, this.y + 2, this.x + this.width, this.y + 3, color1, color2);

            GlStateManager.popMatrix();
        }
    }
}
