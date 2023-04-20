////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A utility class for rendering related stuff.
 * - render items in GUIs
 * - render items statically, usually used for in-world rendering
 */
@SuppressWarnings({"unused", "deprecation"})
@OnlyIn(Dist.CLIENT)
public final class RenderUtils
{
    private static ItemRenderer itemRenderer;

    private RenderUtils() { }

    /**
     * Renders an {@link ItemStack} within a GUI without any decorations (stack count, durability bar etc.).
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param posX The X (left) coordinate for the position within the GUI.
     * @param posY The Y (top) coordinate for the position within the GUI.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     */
    public static void renderGuiItem(@Nonnull ItemStack item, MatrixStack matrixStack, int posX, int posY, float scale) {
        renderGuiItem(item, matrixStack, posX, posY, scale, null, null, false);
    }

    /**
     * Renders an {@link ItemStack} within a GUI with decorations (stack count, durability bar etc.).
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param posX The X (left) coordinate for the position within the GUI.
     * @param posY The Y (top) coordinate for the position within the GUI.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     * @param fontRenderer The <tt>FontRenderer</tt> to be used for the stack count.
     *                     If <tt>NULL</tt>, no stack count is rendered.
     */
    public static void renderGuiItem(@Nonnull ItemStack item, MatrixStack matrixStack, int posX, int posY, float scale,
                                     FontRenderer fontRenderer)
    {
        renderGuiItem(item, matrixStack, posX, posY, scale, fontRenderer, null, true);
    }

    /**
     * Renders an {@link ItemStack} within a GUI with or without decorations (stack count, durability bar etc.).
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param posX The X (left) coordinate for the position within the GUI.
     * @param posY The Y (top) coordinate for the position within the GUI.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     * @param fontRenderer The <tt>FontRenderer</tt> to be used for the stack count or custom text.
     *                     If <tt>NULL</tt>, no stack count/custom text is rendered.
     * @param customText The custom text to be used instead of the stack count.
     *                   If <tt>NULL</tt>, the stack count may render instead.
     * @param renderDecorations A flag to determine whether to draw decorations (stack count/custom text, durability bar, etc.).
     */
    public static void renderGuiItem(@Nonnull ItemStack item, MatrixStack matrixStack, int posX, int posY, float scale,
                                     FontRenderer fontRenderer, String customText, boolean renderDecorations)
    {
        if( itemRenderer == null ) {
            itemRenderer = Minecraft.getInstance().getItemRenderer();
        }

        itemRenderer.blitOffset -= 50.0F;
        matrixStack.pushPose();
        matrixStack.translate(posX, posY, 0.0F);
        matrixStack.scale(scale, scale, scale);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.last().pose());

        itemRenderer.renderGuiItem(item, 0, 0);

