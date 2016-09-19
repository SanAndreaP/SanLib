/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.sanplayermodel.client.model;

import de.sanandrew.mods.sapmanpack.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sapmanpack.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.Resources;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;

import java.util.Arrays;

public class ModelSanPlayerNew
        extends ModelPlayer
        implements ModelJsonHandler<ModelSanPlayerNew, ModelJsonLoader.ModelJson>
{
    private final float scaling;
    private final ModelJsonLoader<ModelSanPlayerNew, ModelJsonLoader.ModelJson> modelJson;

    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer leftArm;
    public ModelRenderer rightArm;
    public ModelRenderer leftLeg;
    public ModelRenderer rightLeg;

    public ModelSanPlayerNew(float scaling) {
        super(scaling, true);
        this.scaling = scaling;
        this.modelJson = ModelJsonLoader.create(this, Resources.MAIN_MODEL, "head", "body", "leftArm", "rightArm", "leftLeg", "rightLeg");
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);

        this.setRotateAngle(this.head, this.bipedHead.rotateAngleX, this.bipedHead.rotateAngleY, this.bipedHead.rotateAngleZ);
        this.setRotateAngle(this.body, this.bipedBody.rotateAngleX * 0.5F, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
        this.setRotateAngle(this.leftArm, this.bipedLeftArm.rotateAngleX, this.bipedLeftArm.rotateAngleY, this.bipedLeftArm.rotateAngleZ);
        this.setRotateAngle(this.rightArm, this.bipedRightArm.rotateAngleX, this.bipedRightArm.rotateAngleY, this.bipedRightArm.rotateAngleZ);

        if( this.isRiding ) {
            this.setRotateAngle(this.leftLeg, this.bipedLeftLeg.rotateAngleX, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
            this.setRotateAngle(this.rightLeg, this.bipedRightLeg.rotateAngleX, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
        } else {
            this.setRotateAngle(this.leftLeg, this.bipedLeftLeg.rotateAngleX * 0.5F, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
            this.setRotateAngle(this.rightLeg, this.bipedRightLeg.rotateAngleX * 0.5F, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
        }

        if( this.isSneak ) {
            this.leftLeg.rotationPointZ = 3.0F;
            this.rightLeg.rotationPointZ = 3.0F;
            this.leftLeg.rotateAngleX -= 0.15F;
            this.rightLeg.rotateAngleX -= 0.15F;
            this.leftArm.rotateAngleX += 0.2F;
            this.rightArm.rotateAngleX += 0.2F;
        } else {
            this.leftLeg.rotationPointZ = 0.0F;
            this.rightLeg.rotationPointZ = 0.0F;
        }

        this.leftArm.rotateAngleZ -= RenderSanPlayer.armTilt;
        this.rightArm.rotateAngleZ += RenderSanPlayer.armTilt;
    }

    @Override
    public void setInvisible(boolean visible) {
        super.setInvisible(visible);

        Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.showModel = visible);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanPlayerNew, ModelJsonLoader.ModelJson> loader) {
        loader.load();

        this.head = loader.getBox("head");
        this.body = loader.getBox("body");
        this.leftArm = loader.getBox("leftArm");
        this.rightArm = loader.getBox("rightArm");
        this.leftLeg = loader.getBox("leftLeg");
        this.rightLeg = loader.getBox("rightLeg");
    }

    @Override
    protected ModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public void setTexture(String textureStr) { }

    @Override
    public float getBaseScale() {
        return this.scaling;
    }
}
