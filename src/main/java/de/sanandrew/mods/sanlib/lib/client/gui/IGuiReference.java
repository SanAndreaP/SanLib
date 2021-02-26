package de.sanandrew.mods.sanlib.lib.client.gui;

public interface IGuiReference
{
    default boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) { return false; }

    default boolean mouseClicked(IGui gui, double mouseX, double mouseY, int mouseButton) { return false; }

    default boolean mouseReleased(IGui gui, double mouseX, double mouseY, int state) { return false; }

    default boolean mouseDragged(IGui gui, double mouseX, double mouseY, int clickedMouseButton, double offsetX, double offsetY) { return false; }

    default void guiClosed(IGui gui) {}

    default boolean keyPressed(IGui gui, int keyCode, int unknown1, int unknown2) { return false; }

    default boolean keyReleased(IGui gui, int keyCode, int unknown1, int unknown2) { return false; }

    default boolean charTyped(IGui gui, char typedChar, int keyCode) { return false; }
}
