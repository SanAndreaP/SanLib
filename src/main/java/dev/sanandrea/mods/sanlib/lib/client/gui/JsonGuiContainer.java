/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui;

import dev.sanandrea.mods.sanlib.SanLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings({"unused", "java:S5993"})
public abstract class JsonGuiContainer<T extends AbstractContainerMenu>
        extends AbstractContainerScreen<T>
        implements IGui
{
    protected final GuiDefinition guiDefinition;

    public JsonGuiContainer(T container, Inventory playerInv, Component title) {
        super(container, playerInv, title);

        this.guiDefinition = buildGuiDefinition();
        if( this.guiDefinition != null ) {
            this.imageWidth = this.guiDefinition.width;
            this.imageHeight = this.guiDefinition.height;
        }
    }

    protected abstract GuiDefinition buildGuiDefinition();

    @Override
    protected void init() {
        super.init();

        try {
            if( GuiDefinition.initialize(this.guiDefinition, this) ) {
                this.initGd();
                this.tick();
            }
        } catch( Exception ex ) {
            SanLib.LOG.catching(ex);
            Objects.requireNonNull(this.minecraft).setScreen(null);
        }
    }

    protected void initGd() { }

    @Override
    public void containerTick() {
        super.tick();

        this.guiDefinition.tick(this);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().pushPose();
        graphics.pose().translate(this.leftPos, this.topPos, 0.0F);
        this.renderGd(graphics, mouseX, mouseY, partialTick);
        this.guiDefinition.drawForeground(this, graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    protected void renderGd(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.guiDefinition.drawBackgroundContainer(this, graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int x, int y) { }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return this.guiDefinition.mouseClicked(this, mx, my, btn) || super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        return this.guiDefinition.mouseDragged(this, mx, my, btn, dx, dy) || super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        return this.guiDefinition.mouseReleased(this, mx, my, btn) || super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        return this.guiDefinition.mouseScrolled(this, mx, my, scrollX, scrollY) || super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.guiDefinition.keyPressed(this, keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.guiDefinition.keyReleased(this, keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.guiDefinition.charTyped(this, typedChar, keyCode) || super.charTyped(typedChar, keyCode);
    }

    @Override
    public void onClose() {
        this.guiDefinition.onClose(this);

        super.onClose();
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDefinition;
    }

    @Override
    public int getPosX() {
        return this.leftPos;
    }

    @Override
    public int getPosY() {
        return this.topPos;
    }
}
