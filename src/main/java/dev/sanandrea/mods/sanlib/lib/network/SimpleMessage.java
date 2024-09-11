/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("unused")
public interface SimpleMessage<T extends SimpleMessage<T>>
        extends CustomPacketPayload
{
    default void handleOnMainServer(final IPayloadContext context) {}
    default void handleOnMainClient(final IPayloadContext context) {}
    default void handleOnNetworkServer(final IPayloadContext context) {}
    default void handleOnNetworkClient(final IPayloadContext context) {}
}
