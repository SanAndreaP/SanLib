package dev.sanandrea.mods.sanlib.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sanandrea.mods.sanlib.client.layer.SanSkirtLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    @Inject(method = "renderHand", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
    private void fixRenderHand(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, ModelPart rendererArm, ModelPart rendererArmwear, CallbackInfo ci) {
        if( SanSkirtLayer.isSanPlayer(player) && !this.model.riding ) {
            if( rendererArm == this.model.leftArm ) {
                rendererArmwear.zRot = (rendererArm.zRot += 0.2F);
            } else if( rendererArm == this.model.rightArm ) {
                rendererArmwear.zRot = (rendererArm.zRot -= 0.2F);
            }
        }
    }

    public MixinPlayerRenderer(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }
}
