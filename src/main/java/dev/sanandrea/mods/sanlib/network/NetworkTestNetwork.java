package dev.sanandrea.mods.sanlib.network;

import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.network.SimpleMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record NetworkTestNetwork(String text)
        implements SimpleMessage<NetworkTestNetwork>
{
    public static final Type<NetworkTestNetwork> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.ID, "network_test_network"));

    public static final StreamCodec<ByteBuf, NetworkTestNetwork> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            NetworkTestNetwork::text,
            NetworkTestNetwork::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnNetworkServer(IPayloadContext context) {
        System.out.println("Handed to Server NETWORK: " + this.text);
    }

    @Override
    public void handleOnNetworkClient(IPayloadContext context) {
        System.out.println("Handed to Client NETWORK: " + this.text);
        PacketDistributor.sendToServer(new NetworkTestNetwork("hello server"));
    }
}
