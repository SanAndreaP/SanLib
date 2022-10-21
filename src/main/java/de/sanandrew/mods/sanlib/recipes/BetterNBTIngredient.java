package de.sanandrew.mods.sanlib.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class BetterNBTIngredient
            extends Ingredient
{
    private final ItemStack stack;

    public BetterNBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.SingleItemList(stack)));
        this.stack = stack;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return input != null && ItemStackUtils.areEqualNbtFit(input, this.stack, false, false);
    }

    @Override
    @Nonnull
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        json.addProperty("count", stack.getCount());
        if( stack.hasTag() ) {
            json.addProperty("nbt", stack.getTag().toString());
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<BetterNBTIngredient>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public BetterNBTIngredient parse(PacketBuffer buffer) {
            return new BetterNBTIngredient(buffer.readItem());
        }

        @Override
        @Nonnull
        public BetterNBTIngredient parse(@Nonnull JsonObject json) {
            return new BetterNBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, BetterNBTIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
        }
    }
}
