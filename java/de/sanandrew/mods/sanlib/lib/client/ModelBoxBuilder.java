/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import java.lang.reflect.InvocationTargetException;

/**
 * A builder class to easily instantiate a ModelRenderer box for a Model without having to deal with multi-line method calls and field assignments.
 * Method cascading FTW \o/
 */
@SuppressWarnings("unused")
public final class ModelBoxBuilder<T extends ModelRenderer>
{
    /**
     * Creates a new Model Box Builder.
     *
     * @param model The model instance
     * @return A new instance of the ModelBoxBuilder.
     */
    public static ModelBoxBuilder<ModelRenderer> newBuilder(final ModelBase model) {
        return newBuilder(model, null, ModelRenderer.class);
    }

    /**
     * Creates a new Model Box Builder with a custom box name.
     *
     * @param model The model instance
     * @return A new instance of the ModelBoxBuilder.
     */
    public static ModelBoxBuilder<ModelRenderer> newBuilder(final ModelBase model, String name) {
        return newBuilder(model, name, ModelRenderer.class);
    }

    /**
     * Creates a new Model Box Builder with a custom box class.
     *
     * @param model    The model instance
     * @param boxClass The custom box class. Note: It MUST be a child of the {@link net.minecraft.client.model.ModelRenderer} class and override the
     *                 {@link net.minecraft.client.model.ModelRenderer#ModelRenderer(net.minecraft.client.model.ModelBase)} parent constructor!
     * @return A new instance of the ModelBoxBuilder.
     */
    public static <T extends ModelRenderer> ModelBoxBuilder<T> newBuilder(final ModelBase model, Class<T> boxClass) {
        return newBuilder(model, null, boxClass);
    }

    /**
     * Creates a new Model Box Builder with a custom box class and name.
     *
     * @param model    The model instance
     * @param name     A name for the box
     * @param boxClass The custom box class. Note: It MUST be a child of the {@link net.minecraft.client.model.ModelRenderer} class and override the
     *                 {@link net.minecraft.client.model.ModelRenderer#ModelRenderer(net.minecraft.client.model.ModelBase, java.lang.String)} parent constructor!
     * @return A new instance of the ModelBoxBuilder.
     */
    public static <T extends ModelRenderer> ModelBoxBuilder<T> newBuilder(final ModelBase model, String name, Class<T> boxClass) {
        return new ModelBoxBuilder<>(model, name, boxClass);
    }

    private T box;

    private ModelBoxBuilder(ModelBase model, String name, Class<T> boxClass) {
        try {
            if( name != null ) {
                this.box = boxClass.getConstructor(ModelBase.class, String.class).newInstance(model, name);
            } else {
                this.box = boxClass.getConstructor(ModelBase.class).newInstance(model);
            }
            this.box.textureWidth = model.textureWidth;
            this.box.textureHeight = model.textureHeight;
        } catch( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new RuntimeException("Unable to build Model box! Check your inheritance or constructors of your ModelRenderer subclass if you've provided one!", e);
        }
    }

    /**
     * Sets the texture variables of the box.
     *
     * @param x      The X coordinate of the texture's upper-left corner
     * @param y      The Y coordinate of the texture's upper-left corner
     * @param mirror If it should mirror the texture on the box. Does not affect the coordinates!
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror) {
        this.box.setTextureOffset(x, y);
        this.box.mirror = mirror;

        return this;
    }

    /**
     * Sets the texture variables of the box.
     *
     * @param x      The X coordinate of the texture's upper-left corner
     * @param y      The Y coordinate of the texture's upper-left corner
     * @param mirror If it should mirror the texture on the box. Does not affect the coordinates!
     * @param width  The width of the texture
     * @param height The height of the texture
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setTexture(int x, int y, boolean mirror, float width, float height) {
        this.box.textureWidth = width;
        this.box.textureHeight = height;
        this.box.setTextureOffset(x, y);
        this.box.mirror = mirror;

        return this;
    }

    /**
     * Sets the location (rotation point) of the box.
     *
     * @param pointX X coordinate of the box
     * @param pointY Y coordinate of the box
     * @param pointZ Z coordinate of the box
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setLocation(float pointX, float pointY, float pointZ) {
        this.box.rotationPointX = pointX;
        this.box.rotationPointY = pointY;
        this.box.rotationPointZ = pointZ;

        return this;
    }

    /**
     * Sets the rotation angles of the box.
     *
     * @param angleX X axis rotation of the box
     * @param angleY Y axis rotation of the box
     * @param angleZ Z axis rotation of the box
     * @return The ModelBoxBuilder instance calling this method.
     */
    public ModelBoxBuilder<T> setRotation(float angleX, float angleY, float angleZ) {
        this.box.rotateAngleX = angleX;
        this.box.rotateAngleY = angleY;
        this.box.rotateAngleZ = angleZ;

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
}