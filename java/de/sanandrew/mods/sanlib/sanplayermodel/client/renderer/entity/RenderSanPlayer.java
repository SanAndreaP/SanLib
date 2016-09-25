/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sanlib.sanplayermodel.client.Resources;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerCustomHeldItem;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanStandardClothes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class RenderSanPlayer
        extends RenderPlayer
{
    private ModelSanPlayer myModel = new ModelSanPlayer(0.0F);
    private LayerSanArmor layerArmor;
    private LayerSanStandardClothes layerClothes;

    public static float armTilt;

    public RenderSanPlayer(RenderManager manager) {
        super(manager);
        this.mainModel = this.myModel;
        this.layerRenderers.clear();

        this.addLayer(this.layerArmor = new LayerSanArmor(this));
        this.addLayer(this.layerClothes = new LayerSanStandardClothes(this));
        this.addLayer(new LayerCustomHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerCustomHead(this.myModel.head));
        this.addLayer(new LayerElytra(this));
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        armTilt = Math.max(this.layerArmor.armTilt, this.layerClothes.armTilt);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer clientPlayer) {
        return clientPlayer.isPlayerSleeping() ? Resources.MAIN_MODEL_TEXTURE_SLEEP : Resources.MAIN_MODEL_TEXTURE;
    }

    @Override
    public void renderRightArm(AbstractClientPlayer clientPlayer) {
        this.bindTexture(this.getEntityTexture(clientPlayer));
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.setModelVisibilities(clientPlayer);
        GlStateManager.enableBlend();
        this.myModel.swingProgress = 0.0F;
        this.myModel.isSneak = false;
        this.myModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        this.myModel.rightArm.rotateAngleX = 0.0F;
        this.myModel.rightArm.render(0.0625F);
        this.layerClothes.renderHand(clientPlayer, 0.0625F, EnumHandSide.RIGHT);
        this.layerArmor.renderHand(clientPlayer, 0.0625F, EnumHandSide.RIGHT);
        GlStateManager.disableBlend();
    }

    @Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer) {
        this.bindTexture(this.getEntityTexture(clientPlayer));
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.setModelVisibilities(clientPlayer);
        GlStateManager.enableBlend();
        this.myModel.swingProgress = 0.0F;
        this.myModel.isSneak = false;
        this.myModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        this.myModel.leftArm.rotateAngleX = 0.0F;
        this.myModel.leftArm.render(0.0625F);
        this.layerClothes.renderHand(clientPlayer, 0.0625F, EnumHandSide.LEFT);
        this.layerArmor.renderHand(clientPlayer, 0.0625F, EnumHandSide.LEFT);
        GlStateManager.disableBlend();
    }

    public boolean isOutlineRendering() {
        return this.renderOutlines;
    }
}
