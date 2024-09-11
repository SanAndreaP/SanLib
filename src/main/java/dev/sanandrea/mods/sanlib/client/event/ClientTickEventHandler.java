package dev.sanandrea.mods.sanlib.client.event;

import dev.sanandrea.mods.sanlib.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientTickEventHandler
{
    private ClientTickEventHandler() { }

    private static long clientTicks = 0L;

    @SubscribeEvent
    public static void onGameTick(ClientTickEvent.Pre event) {
        clientTicks++;
    }

    public static long getClientTicks() {
        return clientTicks;
    }
}
