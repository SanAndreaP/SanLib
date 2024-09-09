/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.sanlib.network.MessageEntityRender;
import dev.sanandrea.mods.sanlib.network.MessageReloadModels;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.ID)
public class CommandSanLib
{
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String TICK_TIME        = "tickTime";
    private static final String NBT              = "nbt";

    private static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sanlib")
                                    .then(Commands.literal("reloadModels")
                                                  .executes(CommandSanLib::reloadModels))
                                    .then(Commands.literal("showEntityRender")
                                                  .then(Commands.argument("entityType", EntitySummonArgument.id())
                                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                                                .then(Commands.argument(NBT, NBTCompoundTagArgument.compoundTag())
                                                                              .then(backgroundColor(true))
                                                                              .executes(c -> openEntityRender(c, true, false, false)))
                                                                .then(backgroundColor(false))
                                                                .then(tickTime(false, false))
                                                                .executes(c -> openEntityRender(c, false, false, false)))
                                                  .executes(CommandSanLib::fail))
                                    .executes(CommandSanLib::fail)
        );
    }

    private static ArgumentBuilder<CommandSource, ?> tickTime(boolean withNbt, boolean withColor) {
        return Commands.argument(TICK_TIME, IntegerArgumentType.integer(-1))
                       .executes(c -> openEntityRender(c, withNbt, withColor, true));
    }

    private static ArgumentBuilder<CommandSource, ?> backgroundColor(boolean withNbt) {
        return Commands.argument(BACKGROUND_COLOR, ColorArgument.color())
                       .then(tickTime(withNbt, true))
                       .executes(c -> openEntityRender(c, withNbt, true, false));
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static int fail(CommandContext<CommandSource> c) {
        c.getSource().sendFailure(new TranslationTextComponent("commands.sanlib.errorArgs"));

        return 0;
    }

    private static int reloadModels(CommandContext<CommandSource> c) throws CommandSyntaxException {
        SanLib.NETWORK.sendToPlayer(new MessageReloadModels(), c.getSource().getPlayerOrException());
        c.getSource().sendSuccess(new TranslationTextComponent("commands.sanlib.reloadModels.success"), true);

        return 1;
    }

    private static int openEntityRender(CommandContext<CommandSource> c, boolean hasNbt, boolean hasColor, boolean hasTick)
            throws CommandSyntaxException
    {
        ResourceLocation rl   = EntitySummonArgument.getSummonableEntity(c, "entityType");
        CompoundNBT      nbt  = hasNbt ? NBTCompoundTagArgument.getCompoundTag(c, NBT) : null;
        TextFormatting   tf   = hasColor ? ColorArgument.getColor(c, BACKGROUND_COLOR) : TextFormatting.DARK_PURPLE;
        int              tick = hasTick ? IntegerArgumentType.getInteger(c, TICK_TIME) : 0;

        SanLib.NETWORK.sendToPlayer(new MessageEntityRender(rl, nbt, MiscUtils.get(tf.getColor(), 0xFF00FF) | 0xFF000000, tick),
                                    c.getSource().getPlayerOrException());

        return 1;
    }

    private CommandSanLib() { }
}
