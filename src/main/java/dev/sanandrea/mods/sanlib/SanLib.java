/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib;

import dev.sanandrea.mods.sanlib.lib.network.MessageRegistrar;
import dev.sanandrea.mods.sanlib.network.NetworkTestMain;
import dev.sanandrea.mods.sanlib.network.NetworkTestNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.ID)
public class SanLib
{
    public static final Logger LOG = LogManager.getLogger(Constants.ID);

    @SuppressWarnings({"unused", "java:S1118"})
    public SanLib(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SanLibConfig.SPEC);

        MessageRegistrar.create(modEventBus, "1.0").withBidiPayload(NetworkTestMain.TYPE, NetworkTestMain.STREAM_CODEC, HandlerThread.MAIN)
                                                   .withBidiPayload(NetworkTestNetwork.TYPE, NetworkTestNetwork.STREAM_CODEC, HandlerThread.NETWORK);
    }

}
