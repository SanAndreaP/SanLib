////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.util;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
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
    public static List<?> getTooltipWithoutShift(@Nonnull ItemStack stack) {
        ByteBuffer keyDownBuffer = ReflectionUtils.getCachedFieldValue(Keyboard.class, null, "keyDownBuffer", "keyDownBuffer");
        byte lShift = keyDownBuffer.get(Keyboard.KEY_LSHIFT);
        byte rShift = keyDownBuffer.get(Keyboard.KEY_RSHIFT);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, (byte) 0);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, (byte) 0);
        List<?> tooltip = stack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
        keyDownBuffer.put(Keyboard.KEY_LSHIFT, lShift);
        keyDownBuffer.put(Keyboard.KEY_RSHIFT, rShift);

        return tooltip;
    }

    /**
     * draws a rectangular texture with the fixed resolution 256x256 or a multiple of it.
     *
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
     *
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
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xPos, yPos + height, z).tex(u * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos + height, z).tex((u + width) * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(xPos + width, yPos, z).tex((u + width) * resScaleX, v * resScaleY).endVertex();
        buffer.pos(xPos, yPos, z).tex(u * resScaleX, v * resScaleY).endVertex();
        tessellator.draw();
    }

    /**
     * draws a rectangle with a gradient color
     *
     * @param x the X coordinate on screen for the rectangle to appear at
     * @param y the Y coordinate on screen for the rectangle to appear at
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color1 the starting color; vertically this is the left, horizontally the top color
     * @param color2 the ending color; vertically this is the right, horizontally the bottom color
     * @param isHorizontal whether this gradient will be horizontal (<tt>true</tt>) or vertical (<tt>false</tt>)
     */
    @SuppressWarnings("Duplicates")
    public static void drawGradientRect(int x, int y, int width, int height, int color1, int color2, boolean isHorizontal) {
        ColorObj startColor = new ColorObj(color1);
        ColorObj endColor = new ColorObj(color2);

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if( isHorizontal ) {
            bufferbuilder.pos(x + width, y, 0).color(startColor.fRed(), startColor.fGreen(), startColor.fBlue(), startColor.fAlpha()).endVertex();
            bufferbuilder.pos(x, y, 0).color(startColor.fRed(), startColor.fGreen(), startColor.fBlue(), startColor.fAlpha()).endVertex();
            bufferbuilder.pos(x, y + height, 0).color(endColor.fRed(), endColor.fGreen(), endColor.fBlue(), endColor.fAlpha()).endVertex();
            bufferbuilder.pos(x + width, y + height, 0).color(endColor.fRed(), endColor.fGreen(), endColor.fBlue(), endColor.fAlpha()).endVertex();
        } else {
            bufferbuilder.pos(x + width, y, 0).color(endColor.fRed(), endColor.fGreen(), endColor.fBlue(), endColor.fAlpha()).endVertex();
            bufferbuilder.pos(x, y, 0).color(startColor.fRed(), startColor.fGreen(), startColor.fBlue(), startColor.fAlpha()).endVertex();
            bufferbuilder.pos(x, y + height, 0).color(startColor.fRed(), startColor.fGreen(), startColor.fBlue(), startColor.fAlpha()).endVertex();
            bufferbuilder.pos(x + width, y + height, 0).color(endColor.fRed(), endColor.fGreen(), endColor.fBlue(), endColor.fAlpha()).endVertex();
        }
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
