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
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

@SuppressWarnings("deprecated")
public class LayerCustomHeldItem
        implements LayerRenderer<EntityPlayer>
{
    private final RenderSanPlayer renderer;

    public LayerCustomHeldItem(RenderSanPlayer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean isLeft = handSide == EnumHandSide.LEFT;
            GlStateManager.translated(isLeft ? -0.05D : 0.05D, 0.125D, -0.625D);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, player, transformType, isLeft);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
