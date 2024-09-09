/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class EnergyHelper
{
    private EnergyHelper() {}

    public static boolean canConnectEnergy(BlockEntity te, Direction facing) {
        IEnergyStorage stg = te.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(), te.getBlockState(), te, facing);
        return stg != null && (stg.canExtract() || stg.canReceive());
    }

    public static long receiveEnergy(BlockEntity te, Direction facing, long amount, boolean simulate) {
        IEnergyStorage stg = te.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(), te.getBlockState(), te, facing);
        if( stg != null ) {
            return stg.receiveEnergy((int) amount, simulate);
        }

        return 0;
    }

    public static long extractEnergy(BlockEntity te, Direction facing, long amount, boolean simulate) {
        IEnergyStorage stg = te.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(), te.getBlockState(), te, facing);
        if( stg != null ) {
            return stg.extractEnergy((int) amount, simulate);
        }

        return 0;
    }
}
