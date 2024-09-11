package dev.sanandrea.mods.sanlib.mixin;

import dev.sanandrea.mods.sanlib.client.layer.SanSkirtLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.model.PlayerModel.class)
public class MixinPlayerModel<T extends LivingEntity>
        extends HumanoidModel<T>
{
    @Final
    @Shadow
    public ModelPart leftPants;
    @Final
    @Shadow
    public ModelPart rightPants;
    @Final
    @Shadow
    public ModelPart leftSleeve;
    @Final
    @Shadow
    public ModelPart rightSleeve;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void setSanAnimations(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if( entity instanceof Player player && SanSkirtLayer.isSanPlayer(player) ) {
            if( !this.riding ) {
                if( this.crouching ) {
                    this.leftSleeve.xRot = (this.leftArm.xRot += 0.2F);
                    this.rightSleeve.xRot = (this.rightArm.xRot += 0.2F);
                }
                this.leftSleeve.zRot = (this.leftArm.zRot -= 0.2F);
                this.rightSleeve.zRot = (this.rightArm.zRot += 0.2F);
                this.leftPants.xRot = (this.leftLeg.xRot *= 0.7F);
                this.rightPants.xRot = (this.rightLeg.xRot *= 0.7F);
            }
        }
    }

    public MixinPlayerModel(ModelPart root) {
        super(root);
    }
}
