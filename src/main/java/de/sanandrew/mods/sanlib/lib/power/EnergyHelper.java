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

@SuppressWarnings("ConstantConditions")
public final class EnergyHelper
{
    public static final boolean COFH_EXISTS = ReflectionUtils.doesClassExist("cofh.api.energy.IEnergyHandler");
    public static final boolean TESLA_EXISTS = ReflectionUtils.doesClassExist("net.darkhax.tesla.capability.TeslaCapabilities");

    public static boolean canConnectEnergy(TileEntity te, EnumFacing facing) {
        //noinspection SimplifiableIfStatement
        if( COFH_EXISTS && te instanceof cofh.api.energy.IEnergyHandler ) {
            return ((cofh.api.energy.IEnergyHandler) te).canConnectEnergy(facing);
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

    public static long receiveEnergy(TileEntity te, EnumFacing facing, int amount, boolean simulate) {
        if( COFH_EXISTS && te instanceof cofh.api.energy.IEnergyReceiver ) {
            return ((cofh.api.energy.IEnergyReceiver) te).receiveEnergy(facing, amount, simulate);
        }
        if( TESLA_EXISTS ) {
            net.darkhax.tesla.api.ITeslaConsumer consumer = te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_CONSUMER, facing);
            if( consumer != null ) {
                return consumer.givePower(amount, simulate);
            }
        }
        IEnergyStorage stg = te.getCapability(CapabilityEnergy.ENERGY, facing);
        if( stg != null ) {
            return stg.receiveEnergy(amount, simulate);
        }

        return 0;
    }

    public static long extractEnergy(TileEntity te, EnumFacing facing, int amount, boolean simulate) {
        if( COFH_EXISTS && te instanceof cofh.api.energy.IEnergyProvider ) {
            return ((cofh.api.energy.IEnergyProvider) te).extractEnergy(facing, amount, simulate);
        }
        if( TESLA_EXISTS ) {
            net.darkhax.tesla.api.ITeslaProducer producer = te.getCapability(net.darkhax.tesla.capability.TeslaCapabilities.CAPABILITY_PRODUCER, facing);
            if( producer != null ) {
                return producer.takePower(amount, simulate);
            }
        }
        IEnergyStorage stg = te.getCapability(CapabilityEnergy.ENERGY, facing);
        if( stg != null ) {
            return stg.extractEnergy(amount, simulate);
        }

        return 0;
    }
}
