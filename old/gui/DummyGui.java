/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class DummyGui
        implements IGui
{
    public static final IGui INSTANCE = new DummyGui();
    private static final GuiDefinition EMPTY_DEF = getEmpty();

    @Override
    public Screen get() {
        return Minecraft.getInstance().screen;
    }

    @Override
    public GuiDefinition getDefinition() {
        return EMPTY_DEF;
    }

    @Override
    public int getScreenPosX() {
        return 0;
    }

    @Override
    public int getScreenPosY() {
        return 0;
    }

    private static GuiDefinition getEmpty() {
        try {
            return new EmptyGuiDefinition();
        } catch( IOException ex ) {
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN!", ex);
        }
    }

    public static final class EmptyGuiDefinition
            extends GuiDefinition
    {
        EmptyGuiDefinition() throws IOException {
            super(null, null);
        }

        @Override
        void register() { /* no-op */ }

        @Override
        public void initElement(GuiElementInst e) { /* no-op */ }

        @Override
        public ResourceLocation getTexture(JsonElement texture) {
            if( texture == null ) {
                throw new IllegalArgumentException("No explicit texture set!");
            }

            return super.getTexture(texture);
        }

        @Override
        public void initGui(IGui gui) { /* no-op */ }

        @Override
        public void drawBackground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) { /* no-op */ }

        @Override
        public void drawForeground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) { /* no-op */ }

        @Override
        boolean doWorkB(Predicate<IGuiElement> execElem, IGuiElement.PriorityTarget target) {
            return false;
        }

        @Override
        void doWorkV(Consumer<IGuiElement> execElem) { /* no-op */ }

        @Override
        public GuiElementInst getElementById(String id) {
            return null;
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) { /* no-op */ }

        @Override
        public void tick(IGui gui) { /* no-op */ }
    }
}
