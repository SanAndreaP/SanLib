/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sanlib.sanplayermodel.Resources;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerCustomHeldItem;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanStandardClothes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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
        this.layerArmor = new LayerSanArmor(this);
        this.layerClothes = new LayerSanStandardClothes(this);
        this.addLayer(this.layerArmor);
        this.addLayer(this.layerClothes);
        this.addLayer(new LayerCustomHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerCustomHead(this.myModel.head));
        this.addLayer(new LayerElytra(this));
    }

    @Override
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks) {
        armTilt = Math.max(this.layerArmor.armTilt, this.layerClothes.armTilt);
        if( !player.isUser() || this.renderManager.renderViewEntity == player ) {
            double yShifted = y;

            if( player.isSneaking() ) {
                yShifted = y - 0.125D;
            }

            this.setModelVisibilities(player);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            doRenderLivingBase(player, x, yShifted, z, entityYaw, partialTicks);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void doRenderLivingBase(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.myModel.swingProgress = this.getSwingProgress(player, partialTicks);
        boolean shouldSit = player.isRiding() && (player.getRidingEntity() != null && player.getRidingEntity().shouldRiderSit());
        this.myModel.isRiding = shouldSit;
        this.myModel.isChild = player.isChild();

        try {
            float interpolOffset = this.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);
            float interpolHead = this.interpolateRotation(player.prevRotationYawHead, player.rotationYawHead, partialTicks);
            float interpolDelta = interpolHead - interpolOffset;

            if( shouldSit && player.getRidingEntity() instanceof EntityLivingBase ) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) player.getRidingEntity();
                interpolOffset = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                interpolDelta = interpolHead - interpolOffset;
                float interpolWrapped = MathHelper.wrapDegrees(interpolDelta);

                if( interpolWrapped < -85.0F ) {
                    interpolWrapped = -85.0F;
                }

                if( interpolWrapped >= 85.0F ) {
                    interpolWrapped = 85.0F;
                }

                interpolOffset = interpolHead - interpolWrapped;

                if( interpolWrapped * interpolWrapped > 2500.0F ) {
                    interpolOffset += interpolWrapped * 0.2F;
                }

                interpolDelta = interpolHead - interpolOffset;
            }

            float rotPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            this.renderLivingAt(player, x, y, z);
            float rotFloat = this.handleRotationFloat(player, partialTicks);
            this.applyRotations(player, rotFloat, interpolOffset, partialTicks);
            float scale = this.prepareScale(player, partialTicks);
            float limbSwingAmount = 0.0F;
            float limbSwing = 0.0F;

            if( !player.isRiding() ) {
                limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
                limbSwing = player.limbSwing - player.limbSwingAmount * (1.0F - partialTicks);

                if( player.isChild() ) {
                    limbSwing *= 3.0F;
                }

                if( limbSwingAmount > 1.0F ) {
                    limbSwingAmount = 1.0F;
                }

                interpolDelta = interpolHead - interpolOffset;
            }

            GlStateManager.enableAlpha();
            this.myModel.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            this.myModel.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, interpolDelta, rotPitch, scale, player);

            if( this.renderOutlines ) {
                boolean hasTeamColor = this.setScoreTeamColor(player);
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColor(player));

                if( !this.renderMarker ) {
                    this.renderModel(player, limbSwing, limbSwingAmount, rotFloat, interpolDelta, rotPitch, scale);
                }

                if( !player.isSpectator() ) {
                    this.renderLayers(player, limbSwing, limbSwingAmount, partialTicks, rotFloat, interpolDelta, rotPitch, scale);
                }

                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();

                if( hasTeamColor ) {
                    this.unsetScoreTeamColor();
                }
            } else {
                boolean hasRenderBrightness = this.setDoRenderBrightness(player, partialTicks);
                this.renderModel(player, limbSwing, limbSwingAmount, rotFloat, interpolDelta, rotPitch, scale);

                if( hasRenderBrightness ) {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if( !player.isSpectator() ) {
                    this.renderLayers(player, limbSwing, limbSwingAmount, partialTicks, rotFloat, interpolDelta, rotPitch, scale);
                }
            }

            GlStateManager.disableRescaleNormal();
        } catch( Exception exception ) {
            //            LOGGER.error("Couldn't render entity", (Throwable)exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(AbstractClientPlayer clientPlayer) {
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
        this.myModel.rightArm.rotateAngleZ = this.myModel.bipedRightArm.rotateAngleZ;
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
        this.myModel.leftArm.rotateAngleZ = this.myModel.bipedLeftArm.rotateAngleZ;
        this.myModel.leftArm.render(0.0625F);
        this.layerClothes.renderHand(clientPlayer, 0.0625F, EnumHandSide.LEFT);
        this.layerArmor.renderHand(clientPlayer, 0.0625F, EnumHandSide.LEFT);
        GlStateManager.disableBlend();
    }

    public boolean isOutlineRendering() {
        return this.renderOutlines;
    }
}
