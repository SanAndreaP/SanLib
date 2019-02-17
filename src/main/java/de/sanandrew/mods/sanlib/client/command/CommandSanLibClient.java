/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSanLibClient
//        extends Command<>
//        implements IClientCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("sanlib").requires(src -> src.getWorld().isRemote)
                                                  .then(RequiredArgumentBuilder.argument("subcommand", StringArgumentType.string()))
                                                    .executes(CommandSanLibClient::onReloadModels));
    }

    //TODO: register in init!
    private static int onReloadModels(CommandContext<CommandSource> src) {
        ModelJsonLoader.REGISTERED_JSON_LOADERS.forEach(loader -> loader.onResourceManagerReload(Minecraft.getInstance().getResourceManager()));
        Minecraft.getInstance().player.sendMessage(new TextComponentTranslation("commands.sanlibc.reloadModels"));

        return 1;
    }
//    @Override
//    public String getName() {
//        return "sanlibc";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 2;
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.sanlibc.usage";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if( args.length < 1 ) {
//            throw new WrongUsageException("commands.sanlibc.usage");
//        } else {
//            if( CommandSanLibCElem.COMMANDS.containsKey(args[0]) ) {
//                CommandSanLibCElem.COMMANDS.get(args[0]).accept(server, sender, args);
//            } else {
//                throw new WrongUsageException("commands.sanlibc.usage");
//            }
//        }
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
//        return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandSanLibCElem.COMMANDS.keySet()) : Collections.emptyList();
//    }
//
//    @Override
//    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
//        return true;
//    }

    @FunctionalInterface
    interface TriConsumerCommEx<T, U, V>
    {
        void accept(T t, U u, V v) throws CommandException;
    }
}
