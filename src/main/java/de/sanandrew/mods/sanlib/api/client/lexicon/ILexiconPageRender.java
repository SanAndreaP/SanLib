/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public interface ILexiconPageRender
{
    /**
     * The ID for this render. Must be unique, recommended is your mod ID appended by a custom name.
     * @return an ID
     */
    String getId();

    /**
     * Initializes the renderer for the opened page. Called on {@link GuiScreen#initGui()}
     * @param entry the entry being opened
     * @param helper the lexicon helper
     */
    @SuppressWarnings("JavadocReference")
    void initPage(ILexiconEntry entry, ILexiconGuiHelper helper);

    /**
     * Called on {@link GuiScreen#tick()}, previously called "updateScreen"
     * @param helper the lexicon helper
     */
    default void tickScreen(ILexiconGuiHelper helper) { }

    default void renderPageOverlay(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, float partTicks) { }

    void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks);

    int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper);

    default void savePageState(NBTTagCompound nbt) { }

    default void loadPageState(NBTTagCompound nbt) { }

    default int shiftEntryPosY() { return 0; }

    default boolean mouseClicked(double mouseX, double mouseY, int mouseBtn, ILexiconGuiHelper helper) { return false; }

    default boolean charTyped(char typedChar, int keyCode, ILexiconGuiHelper helper) { return false; }
}
