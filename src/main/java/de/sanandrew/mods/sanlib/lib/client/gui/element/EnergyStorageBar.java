////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class EnergyStorageBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("energy_bar");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( !(gui instanceof IGuiEnergyContainer) ) {
            throw new RuntimeException("Cannot use energy_bar on a GUI which doesn't implement IGuiEnergyContainer");
        }

        super.bakeData(gui, data, inst);
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        IGuiEnergyContainer gec = (IGuiEnergyContainer) gui;

        double energyPerc = gec.getEnergy() / (double) gec.getMaxEnergy();
        int energyBarY = Math.max(0, Math.min(this.size[1], MathHelper.ceil((1.0D - energyPerc) * this.size[1])));

        AbstractGui.blit(stack, 0, energyBarY, this.uv[0], this.uv[1] + energyBarY, this.size[0], this.size[1] - energyBarY, this.textureSize[0], this.textureSize[1]);
    }

    public interface IGuiEnergyContainer
    {
        int getEnergy();

        int getMaxEnergy();
    }
}
