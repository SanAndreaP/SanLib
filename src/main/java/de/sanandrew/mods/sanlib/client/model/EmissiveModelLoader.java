////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.model;

import de.sanandrew.mods.sanlib.SLibConfig;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;

public class EmissiveModelLoader
        implements ICustomModelLoader
{

    private IResourceManager resourceManager;

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(@Nonnull ResourceLocation modelLocation) {
        if( !SLibConfig.Client.enableEmissiveTextures ) {
            return false;
        }

        try( IResource resource = Minecraft.getMinecraft()
                                           .getResourceManager()
                                           .getResource(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath() + ".json"));
             InputStreamReader io = new InputStreamReader(resource.getInputStream()) )
        {
            return JsonUtils.GSON.fromJson(io, EmissiveMarker.class).sanlib_emissive_marker;
        } catch( Exception ignored ) { }

        return false;
    }

    @Override
    @Nonnull
    public IModel loadModel(@Nonnull ResourceLocation modelLocation) {
        try {
            return new EmissiveModel(modelLocation, this.resourceManager, getVanillaModelLoader());
        } catch( ClassNotFoundException ex ) {
            return ModelLoaderRegistry.getMissingModel();
        }
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ModelLoader getVanillaModelLoader() throws ClassNotFoundException {
        Class c = Class.forName(ModelLoader.class.getName() + "$VanillaLoader");
        Object inst = ReflectionUtils.getCachedFieldValue(c, null, "INSTANCE", "INSTANCE");
        Object ml = ReflectionUtils.getCachedFieldValue(c, inst, "loader", "loader");

        return ReflectionUtils.getCasted(ml);
    }

    public static boolean isLightMapEnabled() {
        return !FMLClientHandler.instance().hasOptifine() && ForgeModContainer.forgeLightPipelineEnabled;
    }

    @SuppressWarnings({ "WeakerAccess", "FieldNamingConvention" })
    static class EmissiveMarker
    {
        boolean sanlib_emissive_marker = false;
    }
}
