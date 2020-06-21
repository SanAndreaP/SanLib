////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.lexicon.button;

import de.sanandrew.mods.sanlib.client.lexicon.GuiLexicon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Deprecated
public class GuiButtonEntryDivider
        extends GuiButton
{
    public GuiButtonEntryDivider(GuiLexicon gui, int id, int x, int y) {
        super(id, x, y, gui.lexicon.getEntryWidth() - 4, 5, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mx, int my, float partTicks) {
        this.enabled = false;
        if( this.visible ) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int color1 = 0xC0FFFFFF;
            int color2 = 0x80FFFFFF;

            this.drawGradientRect(this.x, this.y + 2, this.x + this.width, this.y + 3, color1, color2);

            GlStateManager.popMatrix();
        }
    }
}
