////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//todo: look up what AE2 does for their emissive models
@SuppressWarnings("deprecation")
public class EmissiveModel
//        implements IUnbakedModel
{

//    private final IUnbakedModel               parent;
//    private final ModelLoader                 loader;
//    private final Map<BlockPartFace, Boolean> emissiveFlags = new HashMap<>();
//
//    EmissiveModel(ResourceLocation location, IResourceManager resMgr, ModelLoader loader) {
//        this.loader = loader;
//
//        ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(resMgr, getArmatureLocation(location));
//        BlockModel model = null;
//
//        try( IResource res = Minecraft.getInstance()
//                                      .getResourceManager()
//                                      .getResource(new ResourceLocation(location.getNamespace(), location.getPath() + ".json"));
//             Reader reader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8) )
//        {
//            Gson modelSerializer = new GsonBuilder().registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer())
//                                                    .registerTypeAdapter(BlockPart.class, new EmissiveBlockPartDeserializer())
//                                                    .registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
//                                                    .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
//                                                    .registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
//                                                    .registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
//                                                    .registerTypeAdapter(ItemOverrideList.class, new ItemOverride.Deserializer())
//                                                    .create();
//
//            model = JsonUtils.gsonDeserialize(modelSerializer, reader, BlockModel.class, false);
//            Objects.requireNonNull(model).name = location.toString();
//        } catch( IOException | NullPointerException e ) {
//            SanLib.LOG.log(Level.ERROR, "Cannot deserialize model JSON", e);
//        }
//
////        this.parent = new ItemLayerModel(model);
////        this.parent = ReflectionUtils.invokeCachedMethod(ModelLoader.class, this.loader, "loadModel", "loadModel", new Class[] {
////                            ResourceLocation.class
////                      }, new Object[] { location });
//        this.parent = getVanillaModelWrapper(loader, location, model, animation);
//    }
//
//    @Override
//    public Collection<ResourceLocation> getDependencies() {
//        return this.parent.getDependencies();
//    }
//
//    @Override
//    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> function, Set<Pair<String, String>> set) {
//        return this.parent.getTextures(function, set);
//    }
//
//    @Nullable
//    @Override
//    public IBakedModel bakeModel(ModelBakery modelBakery, Function<RenderMaterial, TextureAtlasSprite> function, IModelTransform iModelTransform, ResourceLocation resourceLocation) {
//        FaceBakery prevBakery = this.loader.
//        return null;
//    }
//
//    @Override
//    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter ) {
//        FaceBakery prevBakery = this.loader.faceBakery;
//        this.loader.faceBakery = new EmissiveFaceBakery();
//        IBakedModel model = this.parent.bake( state, format, bakedTextureGetter );
//        this.loader.faceBakery = prevBakery;
//
//        return model;
//    }
//
//    @Override
//    public IModelState getDefaultState() {
//        return this.parent.getDefaultState();
//    }
//
//    private static ResourceLocation getArmatureLocation(ResourceLocation location) {
//        String p = location.getPath();
//        return new ResourceLocation(location.getNamespace(), p.startsWith("models/") ? p.substring("models/".length()) : p);
//    }
//
//    private static <M extends IModel> M getVanillaModelWrapper(ModelLoader modelLoader, ResourceLocation location, ModelBlock model, ModelBlockAnimation animation) {
//        Object vmw = ReflectionUtils.getNew(ModelLoader.class.getName() + "$VanillaModelWrapper",
//                                            new Class[] {ModelLoader.class, ResourceLocation.class, ModelBlock.class, boolean.class, ModelBlockAnimation.class},
//                                            modelLoader, location, model, false, animation);
//
//        return ReflectionUtils.getCasted(vmw);
//    }
//
//    class EmissiveBlockPartDeserializer
//            implements JsonDeserializer<BlockPart>
//    {
//        private final JsonDeserializer<BlockPart> parent = SanLibDeserializers.getForBlockPart();
//
//        @Override
//        public BlockPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            BlockPart part = this.parent.deserialize(json, typeOfT, context);
//
//            part.mapFaces.forEach((face, partFace) -> EmissiveModel.this.emissiveFlags.put(partFace, JsonUtils.getBoolVal(json.getAsJsonObject().get("emissive"), false)));
//
//            return part;
//        }
//    }
//
//    public class EmissiveFaceBakery
//           extends FaceBakery
//    {
//        @Override
//        public BakedQuad bakeQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, Direction direction, IModelTransform modelRotation, @Nullable BlockPartRotation partRotation, boolean uvLocked, ResourceLocation p_228824_9_) {
//            BakedQuad quad = super.bakeQuad(posFrom, posTo, face, sprite, direction, modelRotation, partRotation, uvLocked, p_228824_9_);
//
//            if( EmissiveModel.this.emissiveFlags.computeIfAbsent(face, f -> false) ) {
//
//                VertexFormat              newFormat = getFormatWithLightMap( quad.getVertexData() );
//                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newFormat );
//                VertexLighterFlat         vlf = new VertexLighterFlat(Minecraft.getInstance().getBlockColors() ) {
//                    @Override
//                    protected void updateLightmap( float[] normal, float[] lightmap, float x, float y, float z ) {
//                        lightmap[0] = 0.007F;
//                        lightmap[1] = 0.007F;
//                    }
//                };
//
//                vlf.setParent( builder );
//                quad.pipe( vlf );
//                builder.setQuadTint( quad.getTintIndex() );
//                builder.setQuadOrientation( quad.getFace() );
//                builder.setTexture( quad.getSprite() );
//                builder.setApplyDiffuseLighting( false );
//
//                return builder.build();
//            } else {
//                return quad;
//            }
//        }
//
//        @Override
//        public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ITransformation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade )
//        {
//            BakedQuad quad = super.makeBakedQuad(posFrom, posTo, face, sprite, facing, modelRotationIn, partRotation, uvLocked, shade);
//
//            if( EmissiveModel.this.emissiveFlags.computeIfAbsent(face, f -> false) ) {
//                VertexFormat              newFormat = getFormatWithLightMap( quad.getFormat() );
//                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newFormat );
//                VertexLighterFlat         vlf = new VertexLighterFlat(Minecraft.getInstance().getBlockColors() ) {
//                                                    @Override
//                                                    protected void updateLightmap( float[] normal, float[] lightmap, float x, float y, float z ) {
//                                                        lightmap[0] = 0.007F;
//                                                        lightmap[1] = 0.007F;
//                                                    }
//                                                };
//
//                vlf.setParent( builder );
//                quad.pipe( vlf );
//                builder.setQuadTint( quad.getTintIndex() );
//                builder.setQuadOrientation( quad.getFace() );
//                builder.setTexture( quad.getSprite() );
//                builder.setApplyDiffuseLighting( false );
//
//                return builder.build();
//            } else {
//                return quad;
//            }
//        }
//    }
//
//    private static final VertexFormat ITEM_FORMAT_WITH_LIGHTMAP = new VertexFormat(DefaultVertexFormats.ITEM).addElement(DefaultVertexFormats.TEX_2S);
//    @SuppressWarnings("ObjectEquality")
//    private static VertexFormat getFormatWithLightMap(VertexFormat format) {
//        if( !EmissiveModelLoader.isLightMapEnabled() ) {
//            return format;
//        }
//
//        if( format == DefaultVertexFormats.BLOCK ) {
//            return DefaultVertexFormats.BLOCK;
//        } else if( format == DefaultVertexFormats.ITEM ) {
//            return ITEM_FORMAT_WITH_LIGHTMAP;
//        } else if( !format.hasUvOffset(1) ) {
//            return new VertexFormat(format).addElement(DefaultVertexFormats.TEX_2S);
//        }
//
//        return format;
//    }
//
//    @Override
//    public Optional<ModelBlock> asVanillaModel() {
//        return this.parent.asVanillaModel();
//    }
}
