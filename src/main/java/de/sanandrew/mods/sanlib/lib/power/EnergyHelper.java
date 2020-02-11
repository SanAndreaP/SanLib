/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.power;

import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("unused")
public final class EnergyHelper
{
    private static final boolean COFH_EXISTS  = ReflectionUtils.doesClassExist("cofh.redstobeflux.api.IEnergyHandler");
    private static final boolean TESLA_EXISTS = ReflectionUtils.doesClassExist("net.darkhax.tesla.capability.TeslaCapabilities");

    public static boolean canConnectEnergy(TileEntity te, EnumFacing facing) {
        if( COFH_EXISTS && te instanceof cofh.redstoneflux.api.IEnergyConnection ) {
            return ((cofh.redstoneflux.api.IEnergyConnection) te).canConnectEnergy(facing);
        }
        if( TESLA_EXISTS ) {
            if( te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_CONSUMER, facing) != null ) {
                return true;
            }
            if( te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_PRODUCER, facing) != null ) {
                return true;
            }
        }
        IEnergyStorage stg = te.getCapability(CapabilityEnergy.ENERGY, facing);
        return stg != null && (stg.canExtract() || stg.canReceive());
    }

    public static long receiveEnergy(TileEntity te, EnumFacing facing, long amount, boolean simulate) {
        if( COFH_EXISTS && te instanceof cofh.redstoneflux.api.IEnergyReceiver ) {
            return ((cofh.redstoneflux.api.IEnergyReceiver) te).receiveEnergy(facing, (int) amount, simulate);
        }
        if( TESLA_EXISTS ) {
            net.darkhax.tesla.api.ITeslaConsumer consumer = te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_CONSUMER, facing);
            if( consumer != null ) {
                return consumer.givePower(amount, simulate);
            }
        }
        IEnergyStorage consumer = te.getCapability(CapabilityEnergy.ENERGY, facing);
        if( consumer != null ) {
            return consumer.receiveEnergy((int) amount, simulate);
        }

        return 0;
    }

    public static long extractEnergy(TileEntity te, EnumFacing facing, long amount, boolean simulate) {
        if( COFH_EXISTS && te instanceof cofh.redstoneflux.api.IEnergyProvider ) {
            return ((cofh.redstoneflux.api.IEnergyProvider) te).extractEnergy(facing, (int) amount, simulate);
        }
        if( TESLA_EXISTS ) {
            net.darkhax.tesla.api.ITeslaProducer producer = te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_PRODUCER, facing);
            if( producer != null ) {
                return producer.takePower(amount, simulate);
            }
        }
        IEnergyStorage provider = te.getCapability(CapabilityEnergy.ENERGY, facing);
        if( provider != null ) {
            return provider.extractEnergy((int) amount, simulate);
        }

        return 0;
    }
}
