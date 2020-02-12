////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayerArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class LayerSanStandardClothes
        implements LayerRenderer<EntityLivingBase>
{
    private final RenderSanPlayer renderer;
    private final Map<EntityEquipmentSlot, ModelSanPlayerArmor> armorModels;

    public float armTilt;
    public boolean hasCstChest;

    public LayerSanStandardClothes(RenderSanPlayer rendererIn) {
        this.renderer = rendererIn;
        this.armorModels = new EnumMap<>(EntityEquipmentSlot.class);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.armTilt = 0.0F;
        this.hasCstChest = false;

        boolean visibleOrOutline = !entitylivingbaseIn.isInvisible() || this.renderer.isOutlineRendering();
        boolean visibleToPlayer = !visibleOrOutline && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if( visibleOrOutline || visibleToPlayer ) {
            this.renderClothLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
            this.renderClothLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
            this.renderClothLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
            this.renderClothLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public void renderHand(EntityLivingBase entityLivingBaseIn, float scale, EnumHandSide hand) {
        ItemStack itemstack = LayerSanStandardClothes.getItemStackFromSlot(entityLivingBaseIn, EntityEquipmentSlot.CHEST);

        if( !(itemstack.getItem() instanceof ItemArmor) || ((ItemArmor) itemstack.getItem()).getEquipmentSlot() != EntityEquipmentSlot.CHEST ) {
            ModelSanPlayerArmor t = this.getArmorModelHook(EntityEquipmentSlot.CHEST);
            if( t == null ) {
                return;
            }
            this.renderer.bindTexture(t.getTexture());

            setFPHandRotation(entityLivingBaseIn, t, scale, hand);
        }
    }

    static void setFPHandRotation(EntityLivingBase entity, ModelSanPlayerArmor t, float scale, EnumHandSide hand) {
        t.swingProgress = 0.0F;
        t.isSneak = false;
        t.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale, entity);

        switch( hand ) {
            case RIGHT:
                t.bipedRightArm.rotateAngleX = 0.0F;
                t.bipedRightArm.rotateAngleZ = 0.1F;
                t.bipedRightArm.render(scale);
                break;
            case LEFT:
                t.bipedLeftArm.rotateAngleX = 0.0F;
                t.bipedLeftArm.rotateAngleZ = -0.1F;
                t.bipedLeftArm.render(scale);
                break;
        }
    }

    private void renderClothLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
        ItemStack itemstack = LayerSanStandardClothes.getItemStackFromSlot(entityLivingBaseIn, slotIn);

        if( !(itemstack.getItem() instanceof ItemArmor) || ((ItemArmor) itemstack.getItem()).getEquipmentSlot() != slotIn ) {
            ModelSanPlayerArmor t = this.getArmorModelHook(slotIn);
            if( t == null ) {
                return;
            }

            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.renderer.bindTexture(t.getTexture());

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    private ModelSanPlayerArmor getArmorModelHook(final EntityEquipmentSlot slot) {
        Supplier<ModelSanPlayerArmor> modelSupply = () -> {
            ResourceLocation resLoc = new ResourceLocation(Constants.PM_ID, String.format("models/entity/sanplayer_noarmor_%s.json", slot.getName()));
            ModelSanPlayerArmor armor = new ModelSanPlayerArmor(0.0F, resLoc, slot);
            LayerSanStandardClothes.this.armorModels.put(slot, armor);
            return armor;
        };

        if( slot == EntityEquipmentSlot.CHEST ) {
            this.hasCstChest = true;
        }

        if( this.armorModels.containsKey(slot) ) {
            ModelSanPlayerArmor armor = this.armorModels.get(slot);
            if( armor == null ) {
                armor = modelSupply.get();
            }
            if( armor.isModelLoaded() ) {
                this.armTilt = Math.max(this.armTilt, armor.getArmTilt());
                return armor;
            }
        } else {
            ModelSanPlayerArmor armor = modelSupply.get();
            if( armor.isModelLoaded() ) {
                return armor;
            }
        }

        if( slot == EntityEquipmentSlot.CHEST ) {
            this.hasCstChest = false;
        }

        return null;
    }

    @Nonnull
    private static ItemStack getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn) {
        return living.getItemStackFromSlot(slotIn);
    }
}
