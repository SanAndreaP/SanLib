package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGuiReference;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ElementParent
        extends GuiElement
        implements IGuiReference, IElementContainer
{
    protected BiMap<String, GuiElement> children = HashBiMap.create();

    @Override
    public void tick(IGui gui) {
        this.executeOnChildren(e -> e.tick(gui), true);
    }

    @Override
    public GuiElement putElement(String id, GuiElement child) {
        return this.children.put(id, child);
    }

    @Override
    public GuiElement getElement(String id) {
        GuiElement child = this.children.get(id);

        if( child == null ) {
            return MiscUtils.getFirst(this.children.values(), c -> {
                if( c instanceof IElementContainer ) {
                    return ((IElementContainer) c).getElement(id);
                } else {
                    return null;
                }
            }, Objects::nonNull);
        }

        return child;
    }

    @Override
    public String getElementId(GuiElement child) {
        return this.children.inverse().get(child);
    }

    @Override
    public GuiElement removeElement(String id) {
        return this.children.remove(id);
    }

    @Override
    public void clear() {
        this.children.clear();
    }

    @Override
    public void load(IGui gui) {
        this.executeOnChildren(c -> c.load(gui), false);
        super.load(gui);
    }

    @Override
    public void unload(IGui gui) {
        this.executeOnChildren(c -> c.unload(gui), false);
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
        this.executeOnChildren(c -> c.onClose(gui), false);
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
    
    protected void loadChildren(GuiDefinition guiDef, JsonElement data) {
        if( data == null ) {
            return;
        }

        if( data.isJsonObject() ) {
            data.getAsJsonObject().entrySet().forEach(e -> this.putElement(e.getKey(), guiDef.loadElement(e.getKey(), e.getValue().getAsJsonObject())));
        } else if( data.isJsonArray() ) {
            JsonArray arr = data.getAsJsonArray();
            for( int i = 0, max = arr.size(); i < max; i++ ) {
                this.putElement(guiDef.loadElement(String.format("#%d", i), arr.get(i).getAsJsonObject()));
            }
        }
    }

    protected abstract Collection<GuiElement> getVisibleChildren();

    /**
     * Iterates through all visible child elements and evaluates the specified predicate against each one.
     * If a child matches the predicate, the iteration stops.
     *
     * @return <tt>true</tt> if a child matches the predicate, <tt>false</tt> otherwise
     */
    protected boolean evaluateChildren(Predicate<GuiElement> execElem) {
        for( GuiElement inst : this.getVisibleChildren() ) {
            if( inst.isVisible() && execElem.test(inst) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Iterates through all visible child elements and calls the specified consumer on each one.
     */
    protected void executeOnChildren(Consumer<GuiElement> execElem, boolean onlyVisible) {
        for( GuiElement inst : (onlyVisible ? this.getVisibleChildren() : this.children.values()) ) {
            if( !onlyVisible || inst.isVisible() ) {
                execElem.accept(inst);
            }
        }
    }
}
