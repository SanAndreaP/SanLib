////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.Locale;

public final class CraftingGrid
{
    private static final ItemStack[] EMPTY_ITEMS = new ItemStack[] {ItemStack.EMPTY};

    private final ItemStack[][][] items;
    private final ItemStack result;
    private final boolean isShapeless;

    public CraftingGrid(int width, int height, boolean shapeless, ItemStack result) {
        this.items = new ItemStack[width][height][];
        this.result = result;
        this.isShapeless = shapeless;
    }

    public CraftingGrid(int width, int height, IRecipe recipe) {
        this(width, height, recipe.getClass().getName().toLowerCase(Locale.ROOT).contains("shapeless"), recipe.getRecipeOutput());
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for( int y = 0; y < height; y++ ) {
            for( int x = 0; x < width; x++ ) {
                int ind = x + y * width;
                if( ind < ingredients.size() ) {
                    this.items[x][y] = ingredients.get(ind).getMatchingStacks();
                } else {
                    this.items[x][y] = EMPTY_ITEMS;
                }
            }
        }
    }

    public int getWidth() {
        return this.items.length;
    }

    public int getHeight() {
        return this.items[0].length;
    }

    public boolean isShapeless() {
        return this.isShapeless;
    }

    public NonNullList<ItemStack> getItemsAt(int row, int col) {
        return NonNullList.from(ItemStack.EMPTY, this.items[row][col]);
    }

    public void putItemsAt(int row, int col, ItemStack... stacks) {
        this.items[row][col] = stacks;
    }

    public ItemStack getResult() {
        return this.result;
    }
}
