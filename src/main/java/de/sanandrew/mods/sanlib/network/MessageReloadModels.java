package de.sanandrew.mods.sanlib.network;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageReloadModels
        extends SimpleMessage
{
    public MessageReloadModels() { }

    @SuppressWarnings("unused")
    public MessageReloadModels(PacketBuffer buffer) { }

    @Override
    public void encode(PacketBuffer buffer) { }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context c = context.get();
        if( c.getDirection().getReceptionSide() == LogicalSide.CLIENT ) {
            c.enqueueWork(this::reloadModels);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void reloadModels() {
        ModelJsonLoader.REGISTERED_JSON_LOADERS.forEach(l -> l.reload(null));
    }
}
