package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.sanplayermodel.Resources;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ModelSanSkirt<T extends PlayerEntity>
        extends BipedModel<T>
        implements ModelJsonHandler<ModelSanSkirt<T>, ModelJsonLoader.JsonBase>
{
    private final ModelJsonLoader<ModelSanSkirt<T>, ModelJsonLoader.JsonBase> modelJson;

    private final float scale;

    private ResourceLocation texture;
    private ModelRenderer    skirt1;
    private ModelRenderer    skirt2;

    public ModelSanSkirt(float scale) {
        super(scale);
        this.scale = scale;

        this.modelJson = ModelJsonLoader.create(this, Resources.SKIRT_MODEL, "skirt1", "skirt2");
    }

    @Override
    public float getBaseScale() {
        return this.scale;
    }

    @Override
    public List<ModelRenderer> getBoxes() {
        return null;
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
                       float red, float green, float blue, float alpha)
    {
        if( this.modelJson != null && this.modelJson.isLoaded() ) {
            Arrays.asList(this.modelJson.getMainBoxes()).forEach(
                    (box) -> box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha)
            );
        }
    }

    @Override
    public void setupAnim(@Nonnull PlayerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if( this.crouching ) {
            this.skirt1.z = 3.0F;
            this.skirt1.y = -0.6F;
            this.skirt2.z = 5.0F;
            this.skirt2.y = -1.0F;

            this.skirt1.xRot = -0.29F;
            this.skirt2.xRot = -0.43F;
        } else {
            this.skirt1.z = 0.0F;
            this.skirt2.z = 0.0F;
            this.skirt1.y = 0.0F;
            this.skirt2.y = 0.0F;

            this.skirt1.xRot = 0.0F;
            this.skirt2.xRot = 0.0F;
        }
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanSkirt<T>, ModelJsonLoader.JsonBase> loader) {
        loader.load();

        if( loader.isLoaded() ) {
            this.skirt1 = loader.getBox("skirt1");
            this.skirt2 = loader.getBox("skirt2");
        }
    }

    public void setTexture(ResourceLocation rl) {
        this.texture = rl;
    }

    @Override
    public void setTexture(String textureStr) {
        this.texture = new ResourceLocation(textureStr);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }
}
