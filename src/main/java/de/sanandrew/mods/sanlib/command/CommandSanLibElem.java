/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.command;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.network.PacketRegistry;
import de.sanandrew.mods.sanlib.network.PacketReloadModels;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommandSanLibElem
{
    static final Map<String, CommandSanLib.TriConsumerCommEx<MinecraftServer, ICommandSender, String[]>> COMMANDS = new HashMap<>();
    static {
        COMMANDS.put("reloadModels", CommandSanLibElem::onReloadModels);
//        COMMANDS.put("rngUnitTest", CommandSanLibElem::xorUnitTest);
    }

    static void onReloadModels(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        PacketRegistry.sendToPlayer(new PacketReloadModels(), CommandBase.getCommandSenderAsPlayer(sender));
        CommandBase.getCommandSenderAsPlayer(sender).addChatMessage(new TextComponentTranslation("commands.sanlib.reloadModels"));
    }

    static void xorUnitTest(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        XorShiftRandom rng = new XorShiftRandom(0xdeadbeef);
        java.util.Random rnd = new Random(0xdeadbeef);
        long nt1, nt2;

//        for( int i = 0; i < 100; i++ ) {
//            System.out.println( rng.randomDouble() );
//        }

//        int nulls = 0;
//        for( int i = 0; i < 1_000_000_000; i++ ) {
//            if( rng.randomInt(100) == 0 ) {
//                nulls++;
//            }
//        }
//        System.out.println(nulls);

        for(int j = 0; j < 200; j++) {
            nt1 = System.nanoTime();
            for( int i = 0; i < 1_000_000_000; i++ ) {
                rnd.nextLong();
            }
            nt2 = System.nanoTime();

            SanLib.LOG.log(Level.INFO, String.format("Nanoseconds used for generating with java.util.Random x1Bil: %,d", nt2 - nt1));

            nt1 = System.nanoTime();
            for( int i = 0; i < 1_000_000_000; i++ ) {
                rng.randomLong();
            }
            nt2 = System.nanoTime();

            SanLib.LOG.log(Level.INFO, String.format("Nanoseconds used for generating with XorShiftRandom x1Bil:   %,d", nt2 - nt1));
        }
    }
}
