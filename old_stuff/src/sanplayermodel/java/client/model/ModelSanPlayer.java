////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.old.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.sanplayermodel.Resources;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class ModelSanPlayer
        extends ModelPlayer
        implements ModelJsonHandler<ModelSanPlayer, ModelJsonLoader.ModelJson>
{
    private final float scaling;
    private ModelJsonLoader<ModelSanPlayer, ModelJsonLoader.ModelJson> modelJson;

    public ModelSanPlayer(float scaling) {
        super(scaling, true);
        this.scaling = scaling;
//        this.modelJson = ModelJsonLoader.create(this, Resources.MAIN_MODEL, "head", "body", "leftArm", "rightArm", "leftLeg", "rightLeg");
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);

        setSwimmingRotation(entity, this, limbSwing);

        this.bipedBody.rotateAngleX *= 0.5F;

        if( !this.isRiding ) {
            this.bipedLeftLeg.rotateAngleX *= 0.45F;
            this.bipedRightLeg.rotateAngleX *= 0.45F;
        }

        if( this.isSneak ) {
            this.bipedLeftLeg.rotationPointZ = 3.0F;
            this.bipedRightLeg.rotationPointZ = 3.0F;
            this.bipedLeftLeg.rotationPointY = 10.0F;
            this.bipedRightLeg.rotationPointY = 10.0F;
            this.bipedLeftLeg.rotateAngleX -= 0.05F;
            this.bipedRightLeg.rotateAngleX -= 0.05F;
            this.bipedLeftArm.rotateAngleX += 0.2F;
            this.bipedRightArm.rotateAngleX += 0.2F;
            if( RenderSanPlayer.hasCstChest ) {
                this.bipedLeftArm.rotationPointZ = 1.5F;
                this.bipedRightArm.rotationPointZ = 1.5F;
                this.bipedLeftArm.rotateAngleZ -= 0.05D;
                this.bipedRightArm.rotateAngleZ += 0.05D;
            }
        } else {
            setLegRotationPointY(this);
            this.bipedLeftLeg.rotationPointZ = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.0F;
        }

        if( RenderSanPlayer.hasCstChest ) {
            this.bipedLeftArm.rotateAngleZ -= RenderSanPlayer.armTilt;
            this.bipedRightArm.rotateAngleZ += RenderSanPlayer.armTilt;
        }
    }

    public static void setLegRotationPointY(ModelPlayer model) {
        if( model.isRiding ) {
            model.bipedLeftLeg.rotationPointY = 12.0F;
            model.bipedRightLeg.rotationPointY = 12.0F;
        } else {
            model.bipedLeftLeg.rotationPointY = 11.0F;
            model.bipedRightLeg.rotationPointY = 11.0F;
        }
    }

    @SuppressWarnings("FloatingPointEquality")
    static void setSwimmingRotation(Entity e, ModelPlayer model, float limbSwing) {
        if( !(e instanceof EntityPlayer) ) {
            return;
        }

        boolean isSwimming = (e.isInWater() && e.isSprinting());
        EntityPlayer p = (EntityPlayer) e;
        if( !(e.isEntityAlive() && p.isPlayerSleeping()) && !p.isElytraFlying() && (isSwimming || e.height == 0.6F) ) {
            model.bipedLeftArm.rotateAngleX = 0.0F;
            model.bipedRightArm.rotateAngleX = 0.0F;
            model.bipedRightArm.rotateAngleZ = (float) Math.PI * 0.5F + MathHelper.cos((float) (limbSwing * 0.6662F + Math.PI)) * 2.0F * 0.7F;
            model.bipedLeftArm.rotateAngleZ = (float) -Math.PI * 0.5F + MathHelper.cos(limbSwing * 0.6662F) * 2.0F * 0.7F;

            if( isSwimming ) {
                model.bipedHead.rotateAngleX *= 0.2F;
            }

            model.bipedHead.rotateAngleX -= 0.8F;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.showModel = visible);
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanPlayer, ModelJsonLoader.ModelJson> loader) {
        loader.load();

        this.bipedHead = loader.getBox("head");
        this.bipedBody = loader.getBox("body");
        this.bipedLeftArm = loader.getBox("leftArm");
        this.bipedRightArm = loader.getBox("rightArm");
        this.bipedLeftLeg = loader.getBox("leftLeg");
        this.bipedRightLeg = loader.getBox("rightLeg");
    }

    @Override
    public void setTexture(String textureStr) { }

    @Override
    public float getBaseScale() {
        return this.scaling;
    }
}
