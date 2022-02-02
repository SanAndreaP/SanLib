////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;

import java.util.Objects;

@Mod("santest")
public class TestMod
{
    public TestMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        testTickFormat();
        testNbtMethods();
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(TestBlock.INSTANCE);
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(TestBlock.INSTANCE, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)).setRegistryName(
                Objects.requireNonNull(TestBlock.INSTANCE.getRegistryName())));
    }

    private static void testTickFormat() {
        System.out.println(MiscUtils.getTimeFromTicks(5, 0));
        System.out.println(MiscUtils.getTimeFromTicks(5, 1));
        System.out.println(MiscUtils.getTimeFromTicks(5, 2));
        System.out.println(MiscUtils.getTimeFromTicks(1, 0));
        System.out.println(MiscUtils.getTimeFromTicks(1, 1));
        System.out.println(MiscUtils.getTimeFromTicks(1, 2));
    }

    private static void testNbtMethods() {
        CompoundNBT numericNbt1 = new CompoundNBT(); numericNbt1.putInt("num", 16); numericNbt1.putLong("num2", 32);
        CompoundNBT numericNbt2 = new CompoundNBT(); numericNbt2.putByte("num", (byte) 16);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(numericNbt1, numericNbt2, false) = %s, should be true",
                                     NBTUtils.doesNbtContainOther(numericNbt1, numericNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(numericNbt2, numericNbt1, false) = %s, should be false",
                                     NBTUtils.doesNbtContainOther(numericNbt2, numericNbt1, false)));

        CompoundNBT tagNbt1 = new CompoundNBT(); tagNbt1.put("tag", numericNbt1);
        CompoundNBT tagNbt2 = new CompoundNBT(); tagNbt2.put("tag", numericNbt2);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(tagNbt1, tagNbt2, false) = %s, should be true",
                                     NBTUtils.doesNbtContainOther(tagNbt1, tagNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(tagNbt2, tagNbt1, false) = %s, should be false",
                                     NBTUtils.doesNbtContainOther(tagNbt2, tagNbt1, false)));

        ListNBT list1 = new ListNBT(); list1.add(numericNbt1); list1.add(new CompoundNBT());
        ListNBT list2 = new ListNBT(); list2.add(numericNbt2);
        CompoundNBT listNbt1 = new CompoundNBT(); listNbt1.put("list", list1);
        CompoundNBT listNbt2 = new CompoundNBT(); listNbt2.put("list", list2);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(listNbt1, listNbt2, false) = %s, should be true",
                                     NBTUtils.doesNbtContainOther(listNbt1, listNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(listNbt2, listNbt1, false) = %s, should be false",
                                     NBTUtils.doesNbtContainOther(listNbt2, listNbt1, false)));
    }
}
