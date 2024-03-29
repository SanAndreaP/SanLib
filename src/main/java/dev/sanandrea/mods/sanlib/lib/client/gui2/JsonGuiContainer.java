/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.SanLib;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings({"unused", "java:S5993"})
public abstract class JsonGuiContainer<T extends Container>
        extends ContainerScreen<T>
        implements IGui
{
    protected final GuiDefinition guiDefinition;

    public JsonGuiContainer(T container, PlayerInventory playerInv, ITextComponent title) {
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
    public void tick() {
        super.tick();

        this.guiDefinition.tick(this);
    }

    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        mStack.pushPose();
        mStack.translate(this.leftPos, this.topPos, 0.0F);
        this.renderGd(mStack, mouseX, mouseY, partialTicks);
        this.guiDefinition.drawForeground(this, mStack, mouseX, mouseY, partialTicks);
        mStack.popPose();
        this.renderTooltip(mStack, mouseX, mouseY);
    }

    protected void renderGd(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) { }

    @Override
    protected void renderBg(@Nonnull MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        this.guiDefinition.drawBackgroundContainer(this, mStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) { }

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
    public boolean mouseScrolled(double mx, double my, double scroll) {
        return this.guiDefinition.mouseScrolled(this, mx, my, scroll) || super.mouseScrolled(mx, my, scroll);
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
