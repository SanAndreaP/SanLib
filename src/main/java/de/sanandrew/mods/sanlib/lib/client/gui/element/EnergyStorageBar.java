////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class EnergyStorageBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("energy_bar");

    public EnergyStorageBar(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color) {
        super(txLocation, size, textureSize, uv, scale, color);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        if( !(gui instanceof IGuiEnergyContainer) ) {
            throw new UnsupportedOperationException("Cannot use energy_bar on a GUI which doesn't implement IGuiEnergyContainer");
        }

        super.setup(gui, inst);
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        IGuiEnergyContainer gec = (IGuiEnergyContainer) gui;

        double energyPerc = gec.getEnergy() / (double) gec.getMaxEnergy();
        int energyBarY = Math.max(0, Math.min(this.size[1], MathHelper.ceil((1.0D - energyPerc) * this.size[1])));

        AbstractGui.blit(stack, 0, energyBarY, this.uv[0], this.uv[1] + (float) energyBarY, this.size[0], this.size[1] - energyBarY, this.textureSize[0], this.textureSize[1]);
    }

    public interface IGuiEnergyContainer
    {
        int getEnergy();

        int getMaxEnergy();
    }

    public static class Builder
            extends Texture.Builder
    {
        public Builder(int[] size) {
            super(size);
        }

        @Override
        public EnergyStorageBar get(IGui gui) {
            this.sanitize(gui);

            return new EnergyStorageBar(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);

            return IBuilder.copyValues(tb, new Builder(tb.size));
        }

        public static EnergyStorageBar fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
