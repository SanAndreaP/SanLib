/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

public interface IGuiReference
{
    default void tick(IGui gui) {}

    default boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) { return false; }

    default boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }

    default void onClose(IGui gui) {}

    default boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean charTyped(IGui gui, char typedChar, int keyCode) { return false; }
}
