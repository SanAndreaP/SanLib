package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;

public class LayerSanArmor
        extends LayerBipedArmor
{
    public LayerSanArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new ModelSanBiped(0.4F);
        this.modelArmor = new ModelSanBiped(0.9F);
    }

    private static final class ModelSanBiped
            extends ModelBiped
    {
        ModelSanBiped(float scale) {
            super(scale);
        }

        @Override
        public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

            this.bipedRightArm.rotateAngleZ += 0.2F;
            this.bipedLeftArm.rotateAngleZ -= 0.2F;

            if( !this.isRiding ) {
                this.bipedLeftLeg.rotateAngleX /= 3.5F;
                this.bipedRightLeg.rotateAngleX /= 3.5F;
            }
        }
    }
}
