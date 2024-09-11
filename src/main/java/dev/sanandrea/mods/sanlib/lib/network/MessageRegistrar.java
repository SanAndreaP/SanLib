/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class MessageRegistrar
{
    private final String                          version;
    private final Map<HandlerThread, PayloadList> payloads = new EnumMap<>(HandlerThread.class);

    private MessageRegistrar(String version) {
        this.version = version;
    }

    public static MessageRegistrar create(IEventBus modEventBus, final String version) {
        final MessageRegistrar registrar = new MessageRegistrar(version);

        modEventBus.addListener(registrar::registerPayloadHandler);

        return registrar;
    }

    public <T extends SimpleMessage<T>> MessageRegistrar withBidiPayload
            (CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, HandlerThread thread)
    {
        this.payloads.computeIfAbsent(thread, t -> new PayloadList()).bidi.push(new Payload<>(type, codec));

        return this;
    }

    public <T extends SimpleMessage<T>> MessageRegistrar withClientPayload
            (CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, HandlerThread thread)
    {
        this.payloads.computeIfAbsent(thread, t -> new PayloadList()).client.push(new Payload<>(type, codec));

        return this;
    }

    public <T extends SimpleMessage<T>> MessageRegistrar withServerPayload
            (CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, HandlerThread thread)
    {
        this.payloads.computeIfAbsent(thread, t -> new PayloadList()).server.push(new Payload<>(type, codec));

        return this;
    }


    private void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(this.version).optional();
        if( payloads.containsKey(HandlerThread.MAIN) ) {
            PayloadList payloadList = payloads.get(HandlerThread.MAIN);
            if( payloadList.hasItems() ) {
                payloadList.register(registrar, false);
            }
        }
        if( payloads.containsKey(HandlerThread.NETWORK) ) {
            PayloadList payloadList = payloads.get(HandlerThread.NETWORK);
            if( payloadList.hasItems() ) {
                registrar = registrar.executesOn(HandlerThread.NETWORK);
                payloadList.register(registrar, true);
            }
        }
    }


    public record Payload<T extends SimpleMessage<T>>(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        private void playBidirectional(PayloadRegistrar registrar, boolean onNetwork) {
            IPayloadHandler<T> handler;
            if( onNetwork ) {
                handler = new DirectionalPayloadHandler<>(SimpleMessage::handleOnNetworkClient, SimpleMessage::handleOnNetworkServer);
            } else {
                handler = new DirectionalPayloadHandler<>(SimpleMessage::handleOnMainClient, SimpleMessage::handleOnMainServer);
            }
            registrar.playBidirectional(this.type, this.codec, handler);
        }

        private void playToClient(PayloadRegistrar registrar, boolean onNetwork) {
            IPayloadHandler<T> handler;
            if( onNetwork ) {
                handler = SimpleMessage::handleOnNetworkClient;
            } else {
                handler = SimpleMessage::handleOnMainClient;
            }
            registrar.playToClient(this.type, this.codec, handler);
        }

        private void playToServer(PayloadRegistrar registrar, boolean onNetwork) {
            IPayloadHandler<T> handler;
            if( onNetwork ) {
                handler = SimpleMessage::handleOnNetworkServer;
            } else {
                handler = SimpleMessage::handleOnMainServer;
            }
            registrar.playToServer(this.type, this.codec, handler);
        }
    }

    private static class PayloadList
    {
        private final Deque<Payload<?>> bidi   = new ArrayDeque<>();
        private final Deque<Payload<?>> client = new ArrayDeque<>();
        private final Deque<Payload<?>> server = new ArrayDeque<>();

        private PayloadList() {}

        private boolean hasItems() {
            return !bidi.isEmpty() || !client.isEmpty() || server.isEmpty();
        }

        private void register(PayloadRegistrar registrar, boolean onNetwork) {
            while( !bidi.isEmpty() ) {
                bidi.pop().playBidirectional(registrar, onNetwork);
            }
            while( !client.isEmpty() ) {
                client.pop().playToClient(registrar, onNetwork);
            }
            while( !server.isEmpty() ) {
                server.pop().playToServer(registrar, onNetwork);
            }
        }
    }
}
