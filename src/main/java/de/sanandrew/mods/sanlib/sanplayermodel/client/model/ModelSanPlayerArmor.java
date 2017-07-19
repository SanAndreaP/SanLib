/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class ModelSanPlayerArmor
        extends ModelPlayer
        implements ModelJsonHandler<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor>
{
    private final float scaling;
    private ResourceLocation texture;
    private final ModelJsonLoader<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor> modelJson;
    private final EntityEquipmentSlot slot;

    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer leftArm;
    public ModelRenderer rightArm;
    public ModelRenderer leftLeg;
    public ModelRenderer rightLeg;

    public ModelSanPlayerArmor(float scaling, ResourceLocation resource, EntityEquipmentSlot slot) {
        super(scaling, true);
        this.scaling = scaling;
        this.slot = slot;
        switch( slot ) {
            case HEAD:
                this.modelJson = ModelJsonLoader.create(this, LayerSanArmor.ModelJsonArmor.class, resource, "head");
                break;
            case CHEST:
                this.modelJson = ModelJsonLoader.create(this, LayerSanArmor.ModelJsonArmor.class, resource, "body", "leftArm", "rightArm");
                break;
            case LEGS:
                this.modelJson = ModelJsonLoader.create(this, LayerSanArmor.ModelJsonArmor.class, resource, "body", "rightLeg", "leftLeg");
                break;
            case FEET:
                this.modelJson = ModelJsonLoader.create(this, LayerSanArmor.ModelJsonArmor.class, resource, "rightLeg", "leftLeg");
                break;
            default:
                this.modelJson = null;
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        if( this.modelJson != null ) {
            Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
        }
    }

    public float getArmTilt() {
        return (this.modelJson != null && this.modelJson.isLoaded()) ? this.modelJson.getModelJsonInstance().armTilt : 0.0F;
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale, entity);

        if( entity instanceof EntitySanArmorStand ) {
            EntityArmorStand armorstand = (EntitySanArmorStand)entity;
            switch (this.slot) {
                case HEAD:
                    this.head.rotateAngleX = 0.017453292F * armorstand.getHeadRotation().getX();
                    this.head.rotateAngleY = 0.017453292F * armorstand.getHeadRotation().getY();
                    this.head.rotateAngleZ = 0.017453292F * armorstand.getHeadRotation().getZ();
                    this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
                    break;
                case CHEST:
                    this.body.rotateAngleX = 0.017453292F * armorstand.getBodyRotation().getX();
                    this.body.rotateAngleY = 0.017453292F * armorstand.getBodyRotation().getY();
                    this.body.rotateAngleZ = 0.017453292F * armorstand.getBodyRotation().getZ();
                    this.leftArm.rotateAngleX = 0.017453292F * armorstand.getLeftArmRotation().getX();
                    this.leftArm.rotateAngleY = 0.017453292F * armorstand.getLeftArmRotation().getY();
                    this.leftArm.rotateAngleZ = 0.017453292F * armorstand.getLeftArmRotation().getZ();
                    this.rightArm.rotateAngleX = 0.017453292F * armorstand.getRightArmRotation().getX();
                    this.rightArm.rotateAngleY = 0.017453292F * armorstand.getRightArmRotation().getY();
                    this.rightArm.rotateAngleZ = 0.017453292F * armorstand.getRightArmRotation().getZ();
                    break;
                case LEGS:
                    this.body.rotateAngleX = 0.017453292F * armorstand.getBodyRotation().getX();
                    this.body.rotateAngleY = 0.017453292F * armorstand.getBodyRotation().getY();
                    this.body.rotateAngleZ = 0.017453292F * armorstand.getBodyRotation().getZ();
                    //FALL-THROUGH
                case FEET:
                    this.leftLeg.rotateAngleX = 0.017453292F * armorstand.getLeftLegRotation().getX();
                    this.leftLeg.rotateAngleY = 0.017453292F * armorstand.getLeftLegRotation().getY();
                    this.leftLeg.rotateAngleZ = 0.017453292F * armorstand.getLeftLegRotation().getZ();
                    this.leftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
                    this.rightLeg.rotateAngleX = 0.017453292F * armorstand.getRightLegRotation().getX();
                    this.rightLeg.rotateAngleY = 0.017453292F * armorstand.getRightLegRotation().getY();
                    this.rightLeg.rotateAngleZ = 0.017453292F * armorstand.getRightLegRotation().getZ();
                    this.rightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
                    break;
            }
        } else {
            switch (this.slot) {
                case HEAD:
                    this.setRotateAngle(this.head, this.bipedHead.rotateAngleX, this.bipedHead.rotateAngleY, this.bipedHead.rotateAngleZ);
                    break;
                case CHEST:
                    this.setRotateAngle(this.body, this.bipedBody.rotateAngleX * 0.5F, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
                    this.setRotateAngle(this.leftArm, this.bipedLeftArm.rotateAngleX, this.bipedLeftArm.rotateAngleY, this.bipedLeftArm.rotateAngleZ);
                    this.setRotateAngle(this.rightArm, this.bipedRightArm.rotateAngleX, this.bipedRightArm.rotateAngleY, this.bipedRightArm.rotateAngleZ);
                    if (this.isSneak) {
                        this.leftArm.rotateAngleX += 0.2F;
                        this.rightArm.rotateAngleX += 0.2F;
                    }

                    this.leftArm.rotateAngleZ -= RenderSanPlayer.armTilt;
                    this.rightArm.rotateAngleZ += RenderSanPlayer.armTilt;
                    break;
                case LEGS:
                    this.setRotateAngle(this.body, this.bipedBody.rotateAngleX * 0.5F, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
                    //FALL-THROUGH
                case FEET:
                    if (this.isRiding) {
                        this.setRotateAngle(this.leftLeg, this.bipedLeftLeg.rotateAngleX, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
                        this.setRotateAngle(this.rightLeg, this.bipedRightLeg.rotateAngleX, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
                    } else {
                        this.setRotateAngle(this.leftLeg, this.bipedLeftLeg.rotateAngleX * 0.5F, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
                        this.setRotateAngle(this.rightLeg, this.bipedRightLeg.rotateAngleX * 0.5F, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
                    }

                    if (this.isSneak) {
                        this.leftLeg.rotationPointZ = 3.0F;
                        this.rightLeg.rotationPointZ = 3.0F;
                        this.leftLeg.rotateAngleX -= 0.15F;
                        this.rightLeg.rotateAngleX -= 0.15F;
                    } else {
                        this.leftLeg.rotationPointZ = 0.0F;
                        this.rightLeg.rotationPointZ = 0.0F;
                    }
                    break;
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.showModel = true);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public boolean isModelLoaded() {
        return this.modelJson.isLoaded();
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor> loader) {
        loader.load();

        switch( this.slot ) {
            case HEAD:
                this.head = loader.getBox("head");
                break;
            case CHEST:
                this.body = loader.getBox("body");
                this.leftArm = loader.getBox("leftArm");
                this.rightArm = loader.getBox("rightArm");
                break;
            case LEGS:
                this.body = loader.getBox("body");
                //FALL-THROUGH
            case FEET:
                this.leftLeg = loader.getBox("leftLeg");
                this.rightLeg = loader.getBox("rightLeg");
                break;
        }
    }

    @Override
    public void setTexture(String textureStr) {
        this.texture = new ResourceLocation(textureStr);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public float getBaseScale() {
        return this.scaling;
    }

    public static final class ModelDefault
            extends ModelBiped
    {
        public ModelDefault(float scale) {
            super(scale);
        }

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.pushMatrix();
            if( entityIn.isSneaking() ) {
                GlStateManager.translate(0.0F, -0.2F, 0.0F);
            }
            super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }

        public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
            super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);

            this.bipedBody.rotateAngleX *= 0.5F;
            if( this.isSneak ) {
                this.bipedRightLeg.rotationPointY = 11.0F;
                this.bipedLeftLeg.rotationPointY = 11.0F;
                this.bipedHead.rotationPointY = 0.0F;

                this.bipedLeftLeg.rotationPointZ = 3.0F;
                this.bipedRightLeg.rotationPointZ = 3.0F;
                this.bipedLeftLeg.rotateAngleX -= 0.15F;
                this.bipedRightLeg.rotateAngleX -= 0.15F;
                this.bipedLeftArm.rotateAngleX += 0.2F;
                this.bipedRightArm.rotateAngleX += 0.2F;
            } else {
                this.bipedLeftLeg.rotationPointZ = 0.0F;
                this.bipedRightLeg.rotationPointZ = 0.0F;
            }

            if( !this.isRiding ) {
                this.bipedLeftLeg.rotateAngleX *= 0.5F;
                this.bipedRightLeg.rotateAngleX *= 0.5F;
            }
        }
    }
}
