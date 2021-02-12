package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers;

import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanSkirt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LayerSanSkirt
        implements LayerRenderer<EntityPlayer>
{
    protected final RenderPlayer renderPlayer;
    private final ModelSanSkirt skirt = new ModelSanSkirt(0.0F);
    private final ModelSanSkirt skirtArmor = new ModelSanSkirt(0.5F);

    private final Map<Item, ResourceLocation> skirtArmorList     = new HashMap<>();
    private final Map<Item, ResourceLocation> skirtArmorOverlays = new HashMap<>();

    public LayerSanSkirt(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if ( SanPlayerModel.isSanPlayer(player) ) {
            ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            boolean hasPants = ItemStackUtils.isValid(pants);

            if( !hasPants || hasSkirtArmor(pants) ) {
                renderSkirt(this.skirt, this.renderPlayer, false, null,
                            player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }

            if( hasPants ) {
                Item pantsItem = pants.getItem();
                boolean test = false;
                Integer overlay = null;

                if( pantsItem instanceof ItemArmor && ((ItemArmor) pantsItem).hasOverlay(pants) ) {
                    overlay = ((ItemArmor) pantsItem).getColor(pants);
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
                            Minecraft.getMinecraft().getResourceManager().getResource(rl);
                        }

                        this.skirtArmor.setTexture(rl);
                        renderSkirt(this.skirtArmor, this.renderPlayer, pants.isItemEnchanted(), overlay,
                                    player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

                        if( this.skirtArmorOverlays.containsKey(pantsItem) ) {
                            this.skirtArmor.setTexture(this.skirtArmorOverlays.get(pantsItem));
                            renderSkirt(this.skirtArmor, this.renderPlayer, pants.isItemEnchanted(), null,
                                        player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
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

    private static void renderSkirt(ModelSanSkirt skirt, RenderPlayer renderPlayer, boolean glint, Integer overlay, EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        skirt.setModelAttributes(renderPlayer.getMainModel());

        if( overlay != null ) {
            float red = (float) (overlay >> 16 & 255) / 255.0F;
            float green = (float) (overlay >> 8 & 255) / 255.0F;
            float blue = (float) (overlay & 255) / 255.0F;

            GlStateManager.color(red, green, blue, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        renderPlayer.bindTexture(skirt.getTexture());

        GlStateManager.pushMatrix();
        if (player.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        renderPlayer.getMainModel().bipedBody.postRender(0.0625F);
        skirt.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
        skirt.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        if( glint ) {
            LayerArmorBase.renderEnchantedGlint(renderPlayer, player, skirt, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
