/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.resources.IResourceManager;

/**
 * An object that handles events during the loading of JSON-Models via the {@link ModelJsonLoader}.
 * @param <T> The type of the object itself. Must extend ModelBase and must implement {@link ModelJsonHandler}.
 * @param <U> The type of a {@link ModelJsonLoader.ModelJson} implementation.
 */
public interface ModelJsonHandler<T extends ModelBase & ModelJsonHandler<T, U>, U extends ModelJsonLoader.ModelJson>
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
     * Returns the scaling value for the {@link net.minecraft.client.model.ModelRenderer ModelRenderer} cubes to be used.
     * Default implementation returns {@code 0.0F}
     * @return the scaling value
     */
    default float getBaseScale() { return 0.0F; }
}
