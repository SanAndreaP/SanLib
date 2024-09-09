package dev.sanandrea.mods.sanlib.client.layer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sanandrea.mods.sanlib.client.Resources;
import dev.sanandrea.mods.sanlib.client.model.SanSkirtModel;
import dev.sanandrea.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.sanlib.lib.util.UuidUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class SanSkirtLayer<T extends Player, M extends HumanoidModel<T>>
        extends RenderLayer<T, M>
{
    private static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreaP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    private final SanSkirtModel model;
    private final SanSkirtModel modelArmor;

    public SanSkirtLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new SanSkirtModel(modelSet.bakeLayer(Resources.SKIRT_MODEL_ID));
        this.modelArmor = new SanSkirtModel(modelSet.bakeLayer(Resources.SKIRT_MODEL_ARMOR_ID));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T player,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if( !isSanPlayer(player) ) {
            return;
        }

        ItemStack pants = player.getItemBySlot(EquipmentSlot.LEGS);

        this.model.riding = this.modelArmor.riding = this.getParentModel().riding;
        this.model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer c = buffer.getBuffer(this.model.renderType(Resources.SKIRT_TEXTURE));
        this.model.renderToBuffer(poseStack, c, packedLight, OverlayTexture.NO_OVERLAY, -1);

        if( ItemStackUtils.isValid(pants) && pants.getItem() instanceof ArmorItem pantsArmor ) {
            this.modelArmor.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            this.modelArmor.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            ArmorMaterial         armormaterial = pantsArmor.getMaterial().value();
            IClientItemExtensions extensions    = IClientItemExtensions.of(pants);
            int                   fallbackColor = extensions.getDefaultDyeColor(pants);

            for( int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++ ) {
                Optional<ResourceLocation> skirtArmorTexture = Resources.getSkirtArmorTexture(pantsArmor, layerIdx);
                if( skirtArmorTexture.isEmpty() ) {
                    continue;
                }

                ArmorMaterial.Layer layer     = armormaterial.layers().get(layerIdx);
                int                 tintColor = extensions.getArmorLayerTintColor(pants, player, layer, layerIdx, fallbackColor);
                if( tintColor != 0 ) {
                    VertexConsumer cArmor = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(skirtArmorTexture.get()), pants.hasFoil());
                    this.modelArmor.renderToBuffer(poseStack, cArmor, packedLight, OverlayTexture.NO_OVERLAY, tintColor);
                }
            }
        }
    }

    public static <T extends Player> boolean isSanPlayer(T e) {
        for( String val : SANPLAYER_NAMES_UUID ) {
            GameProfile profile = e.getGameProfile();
            if( (UuidUtils.isStringUuid(val) && UUID.fromString(val).equals(profile.getId())) || profile.getName().equals(val) ) {
                return true;
            }
        }

        return false;
    }
}
