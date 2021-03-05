package santest;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class TestBlockUnbakedModel
        implements IUnbakedModel
{
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return null;
    }

    @Override
    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return null;
    }

    @Nullable
    @Override
    public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
        return null;
    }
}