        if( renderDecorations ) {
            if( fontRenderer != null ) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, item, 0, 0, customText);
            } else {
                itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, item, 0, 0, "");
            }
        }

        RenderSystem.popMatrix();
        matrixStack.popPose();
        itemRenderer.blitOffset += 50.0F;
    }


    /**
     * Statically renders an {@link ItemStack} with {@link ItemCameraTransforms.TransformType#FIXED} as transform type.
     * Usually used for in-world rendering.
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param matrixStack The <tt>MatrixStack</tt> of the current render.
     * @param pos The relative positioning of the item within the current render.
     * @param rotation The rotation for the item.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     * @param buffer The render buffer of the current render.
     * @param light An integer representing the combined light value.
     *              Usually provided as parameter by the render method,
     *              but <tt>0xF000F0</tt> (full-bright sky- and blocklight) can also be used instead.
     * @param overlay An integer representing the combined overlay value.
     *                Usually provided as parameter by the render method,
     *                but {@link net.minecraft.client.renderer.texture.OverlayTexture#NO_OVERLAY OverlayTexture.NO_OVERLAY}
     *                can also be used instead.
     */
    public static void renderStaticItem(@Nonnull ItemStack item, MatrixStack matrixStack, Vector3f pos, Quaternion rotation, float scale,
                                        IRenderTypeBuffer buffer, int light, int overlay)
    {
        renderStaticItem(item, matrixStack, pos, rotation, scale, ItemCameraTransforms.TransformType.FIXED, buffer, null, light, overlay);
    }

    /**
     * Statically renders an {@link ItemStack} with a custom transform type.
     * Usually used for in-world rendering.
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param matrixStack The <tt>MatrixStack</tt> of the current render.
     * @param pos The relative positioning of the item within the current render.
     * @param rotation The rotation for the item.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     * @param transformType The type of camera transformations to be used.
     * @param buffer The render buffer of the current render.
     * @param light An integer representing the combined light value.
     *              Usually provided as parameter by the render method,
     *              but <tt>0xF000F0</tt> (full-bright sky- and blocklight) can also be used instead.
     * @param overlay An integer representing the combined overlay value.
     *                Usually provided as parameter by the render method,
     *                but {@link net.minecraft.client.renderer.texture.OverlayTexture#NO_OVERLAY OverlayTexture.NO_OVERLAY}
     *                can also be used instead.
     */
    public static void renderStaticItem(@Nonnull ItemStack item, MatrixStack matrixStack, Vector3f pos, Quaternion rotation, float scale,
                                        ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, int light, int overlay)
    {
        renderStaticItem(item, matrixStack, pos, rotation, scale, transformType, buffer, null, light, overlay);
    }

    /**
     * Statically renders an {@link ItemStack} with a custom transform type.
     * This can be bound to an entity, which determines possible alternative item models and additional transformations if the transform type
     * is {@link net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType#FIRST_PERSON_LEFT_HAND ItemCameraTransforms.TransformType#FIRST_PERSON_LEFT_HAND}.
     * Usually used for in-world rendering.
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param matrixStack The <tt>MatrixStack</tt> of the current render.
     * @param pos The relative positioning of the item within the current render.
     * @param rotation The rotation for the item.
     * @param scale The scaling factor for the rendering. <tt>1.0F</tt> is normal size.
     * @param transformType The type of camera transformations to be used.
     * @param buffer The render buffer of the current render.
     * @param entity The entity this item should be bound to.
     *               If <tt>NULL</tt>, the item is not bound to an entity.
     * @param light An integer representing the combined light value.
     *              Usually provided as parameter by the render method,
     *              but <tt>0xF000F0</tt> (full-bright sky- and blocklight) can also be used instead.
     * @param overlay An integer representing the combined overlay value.
     *                Usually provided as parameter by the render method,
     *                but {@link net.minecraft.client.renderer.texture.OverlayTexture#NO_OVERLAY OverlayTexture.NO_OVERLAY}
     *                can also be used instead.
     */
    public static void renderStaticItem(@Nonnull ItemStack item, MatrixStack matrixStack, Vector3f pos, Quaternion rotation, float scale,
                                        ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity,
                                        int light, int overlay)
    {
        matrixStack.pushPose();
        matrixStack.translate(pos.x(), pos.y(), pos.z());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStack.mulPose(rotation);
        matrixStack.scale(scale, scale, scale);

        renderStaticItemNoPosing(item, matrixStack, transformType, buffer, entity, light, overlay);

        matrixStack.popPose();
    }

    /**
     * Statically renders an {@link ItemStack} with a custom transform type, without posing the renderer.
     * This can be bound to an entity, which determines possible alternative item models and additional transformations if the transform type
     * is {@link net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType#FIRST_PERSON_LEFT_HAND ItemCameraTransforms.TransformType#FIRST_PERSON_LEFT_HAND}.
     * Usually used for in-world rendering and internally for all other <tt>renderStaticItem*</tt> methods.
     * @param item The <tt>ItemStack</tt> to be rendered.
     * @param matrixStack The <tt>MatrixStack</tt> of the current render.
     * @param transformType The type of camera transformations to be used.
     * @param buffer The render buffer of the current render.
     * @param entity The entity this item should be bound to.
     *               If <tt>NULL</tt>, the item is not bound to an entity.
     * @param light An integer representing the combined light value.
     *              Usually provided as parameter by the render method,
     *              but <tt>0xF000F0</tt> (full-bright sky- and blocklight) can also be used instead.
     * @param overlay An integer representing the combined overlay value.
     *                Usually provided as parameter by the render method,
     *                but {@link net.minecraft.client.renderer.texture.OverlayTexture#NO_OVERLAY OverlayTexture.NO_OVERLAY}
     *                can also be used instead.
     */
    public static void renderStaticItemNoPosing(@Nonnull ItemStack item, MatrixStack matrixStack, ItemCameraTransforms.TransformType transformType,
                                                IRenderTypeBuffer buffer, LivingEntity entity, int light, int overlay)
    {
        if( itemRenderer == null ) {
            itemRenderer = Minecraft.getInstance().getItemRenderer();
        }

        if( entity != null ) {
            itemRenderer.renderStatic(entity, item, transformType, transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, matrixStack, buffer, entity.level, light, overlay);
        } else {
            itemRenderer.renderStatic(item, transformType, light, overlay, matrixStack, buffer);
        }
    }

