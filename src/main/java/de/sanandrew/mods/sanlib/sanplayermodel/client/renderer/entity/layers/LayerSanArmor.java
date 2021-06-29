package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class LayerSanArmor<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>>
        extends BipedArmorLayer<T, M, A>
{
    public LayerSanArmor(IEntityRenderer<T, M> renderer, A leggingsModel, A armorModel) {
        super(renderer, leggingsModel, armorModel);
    }

    public static final class ModelSanBiped<T extends LivingEntity>
            extends BipedModel<T>
    {
        public ModelSanBiped(float scale) {
            super(scale);
        }

        @Override
        public void setupAnim(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            ModelSanPlayer.setLimbRotations(this);
        }
    }
}
