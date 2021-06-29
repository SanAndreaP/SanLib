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
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * An utility class for rendering related stuff.
 */
@SuppressWarnings({"unused", "deprecation"})
@OnlyIn(Dist.CLIENT)
public final class RenderUtils
{
    private static ItemRenderer itemRenderer;

    /**
     * Renders an ItemStack onto a GUI purely without any overlay (stack count, durability bar etc.).
     * @param stack The ItemStack to be rendered.
     * @param posX The X coordinate for the position on the GUI.
     * @param posY The Y coordinate for the position on the GUI.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     */
    public static void renderStackInGui(@Nonnull ItemStack stack, MatrixStack matrixStack, int posX, int posY, float scale) {
        renderStackInGui(stack, matrixStack, posX, posY, scale, null, null, false);
    }

    /**
     * Renders an ItemStack onto a GUI with overlay.<br>
     * When a FontRenderer is given, the item stack count is drawn as well.
     * @param stack The ItemStack to be rendered.
     * @param posX The X coordinate for the position on the GUI.
     * @param posY The Y coordinate for the position on the GUI.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     * @param fontRenderer The FontRenderer used to render the stack count.
     */
    public static void renderStackInGui(@Nonnull ItemStack stack, MatrixStack matrixStack, int posX, int posY, float scale, FontRenderer fontRenderer) {
        renderStackInGui(stack, matrixStack, posX, posY, scale, fontRenderer, null, true);
    }

    /**
     * Renders an ItemStack onto a GUI with overlay.<br>
     * When a FontRenderer is given, the item stack count is drawn as well.<br>
     * When a custom text (and a FontRenderer) is given, it is drawn instead of the stack count in its place.
     * @param stack The ItemStack to be rendered.
     * @param posX The X coordinate for the position on the GUI.
     * @param posY The Y coordinate for the position on the GUI.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     * @param fontRenderer The FontRenderer used to render the stack count or custom text. NULL if you don't want to render either.
     * @param customTxt The custom text to be used instead of the stack count. NULL if you want to use the stack count.
     * @param doOverlay A flag to determine whether or not to draw the overlay (stack count/custom text, durability bar, etc.).
     */
    public static void renderStackInGui(@Nonnull ItemStack stack, MatrixStack matrixStack, int posX, int posY, float scale, FontRenderer fontRenderer, String customTxt, boolean doOverlay) {
        if( itemRenderer == null ) {
            itemRenderer = Minecraft.getInstance().getItemRenderer();
        }

        itemRenderer.blitOffset -= 50.0F;
        matrixStack.pushPose();
        matrixStack.translate(posX, posY, 0.0F);
        matrixStack.scale(scale, scale, scale);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.last().pose());

        itemRenderer.renderGuiItem(stack, 0, 0);

        if( doOverlay ) {
            if( fontRenderer != null ) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, stack, 0, 0, customTxt);
            } else {
                itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack, 0, 0, "");
            }
        }

        RenderSystem.popMatrix();
        matrixStack.popPose();
        itemRenderer.blitOffset += 50.0F;
    }

    /**
     * Renders an ItemStack into the world with a <tt>FIXED</tt> transform type.
     * @param stack The ItemStack to be rendered.
     * @param pos The position in the world.
     * @param rotation The rotation (in degrees) along the X, Y and Z axes.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     */
    public static void renderStackInWorld(ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale, IRenderTypeBuffer buffer, int light, int overlay) {
        renderStackInWorld(stack, matrixStack, pos, rotation, scale, ItemCameraTransforms.TransformType.FIXED, buffer, null, light, overlay);
    }

        /**
         * Renders an ItemStack into the world.
         * @param stack The ItemStack to be rendered.
         * @param pos The position in the world.
         * @param rotation The rotation (in degrees) along the X, Y and Z axes.
         * @param scale The scaling factor for the rendering. 1.0F is normal size.
         * @param transformType The transform type of the render.
         */
    public static void renderStackInWorld(ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, int light, int overlay)
    {
        renderStackInWorld(stack, matrixStack, pos, rotation, scale, transformType, buffer, null, light, overlay);
    }


    public static void renderStackInWorld(ItemStack stack, MatrixStack matrixStack, Vector3f pos, Vector3f rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity, int light, int overlay)
    {
        matrixStack.pushPose();
        matrixStack.translate(pos.x(), pos.y(), pos.z());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(rotation.x()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation.y()));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        matrixStack.scale(scale, scale, scale);

        renderStackInWorldPure(stack, matrixStack, transformType, buffer, entity, light, overlay);

        matrixStack.popPose();
    }


    public static void renderStackInWorld(ItemStack stack, MatrixStack matrixStack, Vector3f pos, Quaternion rotation, float scale,
                                          ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity, int light, int overlay)
    {
        matrixStack.pushPose();
        matrixStack.translate(pos.x(), pos.y(), pos.z());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStack.mulPose(rotation);
        matrixStack.scale(scale, scale, scale);

        renderStackInWorldPure(stack, matrixStack, transformType, buffer, entity, light, overlay);

        matrixStack.popPose();
    }


    public static void renderStackInWorldPure(ItemStack stack, MatrixStack matrixStack,
                                              ItemCameraTransforms.TransformType transformType, IRenderTypeBuffer buffer, LivingEntity entity, int light, int overlay)
    {
        if( itemRenderer == null ) {
            itemRenderer = Minecraft.getInstance().getItemRenderer();
        }

        if( entity != null ) {
            itemRenderer.renderStatic(entity, stack, transformType, entity.getMainArm() == HandSide.LEFT, matrixStack, buffer, entity.level, light, overlay);
        } else {
            itemRenderer.renderStatic(stack, transformType, light, overlay, matrixStack, buffer);
        }
    }
}
