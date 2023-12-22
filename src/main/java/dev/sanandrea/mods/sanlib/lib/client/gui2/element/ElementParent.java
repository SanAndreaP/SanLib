package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ElementParent
        extends GuiElement
{
    protected Map<String, GuiElement> children = new HashMap<>();
    private GuiElement[] childrenCache;

    @Override
    public void update(IGui gui, boolean updateState) {
        if( updateState ) {
            this.childrenCache = this.children.values().toArray(new GuiElement[0]);
        }
    }

    @Override
    public void load(IGui gui) {
        this.executeOnChildren(c -> c.load(gui));
        super.load(gui);
    }

    @Override
    public void unload(IGui gui) {
        this.executeOnChildren(c -> c.unload(gui));
        super.unload(gui);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) {
        return this.evaluateChildren(c -> c.mouseScrolled(gui, mouseX, mouseY, scroll))
               || super.mouseScrolled(gui, mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.evaluateChildren(c -> c.mouseClicked(gui, mouseX, mouseY, button))
               || super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.evaluateChildren(c -> c.mouseReleased(gui, mouseX, mouseY, button))
               || super.mouseReleased(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.evaluateChildren(c -> c.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY))
               || super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onClose(IGui gui) {
        this.executeOnChildren(c -> c.onClose(gui));
        super.onClose(gui);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.evaluateChildren(c -> c.keyPressed(gui, keyCode, scanCode, modifiers))
               || super.keyPressed(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.evaluateChildren(c -> c.keyReleased(gui, keyCode, scanCode, modifiers))
               || super.keyReleased(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.evaluateChildren(c -> c.charTyped(gui, typedChar, keyCode))
               || super.charTyped(gui, typedChar, keyCode);
    }



    /**
     * Iterates through all visible child elements and evaluates the specified predicate against each one.
     * If a child matches the predicate, the iteration stops.
     *
     * @return <tt>true</tt> if a child matches the predicate, <tt>false</tt> otherwise
     */
    protected boolean evaluateChildren(Predicate<GuiElement> execElem) {
        for( GuiElement inst : this.childrenCache ) {
            if( inst.isVisible() && execElem.test(inst) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Iterates through all visible child elements and calls the specified consumer on each one.
     */
    protected void executeOnChildren(Consumer<GuiElement> execElem) {
        for( GuiElement inst : this.childrenCache ) {
            if( inst.isVisible() ) {
                execElem.accept(inst);
            }
        }
    }
}
