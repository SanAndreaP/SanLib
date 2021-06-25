////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.network.MessageReloadModels;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.ID)
public class CommandSanLib
{
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("sanlib").then(
                LiteralArgumentBuilder.<CommandSource>literal("reloadModels").executes(c -> {
                    SanLib.NETWORK.sendToPlayer(new MessageReloadModels(), c.getSource().getPlayerOrException());
                    c.getSource().sendSuccess(new TranslationTextComponent("commands.sanlib.reloadModels.success"), true);
                    return 1;
                })
        ).executes(c -> {
            c.getSource().sendFailure(new TranslationTextComponent("commands.sanlib.errorArgs"));
            return 0;
        }));
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
