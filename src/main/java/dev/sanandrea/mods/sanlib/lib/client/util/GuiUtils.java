/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
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
    public static void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.enableScissor(x, y, x + width, y + height);
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
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
//        List<?> tooltip = stack.getTooltipLines(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);
//        keyDownBuffer.put(Keyboard.KEY_LSHIFT, lShift);
//        keyDownBuffer.put(Keyboard.KEY_RSHIFT, rShift);
//
//        return tooltip;
//    }

//    /**
//     * draws a rectangular location with the fixed resolution 256x256 or a multiple of it.
//     *
//     * @param xPos The X coordinate on screen for the location to appear at.
//     * @param yPos The Y coordinate on screen for the location to appear at.
//     * @param z The Z index of the location
//     * @param u The X coordinate on the location sheet
//     * @param v The Y coordinate on the location sheet
//     * @param width The width of the location
//     * @param height The height of the location
//     */
//    @Deprecated
//    public static void drawTexture(GuiGraphics graphics, int xPos, int yPos, float z, int u, int v, int width, int height) {
//        drawTexture(graphics, xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
//    }
//
//    /**
//     * draws a rectangular location with a custom resolution scale.
//     *
//     * @param xPos The X coordinate on screen for the location to appear at.
//     * @param yPos The Y coordinate on screen for the location to appear at.
//     * @param z The Z index of the location
//     * @param u The X coordinate on the location sheet
//     * @param v The Y coordinate on the location sheet
//     * @param width The width of the location
//     * @param height The height of the location
//     * @param resScaleX The resolution scale on the X axis. Can be calculated via {@code 1F / location width in pixel}, e.g. {@code 1F / 256F = 0.00390625F}
//     * @param resScaleY The resolution scale on the Y axis. Can be calculated via {@code 1F / location height in pixel}, e.g. {@code 1F / 256F = 0.00390625F}
//     */
//    @Deprecated
//    public static void drawTexture(GuiGraphics stack, int xPos, int yPos, float z, int u, int v, int width, int height, float resScaleX, float resScaleY) {
//        stack.blit(xPos, yPos, z, u, v, width, height, (int)(resScaleX*256), (int)(resScaleY*256));
//        //        Matrix4f      matrix = stack.last().pose();
////        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
////
////        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
////        buffer.vertex(matrix, xPos, yPos + height, z)        .uv(u * resScaleX, (v + height) * resScaleY)          .endVertex();
////        buffer.vertex(matrix, xPos + width, yPos + height, z).uv((u + width) * resScaleX, (v + height) * resScaleY).endVertex();
////        buffer.vertex(matrix, xPos + width, yPos, z)         .uv((u + width) * resScaleX, v * resScaleY)           .endVertex();
////        buffer.vertex(matrix, xPos, yPos, z)                 .uv(u * resScaleX, v * resScaleY)                     .endVertex();
////        buffer.end();
////        RenderSystem.enableAlphaTest();
////        WorldVertexBufferUploader.end(buffer);
//    }

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
    public static void drawGradient(GuiGraphics graphics, float x, float y, float width, float height, int color1, int color2, boolean isHorizontal) {
        ColorObj startColor = ColorObj.fromARGB(color1);
        ColorObj endColor = ColorObj.fromARGB(color2);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        Matrix4f      matrix = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        if( isHorizontal ) {
            buildColoredQuad(matrix, buffer, x, y, width, height, startColor, endColor);
        } else {
            buildColoredQuad(matrix, buffer, x, y, width, height, endColor, startColor, startColor, endColor);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
//        buffer.end();

//        RenderSystem.disableTexture();
//        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
//        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
//        WorldVertexBufferUploader.end(buffer);
//        RenderSystem.shadeModel(GL11.GL_FLAT);
//        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.enableTexture();
    }

    public static void buildColoredQuad(Matrix4f matrix, BufferBuilder bb, float x, float y, float width, float height, ColorObj... colors) {
        if( colors == null || colors.length < 1 ) {
            return;
        }

        switch( colors.length ) {
            case 1: colors = new ColorObj[] {colors[0], colors[0], colors[0], colors[0]}; break;
            case 2: colors = new ColorObj[] {colors[0], colors[0], colors[1], colors[1]}; break;
            case 3: colors = new ColorObj[] {colors[0], colors[1], colors[2], colors[2]}; break;
        }

        bb.addVertex(matrix, x + width, y, 0)         .setColor(colors[0].getColorInt());
        bb.addVertex(matrix, x, y, 0)                 .setColor(colors[1].getColorInt());
        bb.addVertex(matrix, x, y + height, 0)        .setColor(colors[2].getColorInt());
        bb.addVertex(matrix, x + width, y + height, 0).setColor(colors[3].getColorInt());
    }
}
