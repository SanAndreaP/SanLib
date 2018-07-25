/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import java.util.List;

public interface ILexiconGuiHelper
{
    void doEntryScissoring(int x, int y, int width, int height);

    void doEntryScissoring();

    GuiScreen getGui();

    void drawTextureRect(int x, int y, int w, int h, float uMin, float vMin, float uMax, float vMax);

    void drawRect(int x, int y, int w, int h, int color);

    void changePage(ILexiconGroup group, ILexiconEntry entry);

    int getGuiX();

    int getGuiY();

    int getEntryX();

    int getEntryY();

    ILexicon getLexicon();

    void setScroll(float scroll);

    void drawItemGrid(int x, int y, int mouseX, int mouseY, int scrollY, @Nonnull ItemStack stack, float scale, boolean drawTooltip);

    void drawContentString(String str, int x, int y, int wrapWidth, int textColor, @Nonnull List<GuiButton> newButtons);

    int getWordWrappedHeight(String str, int wrapWidth);

    FontRenderer getFontRenderer();

    void drawItem(@Nonnull ItemStack stack, int x, int y, double scale);

    void drawTextureRect(int x, int y, int u, int v, int w, int h);

    @SuppressWarnings("ConstantConditions")
    boolean tryLoadTexture(ResourceLocation location);

    boolean linkActionPerformed(GuiButton button);

    void initCraftings(@Nonnull NonNullList<IRecipe> recipes, List<CraftingGrid> grids);

    Vec3i getCraftingGridSize(CraftingGrid grid);

    void drawCraftingGrid(CraftingGrid grid, boolean isShapeless, int x, int y, int mouseX, int mouseY, int scrollY);

    boolean tryDrawPicture(ResourceLocation location, int x, int y, int width, int height);

    void drawTitleCenter(int y, ILexiconEntry entry);

    void drawTitle(int x, int y, ILexiconEntry entry);

    int drawContentString(int x, int y, ILexiconEntry entry, List<GuiButton> entryButtons);

    IGuiButtonEntry getNewEntryButton(int id, int x, int y, ILexiconEntry entry, FontRenderer fontRenderer);

    IGuiButtonLink getNewLinkButton(int id, int x, int y, String text, String link, FontRenderer fontRenderer);

    IGuiButtonLink getNewLinkButton(int id, int x, int y, String text, String link, FontRenderer fontRenderer, boolean trusted);
}
