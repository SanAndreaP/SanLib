package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class ModelSanPlayer<T extends PlayerEntity>
        extends PlayerModel<T>
{
    public ModelSanPlayer(float modelSize) {
        super(modelSize, true);
    }

    @Override
    public void setRotationAngles(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        setLimbRotations(this);
    }

    public static void setLimbRotations(BipedModel<? extends LivingEntity> model) {
        if( !model.isSitting ) {
            model.bipedLeftArm.rotateAngleZ -= 0.2F;
            model.bipedRightArm.rotateAngleZ += 0.2F;

            model.bipedLeftLeg.rotateAngleX /= 3.5F;
            model.bipedRightLeg.rotateAngleX /= 3.5F;
        }

        if( model instanceof PlayerModel ) {
            PlayerModel<?> pm = (PlayerModel<?>) model;
            pm.bipedLeftArmwear.copyModelAngles(model.bipedLeftArm);
            pm.bipedRightArmwear.copyModelAngles(model.bipedRightArm);
            pm.bipedLeftLegwear.copyModelAngles(model.bipedLeftLeg);
            pm.bipedRightLegwear.copyModelAngles(model.bipedRightLeg);
        }
    }
}
