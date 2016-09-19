package de.sanandrew.mods.sapmanpack.lib.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.resources.IResourceManager;

public interface ModelJsonHandler<T extends ModelBase & ModelJsonHandler<T, U>, U extends ModelJsonLoader.ModelJson>
{
    void onReload(IResourceManager resourceManager, ModelJsonLoader<T, U> loader);
    void setTexture(String textureStr);
    default float getBaseScale() { return 0.0F; }
}
