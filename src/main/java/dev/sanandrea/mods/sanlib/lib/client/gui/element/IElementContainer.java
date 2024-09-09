package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;

import java.util.UUID;

public interface IElementContainer
{
    GuiElement putElement(String id, GuiElement child);

    GuiElement getElement(String id, boolean recursive);

    String getElementId(GuiElement child);

    GuiElement removeElement(String id);

    void clear();

    default GuiElement getElement(String id) {
        return this.getElement(id, true);
    }

    default GuiElement putElement(GuiElement child) {
        return this.putElement(UUID.randomUUID().toString(), child);
    }

    default void removeElement(GuiElement child) {
        this.removeElement(this.getElementId(child));
    }

    default boolean isImmutable() {
        return false;
    }
}
