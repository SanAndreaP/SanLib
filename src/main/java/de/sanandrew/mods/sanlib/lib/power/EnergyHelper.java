////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.power;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class EnergyHelper
{
    public static boolean canConnectEnergy(TileEntity te, Direction facing) {
        IEnergyStorage stg = te.getCapability(CapabilityEnergy.ENERGY, facing).orElse(null);
        return stg != null && (stg.canExtract() || stg.canReceive());
    }

    public static long receiveEnergy(TileEntity te, Direction facing, long amount, boolean simulate) {
        IEnergyStorage consumer = te.getCapability(CapabilityEnergy.ENERGY, facing).orElse(null);
        if( consumer != null ) {
            return consumer.receiveEnergy((int) amount, simulate);
        }

        return 0;
    }

    public static long extractEnergy(TileEntity te, Direction facing, long amount, boolean simulate) {
        IEnergyStorage provider = te.getCapability(CapabilityEnergy.ENERGY, facing).orElse(null);
        if( provider != null ) {
            return provider.extractEnergy((int) amount, simulate);
        }

        return 0;
    }
}
