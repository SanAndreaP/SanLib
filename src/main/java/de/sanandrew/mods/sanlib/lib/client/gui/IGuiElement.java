////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

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

        private static final PriorityTarget[] TARGETS = values();

        public static void forEach(Consumer<PriorityTarget> c) {
            for( PriorityTarget t : TARGETS ) {
                c.accept(t);
            }
        }
    }

    static boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY, int width, int height) {
        mouseX -= gui.getScreenPosX();
        mouseY -= gui.getScreenPosY();
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    interface IBuilder<T extends IGuiElement>
            extends Cloneable
    {
        void sanitize(IGui gui);
        T get(IGui gui);

        @SuppressWarnings({"java:S3011"})
        static <T extends IBuilder<? extends IGuiElement>, U extends T> U copyValues(@Nonnull T from, @Nonnull U to) {
            try {
                Class<?> superclass = to.getClass().getSuperclass();
                while( superclass != null ) {
                    for( Field f : superclass.getDeclaredFields() ) {
                        int mod = f.getModifiers();
                        if( !Modifier.isTransient(mod) && !Modifier.isFinal(mod) && !Modifier.isPrivate(mod) ) {
                            f.setAccessible(true);
                            f.set(to, f.get(from));
                        }
                    }

                    superclass = superclass.getSuperclass();
                }
            } catch( IllegalAccessException | SecurityException ex ) {
                throw new UnsupportedOperationException("Cannot copy builder fields", ex);
            }

            return to;
        }
    }
}
