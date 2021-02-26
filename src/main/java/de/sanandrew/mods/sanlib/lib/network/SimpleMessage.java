package de.sanandrew.mods.sanlib.lib.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class SimpleMessage
{
    public SimpleMessage() { }

    public SimpleMessage(PacketBuffer buffer) { }

    public abstract void encode(PacketBuffer buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> context);

    public boolean handleOnMainThread() {
        return false;
    }
}
