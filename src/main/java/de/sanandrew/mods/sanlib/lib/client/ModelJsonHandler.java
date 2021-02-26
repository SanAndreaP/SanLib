////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.resources.IResourceManager;

import java.util.List;

/**
 * An object that handles events during the loading of JSON-Models via the {@link ModelJsonLoader}.
 * @param <T> The type of the object itself. Must extend Model and must implement {@link ModelJsonHandler}.
 * @param <U> The type of a {@link ModelJsonLoader.JsonBase} implementation.
 */
public interface ModelJsonHandler<T extends Model & ModelJsonHandler<T, U>, U extends ModelJsonLoader.JsonBase>
{
    /**
     * Called when Minecraft (re)loads all resources (e.g. when the texture pack changes)
     * @param resourceManager the Resource Manager given by Minecraft
     * @param loader The JSON-Model-Loader instance
     */
    void onReload(IResourceManager resourceManager, ModelJsonLoader<T, U> loader);

    /**
     * Called when a JSON-Model defines a custom texture.
     * @param textureStr the texture location specified by the JSON model
     */
    void setTexture(String textureStr);

    /**
     * Returns the scaling value for the {@link net.minecraft.client.renderer.model.ModelRenderer} cubes to be used.
     * Default implementation returns {@code 0.0F}
     * @return the scaling value
     */
    default float getBaseScale() { return 0.0F; }

    List<ModelRenderer> getBoxes();
}
