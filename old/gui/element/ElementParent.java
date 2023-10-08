/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGuiElement;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Base class for elements able to host other elements within itself.
 * @param <K> the type of the ID used to fetch specific elements
 */
@SuppressWarnings("unused")
public abstract class ElementParent<K>
        implements IGuiElement
{
    protected GuiElementInst[]            children = new GuiElementInst[0];
    protected Map<K, GuiElementInst> namedChildren = new ConcurrentHashMap<>();

    /**
     * Adds and associates the specified child element with the specified ID.
     * If the ID already exists, the element previously associated with that ID gets replaced.<br>
     * <br>
     * New elements are only used after this parent has been updated
     * (by default, after {@link #update()} is called).
     * @param id the ID with which the specified element is to be associated
     * @param child the element to be associated with the specified ID
     */
    public void put(K id, @Nonnull GuiElementInst child) {
        this.namedChildren.put(id, child);
    }

    /**
     * Returns the child element to which the specified ID is mapped.
     * @param id the ID whose associated element is to be returned
     * @return the child element to which the specified ID is mapped, or <tt>null</tt> if the ID is not mapped
     */
    public GuiElementInst get(K id) {
        return this.namedChildren.get(id);
    }

    /**
     * Returns all child elements of this parent.<br>
     * <br>
     * This will only return elements after this parent has been updated at least once
     * (by default, after {@link #update()} is called).
     *
     * @return an array of elements contained within this parent
     */
    public GuiElementInst[] getAll() {
        return this.children;
    }

    /**
     * Removes the child element associated with the specified ID from this parent if it is present.<br>
     * <br>
     * Elements are only fully removed after this parent has been updated
     * (by default, after {@link #update()} is called).
     *
     * @param id the ID whose associated element is to be removed
     * @return the element being removed, or <tt>null</tt> if the ID was not mapped
     */
    public GuiElementInst remove(K id) {
        return this.namedChildren.remove(id);
    }

    /**
     * Removes all child elements from this parent.<br>
     * <br>
     * Elements are only fully removed after this parent has been updated
     * (by default, after {@link #update()} is called).
     */
    public void clear() {
        this.namedChildren.clear();
    }

    /**
     * Updates this parent in order to make it aware of new/removed child elements.
     */
    public void update() {
        this.children = this.namedChildren.values().toArray(new GuiElementInst[0]);
    }

    /**
     * Returns the amount of child elements from this parent.<br>
     * <br>
     * Size is only correct after adding/removing elements if this parent has been updated afterward
     * (by default, after {@link #update()} is called).
     *
     * @return the number of elements in this parent
     */
    public int size() {
        return this.children.length;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.update();

        this.doWorkV(e -> e.get().setup(gui, e));
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.doWorkV(e -> e.get().tick(gui, e));
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.doWorkV(e -> GuiDefinition.renderElement(gui, stack, x + e.pos[0], y + e.pos[1], mouseX, mouseY, partTicks, e, true));
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        return this.doWorkB(e -> e.mouseScrolled(gui, mouseX, mouseY, mouseScroll));
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.doWorkB(e -> e.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY));
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.doWorkB(e -> e.mouseClicked(gui, mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.doWorkB(e -> e.mouseReleased(gui, mouseX, mouseY, button));
    }

    @Override
    public void onClose(IGui gui) {
        this.doWorkV(e -> e.get().onClose(gui));
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.doWorkB(e -> e.charTyped(gui, typedChar, keyCode));
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.doWorkB(e -> e.keyPressed(gui, keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.doWorkB(e -> e.keyReleased(gui, keyCode, scanCode, modifiers));
    }

    /**
     * Iterates through all visible child elements and evaluates the specified predicate against each one.
     * If a child matches the predicate, the iteration stops.
     *
     * @return <tt>true</tt> if a child matches the predicate, <tt>false</tt> otherwise
     */
    protected boolean doWorkB(Predicate<IGuiElement> execElem) {
        for( GuiElementInst inst : this.children ) {
            if( inst.isVisible() && execElem.test(inst.get()) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Iterates through all visible child elements and calls the specified consumer on each one.
     */
    protected void doWorkV(Consumer<GuiElementInst> execElem) {
        for( GuiElementInst inst : this.children ) {
            if( inst.isVisible() ) {
                execElem.accept(inst);
            }
        }
    }

    @Override
    public int getWidth() {
        int w = 0;
        for( GuiElementInst inst : this.children ) {
            if( inst.isVisible() ) {
                w = Math.max(w, inst.pos[0] + inst.get().getWidth());
            }
        }
        return w;
    }

    @Override
    public int getHeight() {
        int h = 0;
        for( GuiElementInst inst : this.children ) {
            if( inst.isVisible() ) {
                h = Math.max(h, inst.pos[1] + inst.get().getHeight());
            }
        }
        return h;
    }

    /**
     * Converts the specified padding to a normalized array with a length of 4 -> [left, top, right, bottom].
     * <pre>
     * - null or zero-length             converts to [0, 0, 0, 0]
     * - [value]                         converts to [value, value, value, value]
     * - [horizontal, vertical]          converts to [horizontal, vertical, horizontal, vertical]
     * - [left, vertical, right]         converts to [left, vertical, right, vertical]
     * - [left, top, right, bottom, ...] converts to [left, top, right, bottom]
     * </pre>
     * @param padding the padding to be normalized
     * @return the normalized padding array
     */
    public static int[] adjustPadding(int[] padding) {
        if( padding == null || padding.length == 0 ) {
            return new int[] { 0, 0, 0, 0 };
        }

        switch( padding.length ) {
            case 1:  return new int[] { padding[0], padding[0], padding[0], padding[0] };
            case 2:  return new int[] { padding[0], padding[1], padding[0], padding[1] };
            case 3:  return new int[] { padding[0], padding[1], padding[2], padding[1] };
            default: return new int[] { padding[0], padding[1], padding[2], padding[3] };
        }
    }
}
