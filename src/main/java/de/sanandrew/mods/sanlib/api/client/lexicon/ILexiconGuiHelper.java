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

    /**
     * Draws the given text wrapped to the given wrap width.
     * This also converts newlines ({@code \n} to line breaks.<br>
     * Any links inside the text (using the syntax {@code {link:SHOWN_TEXT|TO_GROUP_ID:TO_ENTRY_ID_IN_GROUP}} or {@code {link:SHOWN_TEXT|HTTP(S)_LINK}} will be
     * converted to actual, clickable link buttons, saved inside the {@param entryButtons} parameter.
     * @param str the text that should be rendered.
     * @param x the x-position of the text block. Note that the distance from the right side of the entry area will be the same as this position.
     * @param y the y-position of the text block.
     * @param wrapWidth the width the text should be wrapped in.
     * @param textColor the color of the text.
     * @param links an instance of a list which is used to save the links detected; can be null, but no buttons will be created in that case.
     */
    void drawContentString(String str, int x, int y, int wrapWidth, int textColor, List<GuiButton> links);

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

    /**
     * Draws the text for this entry translated by the key {@code sanlib.lexicon.MY_MOD_ID.MY_GROUP_ID.MY_ENTRY_ID.text} that wraps around the entry area.
     * This also converts newlines ({@code \n} to line breaks.<br>
     * Any links inside the text (using the syntax {@code {link:SHOWN_TEXT|TO_GROUP_ID:TO_ENTRY_ID_IN_GROUP}} or {@code {link:SHOWN_TEXT|HTTP(S)_LINK}} will be
     * converted to actual, clickable link buttons, saved inside the {@param entryButtons} parameter.
     * @param x the x-position of the text block. Note that the distance from the right side of the entry area will be the same as this position.
     * @param y the y-position of the text block.
     * @param entry the entry whose text should be rendered
     * @param links an instance of a list which is used to save the links detected; can be null, but no buttons will be created in that case.
     * @return the total height of the text rendered
     */
    int drawContentString(int x, int y, ILexiconEntry entry, List<GuiButton> links);

    IGuiButtonEntry getNewEntryButton(int id, int x, int y, ILexiconEntry entry, FontRenderer fontRenderer);

    IGuiButtonLink getNewLinkButton(int id, int x, int y, String text, String link, FontRenderer fontRenderer);

    IGuiButtonLink getNewLinkButton(int id, int x, int y, String text, String link, FontRenderer fontRenderer, boolean trusted);

    List<GuiButton> getEntryButtonList();

    List<GuiButton> getGuiButtonList();

    NonNullList<IRecipe> getMatchingRecipes(ItemStack output);
}
