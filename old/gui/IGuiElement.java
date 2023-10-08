/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
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

    /**
     * A builder that builds the specified type of element, either statically or loaded with JSON data.
     * This is preferrable to using the element's constructor.
     * Fields of builders implementing this interface can be shallow-copied into another builder by using {@link #copyValues(IBuilder, IBuilder)}.
     *
     * @param <T> the type of element being built
     */
    interface IBuilder<T extends IGuiElement>
            extends Cloneable
    {
        /**
         * Sanitizes the builder values before creating the element being built.
         * This only adds default values for optional fields of the builder.
         *
         * @param gui the GUI instance in which the elements are being built
         */
        void sanitize(IGui gui);

        /**
         * Builds the element and returns its instance.
         *
         * @param gui the GUI instance in which the elements are being built
         * @return the instance of the element being built
         * @throws IllegalStateException if any field required by the element is not set
         */
        T get(IGui gui);

//        /**
//         * Builds the element and returns its instance.
//         *
//         * @param gui the GUI instance in which the elements are being built
//         * @return the instance of the element being built
//         * @throws IllegalStateException if any field required by the element is not specified
//         */
//        IBuilder<T> loadFromJson(IGui gui, JsonObject json);

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
