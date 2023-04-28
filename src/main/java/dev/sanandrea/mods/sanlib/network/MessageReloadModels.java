/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.network;

import dev.sanandrea.mods.sanlib.lib.client.ModelJsonLoader;
import dev.sanandrea.mods.sanlib.lib.network.SimpleMessage;
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
