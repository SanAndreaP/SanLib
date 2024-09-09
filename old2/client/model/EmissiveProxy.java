/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.client.model;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

class EmissiveProxy
        implements ISimpleModelGeometry<EmissiveProxy>
{
    private final List<EmissiveModelLoader.EmissiveBlockPart> elements;

    public EmissiveProxy(List<EmissiveModelLoader.EmissiveBlockPart> list) {
        this.elements = list;
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
    {
        for( EmissiveModelLoader.EmissiveBlockPart blockpart : elements ) {
            for( Direction direction : blockpart.faces.keySet() ) {
                BlockPartFace      blockpartface = blockpart.faces.get(direction);
                TextureAtlasSprite sprite        = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));

                if( blockpartface.cullForDirection == null ) {
                    modelBuilder.addGeneralQuad(bakeQuad(blockpart, blockpartface, sprite, direction, modelTransform, modelLocation));
                } else {
                    modelBuilder.addFaceQuad(modelTransform.getRotation().rotateTransform(blockpartface.cullForDirection),
                                             bakeQuad(blockpart, blockpartface, sprite, direction, modelTransform, modelLocation));
                }
            }
        }
    }

    private BakedQuad bakeQuad(EmissiveModelLoader.EmissiveBlockPart part, BlockPartFace face, TextureAtlasSprite sprite, Direction direction,
                               IModelTransform modelTransform, ResourceLocation modelLocation)
    {
        BakedQuad quad = BlockModel.makeBakedQuad(part, face, sprite, direction, modelTransform, modelLocation);

        if( Boolean.TRUE.equals(part.emissive.get(direction)) ) {
            BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

            VertexLighterFlat vlf = new VertexLighterFlat(Minecraft.getInstance().getBlockColors())
            {
                @Override
                protected void updateLightmap(@Nonnull float[] normal, float[] lightmap, float x, float y, float z) {
                    lightmap[0] = lightmap[1] = Math.nextAfter(1.0F / 136.52917F, 32767);
                }
            };

            vlf.setParent(builder);
            vlf.setTransform(new MatrixStack().last());
            quad.pipe(vlf);
            builder.setQuadTint(quad.getTintIndex());
            builder.setQuadOrientation(quad.getDirection());
            builder.setTexture(quad.getSprite());
            builder.setApplyDiffuseLighting(false);

            return builder.build();
        } else {
            return quad;
        }
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                                  Set<Pair<String, String>> missingTextureErrors)
    {
        Set<RenderMaterial> textures = Sets.newHashSet();

        for( BlockPart part : elements ) {
            for( BlockPartFace face : part.faces.values() ) {
                RenderMaterial texture = owner.resolveTexture(face.texture);
                if( Objects.equals(texture.texture(), MissingTextureSprite.getLocation()) ) {
                    missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                }

                textures.add(texture);
            }
        }

        return textures;
    }
}
