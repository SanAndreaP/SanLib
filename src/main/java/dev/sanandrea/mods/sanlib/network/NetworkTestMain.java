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

public record NetworkTestMain(String text)
        implements SimpleMessage<NetworkTestMain>
{
    public static final Type<NetworkTestMain> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.ID, "network_test_main"));

    public static final StreamCodec<ByteBuf, NetworkTestMain> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            NetworkTestMain::text,
            NetworkTestMain::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnMainServer(IPayloadContext context) {
        System.out.println("Handed to Server MAIN: " + this.text);
    }

    @Override
    public void handleOnMainClient(IPayloadContext context) {
        System.out.println("Handed to Client MAIN: " + this.text);
        PacketDistributor.sendToServer(new NetworkTestMain("hello server"));
    }
}
