////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class LayerArmorStandHead
        implements LayerRenderer<EntitySanArmorStand>
{
    private static final ItemStack PLANKS = new ItemStack(Blocks.PLANKS, 1, 0);

    private final ModelRenderer head;

    public LayerArmorStandHead(ModelRenderer head) {
        this.head = head;
    }

    public void doRenderLayer(EntitySanArmorStand armorStand, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                              float headPitch, float scale)
    {
        if( !PLANKS.isEmpty() ) {
            GlStateManager.pushMatrix();

            if( armorStand.isSneaking() ) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            if( armorStand.isChild() ) {
                GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
                GlStateManager.scale(0.7F, 0.7F, 0.7F);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
            GlStateManager.scale(0.7F, 0.7F, 0.7F);

            this.head.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, -0.625F, -0.625F);

            Minecraft.getMinecraft().getItemRenderer().renderItem(armorStand, PLANKS, ItemCameraTransforms.TransformType.HEAD);

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
