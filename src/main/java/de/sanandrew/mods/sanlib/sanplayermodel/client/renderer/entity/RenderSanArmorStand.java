////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanArmorStand;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerArmorStandHead;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import net.minecraft.client.model.ModelArmorStandArmor;
import net.minecraft.client.renderer.entity.RenderArmorStand;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSanArmorStand
        extends RenderArmorStand
{
    public RenderSanArmorStand(RenderManager manager) {
        super(manager);
        this.layerRenderers.clear();

        this.mainModel = new ModelSanArmorStand();
        LayerBipedArmor layerbipedarmor = new LayerSanArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelArmorStandArmor(0.5F);
                this.modelArmor = new ModelArmorStandArmor(1.0F);
            }
        };
        this.addLayer(layerbipedarmor);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
        this.addLayer(new LayerArmorStandHead(this.getMainModel().bipedHead));
    }

    @Override
    public void doRender(EntityArmorStand entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
