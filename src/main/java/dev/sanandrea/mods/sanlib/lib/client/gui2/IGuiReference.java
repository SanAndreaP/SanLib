/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;

public interface IGuiReference
{
    default boolean mouseScrolled(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, double mouseX, double mouseY, double scroll) { return false; }

    default boolean mouseClicked(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseReleased(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, double mouseX, double mouseY, int button) { return false; }

    default boolean mouseDragged(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }

    default void onClose(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui) {}

    default boolean keyPressed(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean keyReleased(dev.sanandrea.mods.sanlib.lib.client.gui.IGui gui, int keyCode, int scanCode, int modifiers) { return false; }

    default boolean charTyped(IGui gui, char typedChar, int keyCode) { return false; }
}
