////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.eventbus.api.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
public interface IGuiElement
        extends IGuiReference
{
    default void setup(IGui gui, GuiElementInst inst) {}

    default void tick(IGui gui, GuiElementInst inst) {}

    default void renderTick(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {}

    void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst);

    int getWidth();

    int getHeight();

    default boolean isVisible() { return true; }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Priorities
    {
        Priority[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Priorities.class)
    @Target(ElementType.TYPE)
    @interface Priority
    {
        EventPriority value();
        PriorityTarget target();
    }

    enum PriorityTarget
    {
        MOUSE_INPUT,
        KEY_INPUT;

        public static final PriorityTarget[] VALUES = values();
    }

    static boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY, int width, int height) {
        mouseX -= gui.getScreenPosX();
        mouseY -= gui.getScreenPosY();
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
