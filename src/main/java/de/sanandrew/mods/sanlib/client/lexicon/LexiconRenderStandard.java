/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class LexiconRenderStandard
        implements ILexiconPageRender
{

    private int drawHeight;
    private List<GuiButton> entryButtons;

    @Override
    public String getId() {
        return LexiconInstance.RENDER_ID_STANDARD;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        this.entryButtons = entryButtons;
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        int entryWidth = helper.getLexicon().getEntryWidth();

        helper.drawTitleCenter(0, entry);

        this.drawHeight = 55;
        this.drawHeight += helper.drawContentString(2, this.drawHeight, entry, this.entryButtons);

        int height = entryWidth / 2;
        if( helper.tryDrawPicture(entry.getPicture(), 0, this.drawHeight + 8, entryWidth, height) ) {
            this.drawHeight += height + 8;
        }

        helper.drawItemGrid((entryWidth - 36) / 2, 12, mouseX, mouseY, scrollY, entry.getEntryIcon(), 2.0F, false);
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
