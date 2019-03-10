/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DeprecatedIsStillUsed")
@SideOnly(Side.CLIENT)
@Deprecated
public class LexiconRenderCraftingGrid
        implements ILexiconPageRender
{
    private int drawHeight;
    private List<GuiButton> entryButtons;
    private List<CraftingGrid> crfGrids;

    @Override
    public String getId() {
        return LexiconInstance.RENDER_ID_CRAFTING;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        if( !(entry instanceof ILexiconEntryCraftingGrid) ) {
            return;
        }

        this.entryButtons = entryButtons;

        NonNullList<IRecipe> recipes = ((ILexiconEntryCraftingGrid) entry).getRecipes();

        if( recipes.isEmpty() ) {
            for( ItemStack result : ((ILexiconEntryCraftingGrid) entry).getRecipeResults() ) {
                recipes.addAll(helper.getMatchingRecipes(result));
            }
        }

        if( !recipes.isEmpty() ) {
            this.crfGrids = new ArrayList<>();
            helper.initCraftings(recipes, this.crfGrids);
        } else {
            this.crfGrids = null;
        }
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        helper.drawTitleCenter(0, entry);
        int entryWidth = helper.getLexicon().getEntryWidth();

        if( this.crfGrids != null && this.crfGrids.size() > 0 ) {
            CraftingGrid grid = this.crfGrids.get((int) ((System.nanoTime() / 1_000_000_000L) % this.crfGrids.size()));
            Vec3i gridSize = helper.getCraftingGridSize(grid);

            helper.drawCraftingGrid(grid, grid.isShapeless(), (entryWidth - gridSize.getX()) / 2, 12, mouseX, mouseY, scrollY);
            this.drawHeight = gridSize.getY() + 16;
        } else {
            helper.drawItemGrid((entryWidth - 36) / 2, 12, mouseX, mouseY, scrollY, entry.getEntryIcon(), 2.0F, false);
            this.drawHeight = 55;
        }

        this.drawHeight += helper.drawContentString(2, this.drawHeight, entry, this.entryButtons);

        int height = entryWidth / 2;
        if( helper.tryDrawPicture(entry.getPicture(), 0, this.drawHeight + 8, entryWidth, height) ) {
            this.drawHeight += height + 8;
        }

        this.drawHeight += 2;
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
