package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.sanplayermodel.Resources;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ModelSanSkirt
        extends ModelBase
        implements ModelJsonHandler<ModelSanSkirt, ModelJsonLoader.ModelJson>
{
    private final ModelJsonLoader<ModelSanSkirt, ModelJsonLoader.ModelJson> modelJson;
    private final float scale;

    private ResourceLocation texture;
    private boolean isSneak;

    private ModelRenderer skirt1;
    private ModelRenderer skirt2;

    public ModelSanSkirt(float scale) {
        this.scale = scale;

        this.modelJson = ModelJsonLoader.create(this, Resources.SKIRT_MODEL, "skirt1", "skirt2");
    }

    @Override
    public float getBaseScale() {
        return this.scale;
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if( this.modelJson != null && this.modelJson.isLoaded() ) {
            Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
        }
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);

        if (model instanceof ModelBiped )
        {
            ModelBiped modelbiped = (ModelBiped)model;
            this.isSneak = modelbiped.isSneak;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        if( this.isSneak ) {
            this.skirt1.rotationPointZ = 3.0F;
            this.skirt1.rotationPointY = -0.6F;
            this.skirt2.rotationPointZ = 5.0F;
            this.skirt2.rotationPointY = -1.0F;
            this.skirt1.rotateAngleX = -0.29F;
            this.skirt2.rotateAngleX = -0.43F;
        } else {
            this.skirt1.rotationPointZ = 0.0F;
            this.skirt2.rotationPointZ = 0.0F;
            this.skirt1.rotationPointY = 0.0F;
            this.skirt2.rotationPointY = 0.0F;

            this.skirt1.rotateAngleX = 0.0F;
            this.skirt2.rotateAngleX = 0.0F;
        }
    }

    @Override
    public void onReload(IResourceManager resourceManager, ModelJsonLoader<ModelSanSkirt, ModelJsonLoader.ModelJson> loader) {
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
