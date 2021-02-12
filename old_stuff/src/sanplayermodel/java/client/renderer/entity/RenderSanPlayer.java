////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.sanplayermodel.Resources;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.model.ModelSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity.layers.LayerCustomHeldItem;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity.layers.LayerSanStandardClothes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

@SideOnly( Side.CLIENT )
public class RenderSanPlayer
        extends RenderPlayer
{
    private ModelSanPlayer myModel = new ModelSanPlayer(0.0F);
    private LayerSanArmor layerArmor;
    private LayerSanStandardClothes layerClothes;

    public static float armTilt;
    public static boolean hasCstChest;

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
        this.addLayer(new LayerCustomHead(this.myModel.bipedHead));
        this.addLayer(new LayerElytra(this));
    }

    @Override
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks) {
        if( !this.renderOutlines ) {
            this.renderName(player, x, y, z);
        }
        armTilt = Math.max(this.layerArmor.armTilt, this.layerClothes.armTilt);
        hasCstChest = this.layerArmor.hasCstChest || this.layerClothes.hasCstChest;
        if( !player.isUser() || this.renderManager.renderViewEntity == player ) {
            double yShifted = y;

            if( player.isSneaking() ) {
                yShifted = y - 0.125D;
            }

            this.setModelVisibilities(player);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            doRenderLivingBase(player, x, yShifted, z, partialTicks);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    private void doRenderLivingBase(AbstractClientPlayer player, double x, double y, double z, float partialTicks) {
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
        } catch( Exception ex ) {
            SanLib.LOG.log(Level.ERROR, "Couldn't render SanAndreasP", ex);
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
        return null;
//        return clientPlayer.isPlayerSleeping() ? Resources.MAIN_MODEL_TEXTURE_SLEEP : Resources.MAIN_MODEL_TEXTURE;
    }

    @Override
    public void renderRightArm(AbstractClientPlayer clientPlayer) {
        renderArm(clientPlayer, EnumHandSide.RIGHT);
    }

    @Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer) {
        renderArm(clientPlayer, EnumHandSide.LEFT);
    }

    private void renderArm(AbstractClientPlayer clientPlayer, EnumHandSide side) {
        this.bindTexture(this.getEntityTexture(clientPlayer));
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.setModelVisibilities(clientPlayer);
        GlStateManager.enableBlend();
        this.myModel.swingProgress = 0.0F;
        this.myModel.isSneak = false;
        this.myModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        ModelRenderer arm = side == EnumHandSide.RIGHT ? this.myModel.bipedRightArm : this.myModel.bipedLeftArm;
        arm.rotateAngleX = 0.0F;
        arm.rotateAngleZ = side == EnumHandSide.RIGHT ? 0.1F : -0.1F;
        arm.render(0.0625F);
        this.layerClothes.renderHand(clientPlayer, 0.0625F, side);
        this.layerArmor.renderHand(clientPlayer, 0.0625F, side);
        GlStateManager.disableBlend();
    }

    @Override
    protected void applyRotations(AbstractClientPlayer player, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(player, ageInTicks, rotationYaw, partialTicks);

        boolean isSwimming = player.isInWater() && player.isSprinting();
        if( (!(player.isEntityAlive() && player.isPlayerSleeping()) && !player.isElytraFlying())
            && (isSwimming || player.height == 0.6F) )
        {
            float pitch = player.height == 0.6F && !isSwimming ? 0.0F : player.rotationPitch;
            float f3 = pitch + (-90.0F - pitch * 2.0F) * 1.0F;
            GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -1.0F, 0.2F);
        }
    }

    public boolean isOutlineRendering() {
        return this.renderOutlines;
    }
}
