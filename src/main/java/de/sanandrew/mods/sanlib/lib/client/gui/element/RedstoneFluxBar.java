package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class RedstoneFluxBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("rflux_bar");

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof IGuiEnergyContainer) ) {
            throw new RuntimeException("Cannot use rflux_bar on a GUI which doesn't implement IGuiEnergyContainer");
        }

        super.bakeData(gui, data);
    }

    @Override
    void drawRect(IGui gui) {
        IGuiEnergyContainer gec = (IGuiEnergyContainer) gui;

        double energyPerc = gec.getEnergy() / (double) gec.getMaxEnergy();
        int energyBarY = Math.max(0, Math.min(this.data.size[1], MathHelper.ceil((1.0D - energyPerc) * this.data.size[1])));

        Gui.drawModalRectWithCustomSizedTexture(0, energyBarY, this.data.uv[0], this.data.uv[1] + energyBarY, this.data.size[0], this.data.size[1] - energyBarY, this.data.textureSize[0], this.data.textureSize[1]);
    }

    public interface IGuiEnergyContainer
    {
        int getEnergy();

        int getMaxEnergy();
    }
}
