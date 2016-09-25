/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.command;

import de.sanandrew.mods.sanlib.network.PacketRegistry;
import de.sanandrew.mods.sanlib.network.PacketReloadModels;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandSanLib
        extends CommandBase
{


    @Override
    public String getCommandName() {
        return "sanlib";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.sanlib.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if( args.length < 1 ) {
            throw new WrongUsageException("commands.sanlib.usage");
        } else {
            if( CommandSanLibElem.COMMANDS.containsKey(args[0]) ) {
                CommandSanLibElem.COMMANDS.get(args[0]).accept(server, sender, args);
//                TriConsumerCommEx<MinecraftServer, ICommandSender, String[]> ics = (server2, sender2, args2) -> {
//                    PacketRegistry.sendToPlayer(new PacketReloadModels(), getCommandSenderAsPlayer(sender2));
//                    getCommandSenderAsPlayer(sender2).addChatMessage(new TextComponentTranslation("commands.sanlib.reloadModels"));
//                };
//                ics.accept(server, sender, args);
            } else {
                throw new WrongUsageException("commands.sanlib.usage");
            }
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandSanLibElem.COMMANDS.keySet()) : Collections.emptyList();
    }

    @FunctionalInterface
    interface TriConsumerCommEx<T, U, V>
    {
        void accept(T t, U u, V v) throws CommandException;
    }
}
