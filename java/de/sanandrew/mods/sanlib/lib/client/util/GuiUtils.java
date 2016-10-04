/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.util;

import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * An utility class for GUIs and GUI related stuff.
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public final class GuiUtils
{
    /**
     * A wrapper method for {@link GL11#glScissor(int, int, int, int)} that corrects position and respects the GUI scaling of Minecraft.<br>
     * Unlike glScissor, the starting position of the scissor box is the top left corner instead of the bottom left corner.
     * @param x The X coordinate of the starting position for the scissor box.
     * @param y The Y coordinate of the starting position for the scissor box.
     * @param width The width of the scissor box.
     * @param height The height of the scissor box.
     */
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

    /**
     * Returns the tooltip from an ItemStack without the info whilst holding SHIFT (added by some mods).
     * @param stack The ItemStack.
     * @return The tooltip of the ItemStack.
     */
    public static List<?> getTooltipWithoutShift(ItemStack stack) {
        ByteBuffer keyDownBuffer = ReflectionUtils.getCachedFieldValue(Keyboard.class, null, "keyDownBuffer", "keyDownBuffer");
        byte lShift = keyDownBuffer.get(Keyboard.KEY_LSHIFT);
        byte rShift = keyDownBuffer.get(Keyboard.KEY_RSHIFT);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, (byte) 0);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, (byte) 0);
        List<?> tooltip = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, lShift);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, rShift);

        return tooltip;
    }

    /**
     * draws a rectangular texture with the fixed resolution 256x256 or a multiple of it.
     * @param xPos The X coordinate on screen for the texture to appear at.
     * @param yPos The Y coordinate on screen for the texture to appear at.
     * @param z The Z index of the texture
     * @param u The X coordinate on the texture sheet
     * @param v The Y coordinate on the texture sheet
     * @param width The width of the texture
     * @param height The height of the texture
     */
    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height) {
        drawTexturedModalRect(xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
    }

    /**
     * draws a rectangular texture with a custom resolution scale.
     * @param xPos The X coordinate on screen for the texture to appear at.
     * @param yPos The Y coordinate on screen for the texture to appear at.
     * @param z The Z index of the texture
     * @param u The X coordinate on the texture sheet
     * @param v The Y coordinate on the texture sheet
     * @param width The width of the texture
     * @param height The height of the texture
     * @param resScaleX The resolution scale on the X axis. Can be calculated via {@code 1F / texture width in pixel}, e.g. {@code 1F / 256F = 0.00390625F}
     * @param resScaleY The resolution scale on the Y axis. Can be calculated via {@code 1F / texture height in pixel}, e.g. {@code 1F / 256F = 0.00390625F}
     */
    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height, float resScaleX, float resScaleY) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xPos, yPos + height, z).tex(u * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos + height, z).tex((u + width) * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos, z).tex((u + width) * resScaleX, v * resScaleY).endVertex();
        buffer.pos(xPos, yPos, z).tex(u * resScaleX, v * resScaleY).endVertex();
        tessellator.draw();
    }
}
