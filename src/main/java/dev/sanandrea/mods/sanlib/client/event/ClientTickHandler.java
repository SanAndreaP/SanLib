package dev.sanandrea.mods.sanlib.client.event;

import dev.sanandrea.mods.sanlib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientTickHandler
{
    private ClientTickHandler() { }

    private static long clientTicks = 0L;

    @SubscribeEvent
    public static void onGameTick(ClientTickEvent.Pre event) {
        Screen gui = Minecraft.getInstance().screen;
        if( gui == null || !gui.isPauseScreen() ) {
            clientTicks++;
        }
    }

    public static long getClientTicks() {
        return clientTicks;
    }
}
