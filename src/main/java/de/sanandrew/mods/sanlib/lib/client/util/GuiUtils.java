////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * An utility class for GUIs and GUI related stuff.
 */
@SuppressWarnings({ "unused", "deprecation" })
@OnlyIn(Dist.CLIENT)
public final class GuiUtils
{
    /**
     * A wrapper method for {@link RenderSystem#enableScissor(int, int, int, int)} that corrects position and respects the GUI scaling of Minecraft.<br>
     * Unlike glScissor, the starting position of the scissor box is the top left corner instead of the bottom left corner.
     * @param x The X coordinate of the starting position for the scissor box.
     * @param y The Y coordinate of the starting position for the scissor box.
     * @param width The width of the scissor box.
     * @param height The height of the scissor box.
     */
    public static void enableScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();

        MainWindow window = mc.getMainWindow();
        double scaleFactor = window.getGuiScaleFactor();

        RenderSystem.enableScissor((int) (x * scaleFactor), (int) (window.getHeight() - (y + height) * scaleFactor),
                                   (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

//    /**
//     * Returns the tooltip from an ItemStack without the info whilst holding SHIFT (added by some mods).
//     * @param stack The ItemStack.
//     * @return The tooltip of the ItemStack.
//     */
//    public static List<?> getTooltipWithoutShift(@Nonnull ItemStack stack) {
//        ByteBuffer keyDownBuffer = ReflectionUtils.getCachedFieldValue(Keyboard.class, null, "keyDownBuffer", "keyDownBuffer");
//        byte lShift = keyDownBuffer.get(Keyboard.KEY_LSHIFT);
//        byte rShift = keyDownBuffer.get(Keyboard.KEY_RSHIFT);
//        keyDownBuffer.put(Keyboard.KEY_LSHIFT, (byte) 0);
//        keyDownBuffer.put(Keyboard.KEY_RSHIFT, (byte) 0);
//        List<?> tooltip = stack.getTooltip(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);
//        keyDownBuffer.put(Keyboard.KEY_LSHIFT, lShift);
//        keyDownBuffer.put(Keyboard.KEY_RSHIFT, rShift);
//
//        return tooltip;
//    }

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
    public static void drawTexture(MatrixStack stack, int xPos, int yPos, float z, int u, int v, int width, int height) {
        drawTexture(stack, xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
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
    public static void drawTexture(MatrixStack stack, int xPos, int yPos, float z, int u, int v, int width, int height, float resScaleX, float resScaleY) {
        Matrix4f      matrix = stack.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix, xPos, yPos + height, z).tex(u * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(matrix, xPos + width, yPos + height, z).tex((u + width) * resScaleX, (v + height) * resScaleY).endVertex();
        buffer.pos(matrix, xPos + width, yPos, z).tex((u + width) * resScaleX, v * resScaleY).endVertex();
        buffer.pos(matrix, xPos, yPos, z).tex(u * resScaleX, v * resScaleY).endVertex();
        buffer.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);
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
    public static void drawGradient(MatrixStack stack, int x, int y, int width, int height, int color1, int color2, boolean isHorizontal) {
        ColorObj startColor = new ColorObj(color1);
        ColorObj endColor = new ColorObj(color2);
        Matrix4f      matrix = stack.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if( isHorizontal ) {
            buildColoredQuad(matrix, buffer, x, y, width, height, startColor, endColor);
        } else {
            buildColoredQuad(matrix, buffer, x, y, width, height, endColor, startColor, startColor, endColor);
        }
        buffer.finishDrawing();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        WorldVertexBufferUploader.draw(buffer);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public static void buildColoredQuad(Matrix4f matrix, BufferBuilder bb, int x, int y, int width, int height, ColorObj... colors) {
        if( colors == null || colors.length < 1 ) {
            return;
        }

        switch( colors.length ) {
            case 1: colors = new ColorObj[] {colors[0], colors[0], colors[0], colors[0]}; break;
            case 2: colors = new ColorObj[] {colors[0], colors[0], colors[1], colors[1]}; break;
            case 3: colors = new ColorObj[] {colors[0], colors[1], colors[2], colors[2]}; break;
        }

        bb.pos(matrix, x + width, y, 0).color(colors[0].fRed(), colors[0].fGreen(), colors[0].fBlue(), colors[0].fAlpha()).endVertex();
        bb.pos(matrix, x, y, 0).color(colors[1].fRed(), colors[1].fGreen(), colors[1].fBlue(), colors[1].fAlpha()).endVertex();
        bb.pos(matrix, x, y + height, 0).color(colors[2].fRed(), colors[2].fGreen(), colors[2].fBlue(), colors[2].fAlpha()).endVertex();
        bb.pos(matrix, x + width, y + height, 0).color(colors[3].fRed(), colors[3].fGreen(), colors[3].fBlue(), colors[3].fAlpha()).endVertex();
    }
}
