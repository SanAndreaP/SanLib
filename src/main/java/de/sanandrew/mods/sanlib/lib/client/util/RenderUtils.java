/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * An utility class for rendering related stuff.
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public final class RenderUtils
{
    private static RenderItem renderItem;

    /**
     * Renders an ItemStack onto a GUI purely without any overlay (stack count, durability bar etc.).
     * @param stack The ItemStack to be rendered.
     * @param posX The X coordinate for the position on the GUI.
     * @param posY The Y coordinate for the position on the GUI.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     */
    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale) {
        renderStackInGui(stack, posX, posY, scale, null, null, false);
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
    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, FontRenderer fontRenderer) {
        renderStackInGui(stack, posX, posY, scale, fontRenderer, null, true);
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
     * @param doOverlay A flag to determine wether or not to draw the overlay (stack count/custom text, durability bar, etc.).
     */
    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, FontRenderer fontRenderer, String customTxt, boolean doOverlay) {
        if( renderItem == null ) {
            renderItem = Minecraft.getMinecraft().getRenderItem();
        }

        renderItem.zLevel -= 50.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        if( doOverlay ) {
            if( fontRenderer != null ) {
                renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, customTxt);
                GlStateManager.disableLighting();
            } else {
                renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, 0, 0, "");
                GlStateManager.disableLighting();
            }
        }
        GlStateManager.popMatrix();
        renderItem.zLevel += 50.0F;
    }

    /**
     * Renders an ItemStack into the world.
     * @param stack The ItemStack to be rendered.
     * @param posX The X coordinate in the world.
     * @param posY The Y coordinate in the world.
     * @param posZ The Y coordinate in the world.
     * @param rotateX The rotation (in degrees) along the X axis.
     * @param rotateY The rotation (in degrees) along the Y axis.
     * @param rotateZ The rotation (in degrees) along the Z axis.
     * @param scale The scaling factor for the rendering. 1.0F is normal size.
     */
    public static void renderStackInWorld(ItemStack stack, double posX, double posY, double posZ, float rotateX, float rotateY, float rotateZ, double scale) {
        if( renderItem == null ) {
            renderItem = Minecraft.getMinecraft().getRenderItem();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotateX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotateY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotateZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(scale, scale, scale);

        renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
    }
}
