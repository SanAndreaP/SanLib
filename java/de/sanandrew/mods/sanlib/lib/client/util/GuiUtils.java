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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;

@SuppressWarnings("unused")
public final class GuiUtils
{

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

    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height) {
        drawTexturedModalRect(xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
    }

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