//region Deprecation - TODO: REMOVE IN NEXT MC VERSION (past 1.16)!
    /** @deprecated Use {@link #renderGuiItem(ItemStack, MatrixStack, int, int, float)} */
    @Deprecated
    public static void renderStackInGui(@Nonnull ItemStack stack, MatrixStack matrixStack, int posX, int posY, float scale) {
        renderGuiItem(stack, matrixStack, posX, posY, scale, null, null, false);
    }

    /** @deprecated Use {@link #renderGuiItem(ItemStack, MatrixStack, int, int, float, FontRenderer)} */
    @Deprecated
    public static void renderStackInGui(@Nonnull ItemStack stack, MatrixStack matrixStack, int posX, int posY, float scale,
                                        FontRenderer fontRenderer)
    {
        renderGuiItem(stack, matrixStack, posX, posY, scale, fontRenderer, null, true);
    }

    /** @deprecated Use {@link #renderGuiItem(ItemStack, MatrixStack, int, int, float, FontRenderer, String, boolean)} */
    @Deprecated
    public static void renderStackInGui(@Nonnull ItemStack item, MatrixStack matrixStack, int posX, int posY, float scale,
                                        FontRenderer fontRenderer, String customTxt, boolean doOverlay)
    {
        renderGuiItem(item, matrixStack, posX, posY, scale, fontRenderer, customTxt, doOverlay);
    }

    /** @deprecated Use {@link #renderStaticItem(ItemStack, MatrixStack, Vector3f, Quaternion, float, IRenderTypeBuffer, int, int)} */
    @Deprecated
    public static void renderStackInWorld(@Nonnull ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale,
                                          IRenderTypeBuffer buffer, int light, int overlay)
    {
        renderStackInWorld(stack, matrixStack, pos, rotation, scale, ItemCameraTransforms.TransformType.FIXED, buffer, null, light, overlay);
    }

    /** @deprecated Use {@link #renderStaticItem(ItemStack, MatrixStack, Vector3f, Quaternion, float, ItemCameraTransforms.TransformType,IRenderTypeBuffer, int, int)} */
    @Deprecated
    public static void renderStackInWorld(@Nonnull ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, int light, int overlay)
    {
        renderStackInWorld(stack, matrixStack, pos, rotation, scale, transformType, buffer, null, light, overlay);
    }

    /**@deprecated Use {@link #renderStaticItem(ItemStack, MatrixStack, Vector3f, Quaternion, float, ItemCameraTransforms.TransformType, IRenderTypeBuffer, LivingEntity, int, int)} */
    @Deprecated
    public static void renderStackInWorld(@Nonnull ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity,
                                          int light, int overlay)
    {
        Quaternion q = Vector3f.XP.rotationDegrees(rotation.x());
        q.mul(Vector3f.YP.rotationDegrees(rotation.y()));
        q.mul(Vector3f.ZP.rotationDegrees(rotation.z()));

        renderStaticItem(stack, matrixStack, pos, q, scale, transformType, buffer, entity, light, overlay);

        matrixStack.popPose();
    }

    /** @deprecated Use {@link #renderStaticItem(ItemStack, MatrixStack, Vector3f, Quaternion, float, ItemCameraTransforms.TransformType, IRenderTypeBuffer, LivingEntity, int, int) */
    @Deprecated
    public static void renderStackInWorld(@Nonnull ItemStack stack, MatrixStack matrixStack, Vector3f pos, Quaternion rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity,
                                          int light, int overlay)
    {
        renderStaticItem(stack, matrixStack, pos, rotation, scale, transformType, buffer, entity, light, overlay);
    }

    /** @deprecated Use {@link #renderStaticItemNoPosing(ItemStack, MatrixStack, ItemCameraTransforms.TransformType, IRenderTypeBuffer, LivingEntity, int, int)} */
    @Deprecated
    public static void renderStackInWorldPure(@Nonnull ItemStack stack, MatrixStack matrixStack, ItemCameraTransforms.TransformType transformType,
                                              IRenderTypeBuffer buffer, LivingEntity entity, int light, int overlay)
    {
        renderStaticItemNoPosing(stack, matrixStack, transformType, buffer, entity, light, overlay);
    }
//endregion
}
