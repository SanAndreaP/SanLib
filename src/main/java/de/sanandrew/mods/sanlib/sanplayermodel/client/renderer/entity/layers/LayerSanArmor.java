/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayerArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanArmorStand;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LayerSanArmor
        extends LayerBipedArmor
{
    private final RenderLivingBase<?> renderer;
    private final Table<String, EntityEquipmentSlot, ModelSanPlayerArmor> armorModels;

    public float armTilt;
    public boolean hasCstChest;

    public LayerSanArmor(RenderLivingBase<?> renderer) {
        super(renderer);

        this.renderer = renderer;
        this.armorModels = HashBasedTable.create();
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new ModelSanPlayerArmor.ModelDefault(0.2F);
        this.modelArmor = new ModelSanPlayerArmor.ModelDefault(0.4F);
    }

    public void renderHand(EntityLivingBase entity, float scale, EnumHandSide hand) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if( itemstack.getItem() instanceof ItemArmor ) {
            ItemArmor armorItem = (ItemArmor) itemstack.getItem();
            if( armorItem.getEquipmentSlot() == EntityEquipmentSlot.CHEST ) {
                ModelSanPlayerArmor t = this.getCustomArmorModel(itemstack, EntityEquipmentSlot.CHEST);
                if( t == null ) {
                    return;
                }
                this.renderer.bindTexture(t.getTexture());

                t.swingProgress = 0.0F;
                t.isSneak = false;
                t.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale, entity);

                switch( hand ) {
                    case RIGHT:
                        t.rightArm.rotateAngleX = 0.0F;
                        t.rightArm.rotateAngleZ = t.bipedRightArm.rotateAngleZ;
                        t.rightArm.render(scale);
                        break;
                    case LEFT:
                        t.leftArm.rotateAngleX = 0.0F;
                        t.leftArm.rotateAngleZ = t.bipedLeftArm.rotateAngleZ;
                        t.leftArm.render(scale);
                        break;
                }
            }
        }
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.armTilt = 0.0F;
        this.hasCstChest = false;
        super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn) {
        model.setVisible(false);

        if( model instanceof ModelSanPlayerArmor ) {
            ModelSanPlayerArmor modelSan = (ModelSanPlayerArmor) model;
            switch( slotIn ) {
                case HEAD:
                    modelSan.head.showModel = true;
                    if( this.renderer instanceof RenderSanArmorStand ) {
                        hideArmorStandBoxes(modelSan.head.childModels);
                    }
                    break;
                case CHEST:
                    modelSan.body.showModel = true;
                    modelSan.leftArm.showModel = true;
                    modelSan.rightArm.showModel = true;
                    if( this.renderer instanceof RenderSanArmorStand ) {
                        hideArmorStandBoxes(modelSan.body.childModels);
                        hideArmorStandBoxes(modelSan.leftArm.childModels);
                        hideArmorStandBoxes(modelSan.rightArm.childModels);
                    }
                    this.hasCstChest = true;
                    break;
                case LEGS:
                    modelSan.body.showModel = true;
                    modelSan.leftLeg.showModel = true;
                    modelSan.rightLeg.showModel = true;
                    if( this.renderer instanceof RenderSanArmorStand ) {
                        hideArmorStandBoxes(modelSan.body.childModels);
                        hideArmorStandBoxes(modelSan.leftLeg.childModels);
                        hideArmorStandBoxes(modelSan.rightLeg.childModels);
                    }
                    break;
                case FEET:
                    modelSan.leftLeg.showModel = true;
                    modelSan.rightLeg.showModel = true;
                    if( this.renderer instanceof RenderSanArmorStand ) {
                        hideArmorStandBoxes(modelSan.leftLeg.childModels);
                        hideArmorStandBoxes(modelSan.rightLeg.childModels);
                    }
                    break;
            }
        } else {
            if( slotIn == EntityEquipmentSlot.CHEST ) {
                this.hasCstChest = false;
            }
            super.setModelSlotVisible(model, slotIn);
        }
    }

    private static void hideArmorStandBoxes(List<ModelRenderer> childCubes) {
        if( childCubes != null ) {
            childCubes.forEach(cube -> { if( MiscUtils.defIfNull(cube.boxName, "").contains("noarmorstand_") ) cube.isHidden = true; });
        }
    }

    private ModelSanPlayerArmor getCustomArmorModel(@Nonnull ItemStack itemStack, EntityEquipmentSlot slot) {
        if( ItemStackUtils.isValid(itemStack) && itemStack.getItem() instanceof ItemArmor ) {
            String key = this.getKeyForArmor((ItemArmor) itemStack.getItem());
            if( this.armorModels.contains(key, slot) ) {
                ModelSanPlayerArmor armor = this.armorModels.get(key, slot);
                if( armor.isModelLoaded() ) {
                    return armor;
                }
            } else {
                ResourceLocation resLoc = new ResourceLocation(SanPlayerModel.ID, String.format("models/entity/armor/%s_%s.json", key.replace(':', '/'), slot.getName()));
                ModelSanPlayerArmor armor = new ModelSanPlayerArmor(0.0F, resLoc, slot);
                this.armorModels.put(key, slot, armor);
                if( armor.isModelLoaded() ) {
                    return armor;
                }
            }
        }

        return null;
    }

    @Override
    protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
        ModelSanPlayerArmor armor = getCustomArmorModel(itemStack, slot);
        if( armor != null ) {
            this.armTilt = Math.max(this.armTilt, armor.getArmTilt());
            return armor;
        }

        return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }

    @Override
    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type) {
        if( stack.getItem() instanceof ItemArmor ) {
            String key = this.getKeyForArmor((ItemArmor) stack.getItem());
            if( this.armorModels.contains(key, slot) ) {
                ModelSanPlayerArmor armor = this.armorModels.get(key, slot);
                if( armor.isModelLoaded() ) {
                    return armor.getTexture();
                }
            }
        }

        return super.getArmorResource(entity, stack, slot, type);
    }

    private String getKeyForArmor(ItemArmor item) {
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if( idx != -1 ) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        return String.format("%s:%s", domain, texture);
    }

    public static final class ModelJsonArmor
            extends ModelJsonLoader.ModelJson
    {
        public float armTilt;
    }
}
