////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.sanandrew.mods.sanlib.Constants;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.ID)
public class CommandSanLib
{
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("sanlib").then(
                LiteralArgumentBuilder.<CommandSource>literal("reloadModels").executes(c -> {
                    //TODO: make command work on client only!
                    System.out.println("NYI");
                    return 1;
                })
        ).executes(c -> {
            //TODO: proper error message?
            System.out.println("Called sanlib with no arguments");
            return 1;
        }));
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
