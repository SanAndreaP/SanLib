/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.ID)
public class SanLib
{
    public static final Logger LOG = LogManager.getLogger(Constants.ID);

    @SuppressWarnings({"unused", "java:S1118"})
    public SanLib(IEventBus modEventBus, ModContainer modContainer) { }

}
