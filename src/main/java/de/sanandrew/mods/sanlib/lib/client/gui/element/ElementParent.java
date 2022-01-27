////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({ "WeakerAccess", "unused" })
public abstract class ElementParent<K>
        implements IGuiElement
{
    protected GuiElementInst[]            children = new GuiElementInst[0];
    protected Map<K, GuiElementInst> namedChildren = new ConcurrentHashMap<>();

    public void put(K id, @Nonnull GuiElementInst child) {
        this.namedChildren.put(id, child);
    }

    public GuiElementInst get(K id) {
        return this.namedChildren.get(id);
    }

    public GuiElementInst[] getAll() {
        return this.children;
    }

    public GuiElementInst remove(K id) {
        return this.namedChildren.remove(id);
    }

    public void clear() {
        this.namedChildren.clear();
    }

    public void update() {
        this.children = this.namedChildren.values().toArray(new GuiElementInst[0]);
    }

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

    protected boolean doWorkB(Predicate<IGuiElement> execElem) {
        for( GuiElementInst inst : this.children ) {
            if( inst.isVisible() && execElem.test(inst.get()) ) {
                return true;
            }
        }

        return false;
    }

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
