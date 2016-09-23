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
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class RenderUtils
{
    private static RenderItem renderItem;

    public static void glScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int guiScale = mc.gameSettings.guiScale;

        if( guiScale == 0 ) {
            guiScale = 1000;
        }

        while( scaleFactor < guiScale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240 ) {
            ++scaleFactor;
        }

        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale) {
        renderStackInGui(stack, posX, posY, scale, null, null);
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, @Nullable FontRenderer fontRenderer) {
        renderStackInGui(stack, posX, posY, scale, fontRenderer, null);
    }

    public static void renderStackInGui(ItemStack stack, int posX, int posY, double scale, @Nullable FontRenderer fontRenderer, @Nullable String customTxt) {
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
        if( fontRenderer != null ) {
            renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, 0, 0, customTxt);
            GlStateManager.disableLighting();
        }
        GlStateManager.popMatrix();
        renderItem.zLevel += 50.0F;
    }

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
