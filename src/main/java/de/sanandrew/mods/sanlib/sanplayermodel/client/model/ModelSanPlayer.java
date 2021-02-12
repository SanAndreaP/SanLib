package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

public class ModelSanPlayer
        extends ModelPlayer
{
    public ModelSanPlayer(float modelSize) {
        super(modelSize, true);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.bipedRightArm.rotateAngleZ += 0.2F;
        this.bipedLeftArm.rotateAngleZ -= 0.2F;

        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);

        if( !this.isRiding ) {
            this.bipedLeftLeg.rotateAngleX /= 3.5F;
            this.bipedRightLeg.rotateAngleX /= 3.5F;

            copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
            copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        }
    }
}
