package de.sanandrew.mods.sanlib.lib.client.gui;

public interface IGuiReference
{
    default boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) { return false; }

    default boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }

    default void onClose(IGui gui) {}

    default boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean charTyped(IGui gui, char typedChar, int keyCode) { return false; }
}
