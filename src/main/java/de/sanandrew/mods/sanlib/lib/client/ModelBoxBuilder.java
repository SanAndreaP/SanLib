////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A builder class to easily instantiate a ModelRenderer box for a Model without having to deal with multi-line method calls and field assignments.
 * Method cascading FTW \o/
 */
@SuppressWarnings("unused")
public final class ModelBoxBuilder<T extends ModelRenderer>
{
    /**
     * Creates a new Model Box Builder.
     * @param model The model instance
     * @return A new instance of the ModelBoxBuilder.
     */
    public static ModelBoxBuilder<ModelRenderer> newBuilder(final Model model) {
        return newBuilder(model, ModelRenderer::new);
    }

    /**
     * Creates a new Model Box Builder with a custom box class.
     * @param model    The model instance
     * @param mrCtor The custom box constructor reference. Note: It MUST be a child of {@link ModelRenderer}!
     * @return A new instance of the ModelBoxBuilder.
     */
    public static <T extends ModelRenderer> ModelBoxBuilder<T> newBuilder(final Model model, Function<Model, T> mrCtor) {
        return new ModelBoxBuilder<>(model, mrCtor);
    }

    private final T      box;

    private ModelBoxBuilder(Model model, Function<Model, T> mrCtor) {
        this.box = mrCtor.apply(model);
    }

    public ModelBoxBuilder<T> setName(String name) {
        if( this.box instanceof INamedModelRenderer ) {
            ((INamedModelRenderer) this.box).setName(name);
        }

        return this;
    }

    /**
     * Sets the texture variables of the box.
     * @param x      The X coordinate of the texture's upper-left corner
     * @param y      The Y coordinate of the texture's upper-left corner
     * @param mirror If it should mirror the texture on the box. Does not affect the coordinates!
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror) {
        this.box.texOffs(x, y);
        this.box.mirror = mirror;

        return this;
    }

    /**
     * Sets the texture variables of the box.
     * @param x      The X coordinate of the texture's upper-left corner
     * @param y      The Y coordinate of the texture's upper-left corner
     * @param mirror If it should mirror the texture on the box. Does not affect the coordinates!
     * @param width  The width of the texture
     * @param height The height of the texture
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror, int width, int height) {
        this.box.setTexSize(width, height);
        this.box.texOffs(x, y);
        this.box.mirror = mirror;

        return this;
    }

    /**
     * Sets the location (rotation point) of the box.
     * @param pointX X coordinate of the box
     * @param pointY Y coordinate of the box
     * @param pointZ Z coordinate of the box
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setLocation(float pointX, float pointY, float pointZ) {
        this.box.x = pointX;
        this.box.y = pointY;
        this.box.z = pointZ;

        return this;
    }

    /**
     * Sets the rotation angles of the box.
     * @param angleX X axis rotation of the box
     * @param angleY Y axis rotation of the box
     * @param angleZ Z axis rotation of the box
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setRotation(float angleX, float angleY, float angleZ) {
        this.box.xRot = angleX;
        this.box.yRot = angleY;
        this.box.zRot = angleZ;

        return this;
    }

    /**
     * Get the final result box from the builder whilst adding the cube to it.
     * @param xOffset X offset of the cube relative to the box's coordinates
     * @param yOffset Y offset of the cube relative to the box's coordinates
     * @param zOffset Z offset of the cube relative to the box's coordinates
     * @param xSize X size of the cube
     * @param ySize Y size of the cube
     * @param zSize Z size of the cube
     * @param scale scale of the cube
     * @return The box of type {@link T}
     */
    public T getBox(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, float scale) {
        this.box.addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize, scale);

        return this.box;
    }

    public static class NamedModelRenderer
            extends ModelRenderer
            implements INamedModelRenderer
    {
        private String name;

        public NamedModelRenderer(Model p_i1173_1_) {
            super(p_i1173_1_);
        }

        public NamedModelRenderer(Model p_i46358_1_, int p_i46358_2_, int p_i46358_3_) {
            super(p_i46358_1_, p_i46358_2_, p_i46358_3_);
        }

        public NamedModelRenderer(int p_i225949_1_, int p_i225949_2_, int p_i225949_3_, int p_i225949_4_) {
            super(p_i225949_1_, p_i225949_2_, p_i225949_3_, p_i225949_4_);
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public interface INamedModelRenderer
    {
        void setName(String name);
        String getName();
    }
}