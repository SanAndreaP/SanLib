/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.network;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Function;

public class PacketRegistry
{
    public static void initialize() {
        registerMessage(SanLib.network, PacketReloadModels.class, 0, Side.CLIENT);
    }

    public static void sendToAllAround(IMessage message, int dim, double x, double y, double z, double range) {
        SanLib.network.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
    }

    public static void sendToAllAround(IMessage message, int dim, Tuple pos, double range) {
        Function<?, Boolean> fkt = (obj) -> obj instanceof Number;
        if( pos.checkValue(0, fkt) && pos.checkValue(1, fkt) && pos.checkValue(2, fkt) ) {
            sendToAllAround(message, dim, pos.<Number>getValue(0).doubleValue(), pos.<Number>getValue(1).doubleValue(), pos.<Number>getValue(2).doubleValue(), range);
        }
    }

    public static void sendToAll(IMessage message) {
        SanLib.network.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        SanLib.network.sendToServer(message);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        SanLib.network.sendTo(message, player);
    }

    public static <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage (SimpleNetworkWrapper network, Class<T> clazz, int id, Side side) {
        network.registerMessage(clazz, clazz, id, side);
    }
}
