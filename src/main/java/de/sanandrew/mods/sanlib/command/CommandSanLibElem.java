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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashMap;
import java.util.Map;

public class CommandSanLibElem
{
    static final Map<String, CommandSanLib.TriConsumerCommEx<MinecraftServer, ICommandSender, String[]>> COMMANDS = new HashMap<>();
    static {
        COMMANDS.put("reloadModels", CommandSanLibElem::onReloadModels);
    }

    static void onReloadModels(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        PacketRegistry.sendToPlayer(new PacketReloadModels(), CommandBase.getCommandSenderAsPlayer(sender));
        CommandBase.getCommandSenderAsPlayer(sender).sendMessage(new TextComponentTranslation("commands.sanlib.reloadModels"));
    }
}
