/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.network;

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
