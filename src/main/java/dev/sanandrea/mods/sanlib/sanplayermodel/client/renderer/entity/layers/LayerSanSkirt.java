/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.sanlib.sanplayermodel.SanPlayerModel;
import dev.sanandrea.mods.sanlib.sanplayermodel.client.model.ModelSanSkirt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LayerSanSkirt<T extends PlayerEntity, M extends PlayerModel<T>>
        extends LayerRenderer<T, M>
{
    private final ModelSanSkirt<T> skirt      = new ModelSanSkirt<>(0.0F);
    private final ModelSanSkirt<T> skirtArmor = new ModelSanSkirt<>(0.5F);

    private final Map<Item, ResourceLocation> skirtArmorList     = new HashMap<>();
    private final Map<Item, ResourceLocation> skirtArmorOverlays = new HashMap<>();

    public LayerSanSkirt(IEntityRenderer<T, M> renderPlayer) {
        super(renderPlayer);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull T player,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if( SanPlayerModel.isSanPlayer(player) ) {
            ItemStack pants = player.getItemBySlot(EquipmentSlotType.LEGS);
            boolean hasPants = ItemStackUtils.isValid(pants);

            PlayerModel<T> pm = this.getParentModel();

            if( !hasPants || hasSkirtArmor(pants) ) {
                pm.copyPropertiesTo(this.skirt);
                renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirt, pm.body, false, null,
                            player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }

            if( hasPants ) {
                Item pantsItem = pants.getItem();
                boolean test = false;
                Integer overlay = null;

                if( pantsItem instanceof IDyeableArmorItem && ((IDyeableArmorItem) pantsItem).hasCustomColor(pants) ) {
                    overlay = ((IDyeableArmorItem) pantsItem).getColor(pants);
                }

                if( !this.skirtArmorList.containsKey(pantsItem) ) {
                    String path = String.format("textures/entity/player/%s", MiscUtils.apply(pants.getItem().getRegistryName(), ResourceLocation::toString, "").replace(":", "_"));
                    this.skirtArmorList.put(pantsItem, new ResourceLocation(Constants.PM_ID, path + ".png"));
                    test = true;

                    if( overlay != null ) {
                        this.skirtArmorOverlays.put(pantsItem, new ResourceLocation(Constants.PM_ID, path + "_overlay.png"));
                    }
                }

                ResourceLocation rl = this.skirtArmorList.get(pants.getItem());
                if( rl != null ) {
                    try {
                        if( test ) {
                            Minecraft.getInstance().getResourceManager().getResource(rl);
                        }

                        pm.copyPropertiesTo(this.skirtArmor);
                        this.skirtArmor.setTexture(rl);
                        renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirtArmor, pm.body, pants.hasFoil(), overlay,
                                    player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                        if( this.skirtArmorOverlays.containsKey(pantsItem) ) {
                            this.skirtArmor.setTexture(this.skirtArmorOverlays.get(pantsItem));
                            renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirtArmor, pm.body, pants.hasFoil(), null,
                                        player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                        }
                    } catch( IOException ex ) {
                        SanPlayerModel.LOG.log(Level.WARN, String.format("Cannot find texture for %s", rl));
                        this.skirtArmorList.put(pantsItem, null);
                    }
                }
            }
        }
    }

    private boolean hasSkirtArmor(ItemStack pants) {
        return this.skirtArmorList.get(pants.getItem()) != null;
    }

    private static void renderSkirt(MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ModelSanSkirt<?> skirt, ModelRenderer body,
                                    boolean glint, Integer overlay, PlayerEntity player, float limbSwing, float limbSwingAmount,
                                    float ageInTicks, float netHeadYaw, float headPitch)
    {
        float red   = 1.0F;
        float green = 1.0F;
        float blue  = 1.0F;

        if( overlay != null ) {
            red = (float) (overlay >> 16 & 255) / 255.0F;
            green = (float) (overlay >> 8 & 255) / 255.0F;
            blue = (float) (overlay & 255) / 255.0F;
        }

        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(skirt.getTexture()), false, glint);

        stack.pushPose();
        body.translateAndRotate(stack);
        skirt.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        skirt.renderToBuffer(stack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        stack.popPose();
    }
}
