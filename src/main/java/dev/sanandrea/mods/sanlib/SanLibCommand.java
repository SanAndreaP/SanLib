package dev.sanandrea.mods.sanlib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.sanandrea.mods.sanlib.network.NetworkTestMain;
import dev.sanandrea.mods.sanlib.network.NetworkTestNetwork;
import dev.sanandrea.mods.sanlib.network.OpenTestGUI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Constants.ID, bus = EventBusSubscriber.Bus.GAME)
public class SanLibCommand
{
    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sanlib")
                                    .then(Commands.literal("testNetwork").executes(SanLibCommand::testNetwork))
                                    .then(Commands.literal("openTestGUI").executes(SanLibCommand::openTestGui))
                                    .executes(SanLibCommand::fail));
    }

    private static int fail(CommandContext<CommandSourceStack> c) {
        c.getSource().sendFailure(Component.translatable("commands.sanlib.errorArgs"));

        return 0;
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static int testNetwork(CommandContext<CommandSourceStack> c) {
        if( c.getSource().getPlayer() instanceof ServerPlayer player ) {
            PacketDistributor.sendToPlayer(player, new NetworkTestMain("hello player"));
            PacketDistributor.sendToPlayer(player, new NetworkTestNetwork("hello player"));

            c.getSource().sendSuccess(() -> Component.translatable("commands.sanlib.networkTest"), false);
        }

        return 0;
    }

    private static int openTestGui(CommandContext<CommandSourceStack> c) {
        if( c.getSource().getPlayer() instanceof ServerPlayer player ) {
            PacketDistributor.sendToPlayer(player, new OpenTestGUI());

            c.getSource().sendSuccess(() -> Component.translatable("commands.sanlib.openTestGUI"), false);
        }

        return 0;
    }
}
