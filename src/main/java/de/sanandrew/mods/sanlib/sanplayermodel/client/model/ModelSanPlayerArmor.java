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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class ModelSanPlayerArmor
        extends ModelPlayer
        implements ModelJsonHandler<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor>
{
    private final float scaling;
    private ResourceLocation texture;
    private final ModelJsonLoader<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor> modelJson;
    private final EntityEquipmentSlot slot;

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
                    this.bipedHead.rotateAngleX = 0.017453292F * armorstand.getHeadRotation().getX();
                    this.bipedHead.rotateAngleY = 0.017453292F * armorstand.getHeadRotation().getY();
                    this.bipedHead.rotateAngleZ = 0.017453292F * armorstand.getHeadRotation().getZ();
                    this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
                    break;
                case CHEST:
                    this.bipedBody.rotateAngleX = 0.017453292F * armorstand.getBodyRotation().getX();
                    this.bipedBody.rotateAngleY = 0.017453292F * armorstand.getBodyRotation().getY();
                    this.bipedBody.rotateAngleZ = 0.017453292F * armorstand.getBodyRotation().getZ();
                    this.bipedLeftArm.rotateAngleX = 0.017453292F * armorstand.getLeftArmRotation().getX();
                    this.bipedLeftArm.rotateAngleY = 0.017453292F * armorstand.getLeftArmRotation().getY();
                    this.bipedLeftArm.rotateAngleZ = 0.017453292F * armorstand.getLeftArmRotation().getZ();
                    this.bipedRightArm.rotateAngleX = 0.017453292F * armorstand.getRightArmRotation().getX();
                    this.bipedRightArm.rotateAngleY = 0.017453292F * armorstand.getRightArmRotation().getY();
                    this.bipedRightArm.rotateAngleZ = 0.017453292F * armorstand.getRightArmRotation().getZ();
                    break;
                case LEGS:
                    this.bipedBody.rotateAngleX = 0.017453292F * armorstand.getBodyRotation().getX();
                    this.bipedBody.rotateAngleY = 0.017453292F * armorstand.getBodyRotation().getY();
                    this.bipedBody.rotateAngleZ = 0.017453292F * armorstand.getBodyRotation().getZ();
                    //FALL-THROUGH
                case FEET:
                    this.bipedLeftLeg.rotateAngleX = 0.017453292F * armorstand.getLeftLegRotation().getX();
                    this.bipedLeftLeg.rotateAngleY = 0.017453292F * armorstand.getLeftLegRotation().getY();
                    this.bipedLeftLeg.rotateAngleZ = 0.017453292F * armorstand.getLeftLegRotation().getZ();
                    this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
                    this.bipedRightLeg.rotateAngleX = 0.017453292F * armorstand.getRightLegRotation().getX();
                    this.bipedRightLeg.rotateAngleY = 0.017453292F * armorstand.getRightLegRotation().getY();
                    this.bipedRightLeg.rotateAngleZ = 0.017453292F * armorstand.getRightLegRotation().getZ();
                    this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
                    break;
            }
        } else {
            ModelSanPlayer.setSwimmingRotation(entity, this, limbSwing);

            switch (this.slot) {
                case CHEST:
                    this.bipedBody.rotateAngleX *= 0.5F;

                    if (this.isSneak) {
                        this.bipedLeftArm.rotateAngleX += 0.2F;
                        this.bipedRightArm.rotateAngleX += 0.2F;
                        this.bipedLeftArm.rotationPointZ = 1.5F;
                        this.bipedRightArm.rotationPointZ = 1.5F;
                        this.bipedLeftArm.rotateAngleZ -= 0.05D;
                        this.bipedRightArm.rotateAngleZ += 0.05D;
                    }

                    this.bipedLeftArm.rotateAngleZ -= RenderSanPlayer.armTilt;
                    this.bipedRightArm.rotateAngleZ += RenderSanPlayer.armTilt;

                    break;
                case LEGS:
                    this.bipedBody.rotateAngleX *= 0.5F;
                    //FALL-THROUGH
                case FEET:
                    if( !this.isRiding ) {
                        this.bipedLeftLeg.rotateAngleX *= 0.45F;
                        this.bipedRightLeg.rotateAngleX *= 0.45F;
                    }

                    if (this.isSneak) {
                        this.bipedLeftLeg.rotationPointZ = 3.0F;
                        this.bipedRightLeg.rotationPointZ = 3.0F;
                        this.bipedLeftLeg.rotationPointY = 10.0F;
                        this.bipedRightLeg.rotationPointY = 10.0F;
                        this.bipedLeftLeg.rotateAngleX -= 0.05F;
                        this.bipedRightLeg.rotateAngleX -= 0.05F;
                    } else {
                        ModelSanPlayer.setLegRotationPointY(this);
                        this.bipedLeftLeg.rotationPointZ = 0.0F;
                        this.bipedRightLeg.rotationPointZ = 0.0F;
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

    public boolean isModelLoaded() {
        return this.modelJson.isLoaded();
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanPlayerArmor, LayerSanArmor.ModelJsonArmor> loader) {
        loader.load();

        switch( this.slot ) {
            case HEAD:
                this.bipedHead = loader.getBox("head");
                break;
            case CHEST:
                this.bipedBody = loader.getBox("body");
                this.bipedLeftArm = loader.getBox("leftArm");
                this.bipedRightArm = loader.getBox("rightArm");
                break;
            case LEGS:
                this.bipedBody = loader.getBox("body");
                //FALL-THROUGH
            case FEET:
                this.bipedLeftLeg = loader.getBox("leftLeg");
                this.bipedRightLeg = loader.getBox("rightLeg");
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
