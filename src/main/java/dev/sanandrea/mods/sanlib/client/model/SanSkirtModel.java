package dev.sanandrea.mods.sanlib.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sanandrea.mods.sanlib.client.Resources;
import dev.sanandrea.mods.sanlib.lib.client.JsonModelLoader;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SanSkirtModel
        extends EntityModel<Player>
{
    private final ModelPart skirt1;
    private final ModelPart skirt2;

    private float swimAmount = 0.0F;

    public SanSkirtModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

        this.skirt1 = root.getChild("skirt1");
        this.skirt2 = root.getChild("skirt2");
    }

    @Override
    public void prepareMobModel(@NotNull Player player, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(player, limbSwing, limbSwingAmount, partialTick);

        this.swimAmount = player.getSwimAmount(partialTick);
    }

    @Override
    public void setupAnim(@NotNull Player player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.skirt1.resetPose();
        this.skirt2.resetPose();


        if( this.riding ) {
            this.skirt1.y -= 1.5F;
            this.skirt1.xRot = -0.2F;
            this.skirt2.y -= 4.5F;
            this.skirt2.xRot = -0.5F;
            return;
        }

        float swing;
        if( this.swimAmount > 0.0F) {
            swing = Mth.abs(Mth.cos(limbSwing * 0.6662F) * this.swimAmount);
            swing = Mth.lerp(this.swimAmount, swing, 0.3F * Mth.abs(Mth.cos(limbSwing * 0.33333334F + 3.1415927F)));
        } else {
            float amt = 1.0F;
            boolean isFlying = player.getFallFlyingTicks() > 4;

            if( isFlying ) {
                amt = (float)player.getDeltaMovement().lengthSqr();
                amt /= 0.2F;
                amt *= amt * amt;
            }

            if( amt < 1.0F ) {
                amt = 1.0F;
            }

            swing = Mth.abs(Mth.cos(limbSwing * 0.6662F) * limbSwingAmount) / amt;
        }

        this.skirt1.zScale = 1.0F + swing * 0.5F;
        this.skirt1.z -= swing * 2.5F;

        this.skirt2.zScale = 1.0F + swing;
        this.skirt2.z -= swing * 5.0F;

        if( player.isCrouching() ) {
            this.skirt1.z += 4.6F;
            this.skirt1.y += 1.8F;
            this.skirt2.z += 5.0F;
            this.skirt2.y -= 0.5F;

            this.skirt1.y += swing * 0.5F;
            this.skirt1.xRot = 0.2F;
            this.skirt2.zScale = 1.0F + swing;
        } else {
            this.skirt1.y -= swing * 2.0F;
            this.skirt2.yScale = 1.0F - swing * 0.25F;
            this.skirt2.y -= swing * 5.0F;
        }
    }

    public static LayerDefinition createLayer() {
        return createLayer(0.0F);
    }

    public static LayerDefinition createArmorLayer() {
        return createLayer(0.5F);
    }

    private static LayerDefinition createLayer(float scale) {
        return JsonModelLoader.load(Resources.SKIRT_MODEL, scale, "skirt1", "skirt2");
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color)
    {
        this.skirt1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.skirt2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
