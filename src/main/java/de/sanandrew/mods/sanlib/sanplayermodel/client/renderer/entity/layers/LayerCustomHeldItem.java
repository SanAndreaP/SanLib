/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCustomHeldItem
        implements LayerRenderer<EntityPlayer>
{
    private final RenderSanPlayer renderer;

    public LayerCustomHeldItem(RenderSanPlayer renderer) {
        this.renderer = renderer;
    }

    public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean isRight = player.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack leftItem = isRight ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
        ItemStack rightItem = isRight ? player.getHeldItemMainhand() : player.getHeldItemOffhand();

        if( ItemStackUtils.isValid(leftItem) || ItemStackUtils.isValid(rightItem) ) {
            GlStateManager.pushMatrix();
            this.renderHeldItem(player, rightItem, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(player, leftItem, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityPlayer player, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide) {
        if( ItemStackUtils.isValid(stack) ) {
            GlStateManager.pushMatrix();
            this.renderer.getMainModel().postRenderArm(0.0625F, handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean isLeft = handSide == EnumHandSide.LEFT;
            GlStateManager.translate(isLeft ? -0.05 : 0.05, 0.125F, -0.625F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, transformType, isLeft);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
