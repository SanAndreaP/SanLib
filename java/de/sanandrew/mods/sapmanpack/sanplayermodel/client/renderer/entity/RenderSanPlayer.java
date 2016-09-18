/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sapmanpack.sanplayermodel.client.Resources;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.model.ModelSanPlayerNew;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.renderer.entity.layers.LayerSanStandardClothes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class RenderSanPlayer
        extends RenderPlayer
{
    private ModelSanPlayerNew myModel = new ModelSanPlayerNew(0.0F);
    private LayerSanArmor layerArmor;
    private LayerSanStandardClothes layerClothes;

    public RenderSanPlayer(RenderManager manager) {
        super(manager);
        this.mainModel = this.myModel;
        this.layerRenderers.clear();

        this.addLayer(this.layerArmor = new LayerSanArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerCustomHead(this.myModel.head));
        this.addLayer(new LayerElytra(this));
        this.addLayer(this.layerClothes = new LayerSanStandardClothes(this));
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
}
