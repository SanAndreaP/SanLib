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
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.SanLibDeserializers;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.logging.log4j.Level;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class EmissiveModel
        implements IModel
{

    private final IModel                      parent;
    private final ModelLoader                 loader;
    private final Map<BlockPartFace, Boolean> emissiveFlags = new HashMap<>();

    EmissiveModel(ResourceLocation location, IResourceManager resMgr, ModelLoader loader) {
        this.loader = loader;

        ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(resMgr, getArmatureLocation(location));
        ModelBlock model = null;

        try( IResource res = Minecraft.getMinecraft()
                                      .getResourceManager()
                                      .getResource(new ResourceLocation(location.getNamespace(), location.getPath() + ".json"));
             Reader reader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8) )
        {
            Gson modelSerializer = new GsonBuilder().registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer())
                                                    .registerTypeAdapter(BlockPart.class, new EmissiveBlockPartDeserializer())
                                                    .registerTypeAdapter(BlockPartFace.class, SanLibDeserializers.getForBlockPartFace())
                                                    .registerTypeAdapter(BlockFaceUV.class, SanLibDeserializers.getForBlockFaceUV())
                                                    .registerTypeAdapter(ItemTransformVec3f.class, SanLibDeserializers.getForItemTransformVec3f())
                                                    .registerTypeAdapter(ItemCameraTransforms.class, SanLibDeserializers.getForItemCameraTransforms())
                                                    .registerTypeAdapter(ItemOverrideList.class, SanLibDeserializers.getForItemOverride())
                                                    .create();

            model = JsonUtils.gsonDeserialize(modelSerializer, reader, ModelBlock.class, false);
            Objects.requireNonNull(model).name = location.toString();
        } catch( IOException | NullPointerException e ) {
            SanLib.LOG.log(Level.ERROR, "Cannot deserialize model JSON", e);
        }

        this.parent = getVanillaModelWrapper(loader, location, model, animation );
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.parent.getDependencies();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return this.parent.getTextures();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter ) {
        FaceBakery prevBakery = this.loader.faceBakery;
        this.loader.faceBakery = new EmissiveFaceBakery();
        IBakedModel model = this.parent.bake( state, format, bakedTextureGetter );
        this.loader.faceBakery = prevBakery;
        return model;
    }

    @Override
    public IModelState getDefaultState() {
        return this.parent.getDefaultState();
    }

    private static ResourceLocation getArmatureLocation(ResourceLocation location) {
        String p = location.getPath();
        return new ResourceLocation(location.getNamespace(), p.startsWith("models/") ? p.substring("models/".length()) : p);
    }

    private static <M extends IModel> M getVanillaModelWrapper(ModelLoader modelLoader, ResourceLocation location, ModelBlock model, ModelBlockAnimation animation) {
        Object vmw = ReflectionUtils.getNew("net.minecraftforge.client.model.ModelLoader$VanillaModelWrapper",
                                            new Class[] {ResourceLocation.class, ModelBlock.class, boolean.class, ModelBlockAnimation.class},
                                            modelLoader, location, model, false, animation);

        return ReflectionUtils.getCasted(vmw);
    }

    class EmissiveBlockPartDeserializer
            implements JsonDeserializer<BlockPart>
    {
        private final JsonDeserializer<BlockPart> parent = SanLibDeserializers.getForBlockPart();

        @Override
        public BlockPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BlockPart part = this.parent.deserialize(json, typeOfT, context);

            part.mapFaces.forEach((face, partFace) -> EmissiveModel.this.emissiveFlags.put(partFace, JsonUtils.getBoolVal(json.getAsJsonObject().get("emissive"))));

            return part;
        }
    }

//    @SideOnly(Side.CLIENT)
//    static class EmissiveBlockPartFaceDeserializer
//            implements JsonDeserializer<BlockPartFace>
//    {
//        @Override
//        public BlockPartFace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            JsonObject  jObj = json.getAsJsonObject();
//            EnumFacing  cullFace = parseCullFace(jObj);
//            int         tintIndex = JsonUtils.getInt(jObj, "tintindex", -1);
//            String      texture = JsonUtils.getString(jObj, "texture");
//            BlockFaceUV faceUV = context.deserialize(jObj, BlockFaceUV.class);
//
//            return new BlockPartFace(cullFace, tintIndex, texture, faceUV);
//        }
//
//        @Nullable
//        private static EnumFacing parseCullFace(JsonObject object) {
//            String s = JsonUtils.getString(object, "cullface", "");
//            return EnumFacing.byName(s);
//        }
//    }

    public class EmissiveFaceBakery
           extends FaceBakery
    {

        @Override
        public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ITransformation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade )
        {
            BakedQuad quad = super.makeBakedQuad(posFrom, posTo, face, sprite, facing, modelRotationIn, partRotation, uvLocked, shade);

            if( EmissiveModel.this.emissiveFlags.computeIfAbsent(face, f -> false) ) {
                VertexFormat              newFormat = getFormatWithLightMap( quad.getFormat() );
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newFormat );
                VertexLighterFlat         vlf = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors() ) {
                                                    @Override
                                                    protected void updateLightmap( float[] normal, float[] lightmap, float x, float y, float z ) {
                                                        lightmap[0] = 0.007F;
                                                        lightmap[1] = 0.007F;
                                                    }
//                                                    @Override
//                                                    public void setQuadTint( int tint ) {
//                                                        // Tint requires a block state which we don't have at this point
//                                                    }
                                                };

                vlf.setParent( builder );
                quad.pipe( vlf );
                builder.setQuadTint( quad.getTintIndex() );
                builder.setQuadOrientation( quad.getFace() );
                builder.setTexture( quad.getSprite() );
                builder.setApplyDiffuseLighting( false );

                return builder.build();
            } else {
                return quad;
            }
        }
    }

    private static final VertexFormat ITEM_FORMAT_WITH_LIGHTMAP = new VertexFormat(DefaultVertexFormats.ITEM).addElement(DefaultVertexFormats.TEX_2S);
    @SuppressWarnings("ObjectEquality")
    private static VertexFormat getFormatWithLightMap(VertexFormat format) {
        if( isLightMapDisabled() ) {
            return format;
        }

        if( format == DefaultVertexFormats.BLOCK ) {
            return DefaultVertexFormats.BLOCK;
        } else if( format == DefaultVertexFormats.ITEM ) {
            return ITEM_FORMAT_WITH_LIGHTMAP;
        } else if( !format.hasUvOffset(1) ) {
            VertexFormat result = new VertexFormat(format);

            result.addElement(DefaultVertexFormats.TEX_2S);

            return result;
        }

        return format;
    }

    private static boolean isLightMapDisabled() {
        return FMLClientHandler.instance().hasOptifine() || !ForgeModContainer.forgeLightPipelineEnabled;
    }
}
