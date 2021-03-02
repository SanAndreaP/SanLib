package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanSkirt;
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
            ItemStack pants = player.getItemStackFromSlot(EquipmentSlotType.LEGS);
            boolean hasPants = ItemStackUtils.isValid(pants);

            PlayerModel<T> pm = this.getEntityModel();

            if( !hasPants || hasSkirtArmor(pants) ) {
                pm.setModelAttributes(this.skirt);
                renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirt, pm.bipedBody, false, null,
                            player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }

            if( hasPants ) {
                Item pantsItem = pants.getItem();
                boolean test = false;
                Integer overlay = null;

                if( pantsItem instanceof IDyeableArmorItem && ((IDyeableArmorItem) pantsItem).hasColor(pants) ) {
                    overlay = ((IDyeableArmorItem) pantsItem).getColor(pants);
                }

                if( !this.skirtArmorList.containsKey(pantsItem) ) {
                    String path = String.format("textures/entity/player/%s", MiscUtils.applyNonNull(pants.getItem().getRegistryName(), ResourceLocation::toString, "").replace(":", "_"));
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

                        pm.setModelAttributes(this.skirtArmor);
                        this.skirtArmor.setTexture(rl);
                        renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirtArmor, pm.bipedBody, pants.hasEffect(), overlay,
                                    player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                        if( this.skirtArmorOverlays.containsKey(pantsItem) ) {
                            this.skirtArmor.setTexture(this.skirtArmorOverlays.get(pantsItem));
                            renderSkirt(matrixStackIn, bufferIn, packedLightIn, this.skirtArmor, pm.bipedBody, pants.hasEffect(), null,
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

        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(bufferIn, RenderType.getArmorCutoutNoCull(skirt.getTexture()), false, glint);

        stack.push();
        body.translateRotate(stack);
        skirt.setRotationAngles(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        skirt.render(stack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        stack.pop();
    }
}
