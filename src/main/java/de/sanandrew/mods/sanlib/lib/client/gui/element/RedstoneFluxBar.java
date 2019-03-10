package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

@SuppressWarnings({"Duplicates"})
public class RedstoneFluxBar
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("rflux_bar");

    private BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof IGuiEnergyContainer) ) {
            throw new RuntimeException("Cannot use rfluxbar on a GUI which doesn't implement IGuiEnergyContainer");
        }
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.location = new ResourceLocation(data.get("location").getAsString());
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
            this.data.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
            this.data.scale = JsonUtils.getDoubleArray(data.get("scale"), new double[] {1.0D, 1.0D}, Range.is(2));
            this.data.color = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
            this.data.forceAlpha = JsonUtils.getBoolVal(data.get("forceAlpha"), false);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        IGuiEnergyContainer gec = (IGuiEnergyContainer) gui;

        gui.get().mc.renderEngine.bindTexture(this.data.location);
        GlStateManager.pushMatrix();
        if( this.data.forceAlpha ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.translate(x, y, 0.0D);
        GlStateManager.scale(this.data.scale[0], this.data.scale[1], 1.0D);
        GlStateManager.color(this.data.color.fRed(), this.data.color.fGreen(), this.data.color.fBlue(), this.data.color.fAlpha());


        double energyPerc = gec.getEnergy() / (double) gec.getMaxEnergy();
        int energyBarY = Math.max(0, Math.min(this.data.size[1], MathHelper.ceil((1.0D - energyPerc) * this.data.size[1])));

        Gui.drawModalRectWithCustomSizedTexture(0, energyBarY, this.data.uv[0], this.data.uv[1] + energyBarY, this.data.size[0], this.data.size[1] - energyBarY, this.data.textureSize[0], this.data.textureSize[1]);

        GlStateManager.popMatrix();
    }

    @Override
    public int getHeight() {
        return this.data == null ? 0 : this.data.size[1];
    }

    static final class BakedData
    {
        private ResourceLocation location;
        int[] size;
        private int[] textureSize;
        private int[] uv;
        private double[] scale;
        private ColorObj color;
        private boolean forceAlpha;
    }

    public interface IGuiEnergyContainer
    {
        int getEnergy();

        int getMaxEnergy();
    }
}
