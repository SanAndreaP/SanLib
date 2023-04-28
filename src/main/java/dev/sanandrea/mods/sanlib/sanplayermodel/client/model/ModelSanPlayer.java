/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.sanplayermodel.client.model;

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
    public void setupAnim(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        setLimbRotations(this);
    }

    public static void setLimbRotations(BipedModel<? extends LivingEntity> model) {
        if( !model.riding ) {
            model.leftArm.zRot -= 0.2F;
            model.rightArm.zRot += 0.2F;

            model.leftLeg.xRot /= 3.5F;
            model.rightLeg.xRot /= 3.5F;
        }

        if( model instanceof PlayerModel ) {
            PlayerModel<?> pm = (PlayerModel<?>) model;
            pm.leftSleeve.copyFrom(model.leftArm);
            pm.rightSleeve.copyFrom(model.rightArm);
            pm.leftPants.copyFrom(model.leftLeg);
            pm.rightPants.copyFrom(model.rightLeg);
        }
    }
}
