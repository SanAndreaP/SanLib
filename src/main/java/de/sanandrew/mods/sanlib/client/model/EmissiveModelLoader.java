////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
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
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EmissiveModelLoader
    implements IModelLoader<EmissiveModelLoader.EmissiveProxy>
{
    @SubscribeEvent
    public static void onModelLoader(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.ID, "emissive"), new EmissiveModelLoader());
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

    }

    @Override
    @Nonnull
    public EmissiveModelLoader.EmissiveProxy read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents)
    {
        List<EmissiveBlockPart> list = this.getModelElements(deserializationContext, modelContents);
        return new EmissiveProxy(list);
    }

    private List<EmissiveBlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
        List<EmissiveBlockPart> list = Lists.newArrayList();
        if( object.has("elements") ) {
            for( JsonElement jsonelement : JSONUtils.getJsonArray(object, "elements") ) {
                list.add(new EmissiveBlockPartDeserializer().deserialize(jsonelement, BlockPart.class, deserializationContext));
            }
        }

        return list;
    }

    public static class EmissiveBlockPart
            extends BlockPart
    {
        final Map<Direction, Boolean> emissive;

        public EmissiveBlockPart(BlockPart delegate, Map<Direction, Boolean> emissive) {
            super(delegate.positionFrom, delegate.positionTo, delegate.mapFaces, delegate.partRotation, delegate.shade);
            this.emissive = emissive;
        }
    }

    public static class EmissiveBlockPartDeserializer
            implements JsonDeserializer<EmissiveBlockPart>
    {
        @Override
        public EmissiveBlockPart deserialize(JsonElement elem, Type type, JsonDeserializationContext context) throws JsonParseException {
            Map<Direction, Boolean> emissiveFaces = new EnumMap<>(Direction.class);
            JsonObject              elemObj       = JSONUtils.getJsonObject(elem, "element");
            if( elemObj.has("emissive") ) {
                boolean emissive = JsonUtils.getBoolVal(elemObj.get("emissive"));
                for( Direction dir : Direction.values() ) {
                    emissiveFaces.put(dir, emissive);
                }
            } else {
                JsonObject faces = JSONUtils.getJsonObject(elemObj, "faces");
                for( Direction dir : Direction.values() ) {
                    String dirName = dir.getName2();
                    if( faces.has(dirName) ) {
                        JsonObject dirData = faces.getAsJsonObject(dirName);
                        emissiveFaces.put(dir, JsonUtils.getBoolVal(dirData.get("emissive"), false));
                    }
                }
            }


            return new EmissiveBlockPart(new BlockPart.Deserializer().deserialize(elem, type, context), emissiveFaces);
        }
    }

    static class EmissiveProxy
            implements ISimpleModelGeometry<EmissiveProxy>
    {
        private final List<EmissiveBlockPart> elements;

        public EmissiveProxy(List<EmissiveBlockPart> list)
        {
            this.elements = list;
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            for( EmissiveBlockPart blockpart : elements ) {
                for( Direction direction : blockpart.mapFaces.keySet() ) {
                    BlockPartFace      blockpartface = blockpart.mapFaces.get(direction);
                    TextureAtlasSprite sprite        = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));
                    if( blockpartface.cullFace == null ) {
                        modelBuilder.addGeneralQuad(bakeQuad(blockpart, blockpartface, sprite, direction, modelTransform, modelLocation));
                    } else {
                        modelBuilder.addFaceQuad(
                                modelTransform.getRotation().rotateTransform(blockpartface.cullFace),
                                bakeQuad(blockpart, blockpartface, sprite, direction, modelTransform, modelLocation));
                    }
                }
            }
        }

        private BakedQuad bakeQuad(EmissiveBlockPart part, BlockPartFace face, TextureAtlasSprite sprite, Direction direction, IModelTransform modelTransform, ResourceLocation modelLocation) {
            BakedQuad quad = BlockModel.makeBakedQuad(part, face, sprite, direction, modelTransform, modelLocation);

            if( part.emissive.get(direction) ) {
                BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

                VertexLighterFlat vlf = new VertexLighterFlat(Minecraft.getInstance().getBlockColors())
                {
                    @Override
                    protected void updateLightmap(@Nonnull float[] normal, float[] lightmap, float x, float y, float z) {
                        lightmap[0] = lightmap[1] = Math.nextAfter(1.0F / 136.52917F, 32767);
                    }
                };

                vlf.setParent(builder);
                vlf.setTransform(new MatrixStack().getLast());
                quad.pipe(vlf);
                builder.setQuadTint(quad.getTintIndex());
                builder.setQuadOrientation(quad.getFace());
                builder.setTexture(quad.getSprite());
                builder.setApplyDiffuseLighting(false);

                return builder.build();
            } else {
                return quad;
            }
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            Set<RenderMaterial> textures = Sets.newHashSet();

            for( BlockPart part : elements ) {
                for( BlockPartFace face : part.mapFaces.values() ) {
                    RenderMaterial texture = owner.resolveTexture(face.texture);
                    if( Objects.equals(texture.getTextureLocation(), MissingTextureSprite.getLocation()) ) {
                        missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                    }

                    textures.add(texture);
                }
            }

            return textures;
        }
    }
}
