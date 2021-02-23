////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.network;

import de.sanandrew.mods.sanlib.lib.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An abstract implementation of {@link IMessageHandler} and {@link IMessage}.<br>
 * @param <M> The type of the class extending this implementation.
 */
@SuppressWarnings("unused")
public abstract class AbstractMessage<M extends AbstractMessage>
        implements IMessage, IMessageHandler<M, IMessage>
{
    /**
     * Receives the message and adds the packet handling method to the scheduler on the appropriate side
     * (either {@link #handleClientMessage(AbstractMessage, EntityPlayer)} on the client or
     * {@link #handleServerMessage(AbstractMessage, EntityPlayer)} on the server).
     * @param message The message received.
     * @param ctx The context of the message.
     * @return An optional reply message. {@code null}, if no reply message is needed.
     * @see IMessageHandler#onMessage(IMessage, MessageContext)
     */
    @Override
    @SuppressWarnings("MethodCallSideOnly")
    public IMessage onMessage(M message, MessageContext ctx) {
        if( ctx.side.isClient() ) {
            Minecraft.getInstance().addScheduledTask(() -> handleClientMessage(message, PlayerUtils.getClientPlayer()));
        } else if( ctx.getServerHandler().player.getServer() != null ) {
            ctx.getServerHandler().player.getServer().addScheduledTask(() -> handleServerMessage(message, ctx.getServerHandler().player));
        }

        return null;
    }

    /**
     * Called when a packet is received on the client-side.
     * @param packet The packet received.
     * @param player The player receiving the packet.
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void handleClientMessage(M packet, EntityPlayer player);

    /**
     * Called when a packet is received on the server-side.
     * @param packet The packet received.
     * @param player The player that sent the packet.
     */
    public abstract void handleServerMessage(M packet, EntityPlayer player);
}
