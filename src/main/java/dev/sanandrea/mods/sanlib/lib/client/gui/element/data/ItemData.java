package dev.sanandrea.mods.sanlib.lib.client.gui.element.data;

import com.google.gson.JsonElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemData
        extends StatedData<ItemStack>
{
    protected ItemData(@NotNull ItemStack regular, @Nullable ItemStack hover, @Nullable ItemStack disabled) {
        super(regular, hover, disabled, ItemStackUtils::toJson);
    }

    public static ItemData fromJson(GuiDefinition guiDef, JsonElement data) {
        return StatedData.fromJson(guiDef, data, ItemData::new, ((gd, d, def) -> ItemStackUtils.fromJson(d)));
    }
}
