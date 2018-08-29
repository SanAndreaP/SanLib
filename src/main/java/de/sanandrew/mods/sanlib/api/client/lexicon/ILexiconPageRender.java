/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
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
     * @param globalButtons Deprecated. Use {@link ILexiconGuiHelper#getGuiButtonList()}
     * @param entryButtons Deprecated. Use {@link ILexiconGuiHelper#getEntryButtonList()}
     */
    void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, @Deprecated List<GuiButton> globalButtons, @Deprecated List<GuiButton> entryButtons);

    /**
     * Called on {@link GuiScreen#updateScreen()},
     * @param helper the lexicon helper
     */
    default void updateScreen(ILexiconGuiHelper helper) { }

    default void renderPageOverlay(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, float partTicks) { }

    void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks);

    int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper);

    default void savePageState(NBTTagCompound nbt) { }

    default void loadPageState(NBTTagCompound nbt) { }

    default int shiftEntryPosY() {
        return 0;
    }

    default boolean actionPerformed(GuiButton button, ILexiconGuiHelper helper) {
        return helper.linkActionPerformed(button);
    }

    default void mouseClicked(int mouseX, int mouseY, int mouseBtn, ILexiconGuiHelper helper) throws IOException { }

    default void keyTyped(char typedChar, int keyCode, ILexiconGuiHelper helper) throws IOException { }
}
