package santest;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TestBlockBakedModel
        implements IBakedModel
{
    private IBakedModel original;

    public TestBlockBakedModel(IBakedModel original) {
        this.original = original;
    }

    @Override
    @Deprecated
    public @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        List<BakedQuad> quads = this.original.getQuads(state, side, rand);
//        List<BakedQuad> processedQuads
//        quads.
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.original.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return this.original.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    @Deprecated
    public @Nonnull TextureAtlasSprite getParticleTexture() {
        return this.original.getParticleTexture();
    }

    @Override
    @Deprecated
    public @Nonnull ItemOverrideList getOverrides() {
        return this.original.getOverrides();
    }
}
