/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.network;

import de.sanandrew.mods.sanlib.lib.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public abstract class AbstractMessage<M extends AbstractMessage>
        implements IMessage, IMessageHandler<M, IMessage>
{
    @Override
    public IMessage onMessage(M message, MessageContext ctx) {
        if( ctx.side.isClient() ) {
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientMessage(message, PlayerUtils.getClientPlayer()));
        } else if( ctx.getServerHandler().playerEntity.getServer() != null ) {
            ctx.getServerHandler().playerEntity.getServer().addScheduledTask(() -> handleServerMessage(message, ctx.getServerHandler().playerEntity));
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public abstract void handleClientMessage(M packet, EntityPlayer player);

    public abstract void handleServerMessage(M packet, EntityPlayer player);
}
