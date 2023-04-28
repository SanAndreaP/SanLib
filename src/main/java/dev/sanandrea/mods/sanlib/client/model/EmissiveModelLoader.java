/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.client.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EmissiveModelLoader
    implements IModelLoader<EmissiveProxy>
{
    private static final String EMISSIVE_KEY = "emissive";

    @SubscribeEvent
    public static void onModelLoader(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.ID, EMISSIVE_KEY), new EmissiveModelLoader());
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) { /* no-op */ }

    @Override
    @Nonnull
    public EmissiveProxy read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        List<EmissiveBlockPart> list = this.getModelElements(deserializationContext, modelContents);
        return new EmissiveProxy(list);
    }

    private List<EmissiveBlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
        List<EmissiveBlockPart> list = Lists.newArrayList();

        if( object.has("elements") ) {
            for( JsonElement jsonelement : JSONUtils.getAsJsonArray(object, "elements") ) {
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
            super(delegate.from, delegate.to, delegate.faces, delegate.rotation, delegate.shade);
            this.emissive = emissive;
        }
    }

    public static class EmissiveBlockPartDeserializer
            implements JsonDeserializer<EmissiveBlockPart>
    {
        @Override
        public EmissiveBlockPart deserialize(JsonElement elem, Type type, JsonDeserializationContext context) throws JsonParseException {
            Map<Direction, Boolean> emissiveFaces = new EnumMap<>(Direction.class);
            JsonObject              elemObj       = JSONUtils.convertToJsonObject(elem, "element");

            if( elemObj.has(EMISSIVE_KEY) ) {
                boolean emissive = JsonUtils.getBoolVal(elemObj.get(EMISSIVE_KEY));
                for( Direction dir : Direction.values() ) {
                    emissiveFaces.put(dir, emissive);
                }
            } else {
                JsonObject faces = JSONUtils.getAsJsonObject(elemObj, "faces");
                for( Direction dir : Direction.values() ) {
                    String dirName = dir.getName();
                    if( faces.has(dirName) ) {
                        JsonObject dirData = faces.getAsJsonObject(dirName);
                        emissiveFaces.put(dir, JsonUtils.getBoolVal(dirData.get(EMISSIVE_KEY), false));
                    }
                }
            }


            return new EmissiveBlockPart(new BlockPart.Deserializer().deserialize(elem, type, context), emissiveFaces);
        }
    }

}
