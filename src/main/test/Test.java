/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Level;

@Mod(modid = "santest", dependencies = "required-after:" + SanLib.ID)
public class Test
{
    @Mod.Instance
    public static Test instance;

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        NBTTagCompound numericNbt1 = new NBTTagCompound(); numericNbt1.setInteger("num", 16); numericNbt1.setLong("num2", 32);
        NBTTagCompound numericNbt2 = new NBTTagCompound(); numericNbt2.setByte("num", (byte) 16);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(numericNbt1, numericNbt2, false) = %s, should be true",
                                     MiscUtils.doesNbtContainOther(numericNbt1, numericNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(numericNbt2, numericNbt1, false) = %s, should be false",
                                     MiscUtils.doesNbtContainOther(numericNbt2, numericNbt1, false)));

        NBTTagCompound tagNbt1 = new NBTTagCompound(); tagNbt1.setTag("tag", numericNbt1);
        NBTTagCompound tagNbt2 = new NBTTagCompound(); tagNbt2.setTag("tag", numericNbt2);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(tagNbt1, tagNbt2, false) = %s, should be true",
                                     MiscUtils.doesNbtContainOther(tagNbt1, tagNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(tagNbt2, tagNbt1, false) = %s, should be false",
                                     MiscUtils.doesNbtContainOther(tagNbt2, tagNbt1, false)));

        NBTTagList list1 = new NBTTagList(); list1.appendTag(numericNbt1); list1.appendTag(new NBTTagCompound());
        NBTTagList list2 = new NBTTagList(); list2.appendTag(numericNbt2);
        NBTTagCompound listNbt1 = new NBTTagCompound(); listNbt1.setTag("list", list1);
        NBTTagCompound listNbt2 = new NBTTagCompound(); listNbt2.setTag("list", list2);

        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(listNbt1, listNbt2, false) = %s, should be true",
                                     MiscUtils.doesNbtContainOther(listNbt1, listNbt2, false)));
        SanLib.LOG.log(Level.INFO,
                       String.format("MiscUtils.doesNbtContainOther(listNbt2, listNbt1, false) = %s, should be false",
                                     MiscUtils.doesNbtContainOther(listNbt2, listNbt1, false)));
    }
}
