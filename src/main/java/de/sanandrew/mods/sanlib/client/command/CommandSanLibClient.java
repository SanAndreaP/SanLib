////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class CommandSanLibClient
        extends CommandBase
        implements IClientCommand
{
    @Override
    public String getName() {
        return "sanlibc";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.sanlibc.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if( args.length < 1 ) {
            throw new WrongUsageException("commands.sanlibc.usage");
        } else {
            if( CommandSanLibCElem.COMMANDS.containsKey(args[0]) ) {
                CommandSanLibCElem.COMMANDS.get(args[0]).accept(server, sender, args);
            } else {
                throw new WrongUsageException("commands.sanlibc.usage");
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandSanLibCElem.COMMANDS.keySet()) : Collections.emptyList();
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return true;
    }

    @FunctionalInterface
    interface TriConsumerCommEx<T, U, V>
    {
        void accept(T t, U u, V v) throws CommandException;
    }
}
