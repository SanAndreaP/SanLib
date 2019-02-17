/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.network;

///**
// * An abstract implementation of {@link IMessageHandler} and {@link IMessage}.<br>
// * @param <M> The type of the class extending this implementation.
// */

/**
 * @deprecated See https://github.com/mezz/JustEnoughItems/blob/1.13-pre/src/main/java/mezz/jei/network/PacketHandler.java
 *                 https://github.com/mezz/JustEnoughItems/blob/1.13-pre/src/main/java/mezz/jei/network/Network.java
 * @param <M>
 */
@SuppressWarnings("unused")
@Deprecated
public abstract class AbstractMessage<M extends AbstractMessage>
//        implements IMessage, IMessageHandler<M, IMessage>
{
//    /**
//     * Receives the message and adds the packet handling method to the scheduler on the appropriate side
//     * (either {@link #handleClientMessage(AbstractMessage, EntityPlayer)} on the client or
//     * {@link #handleServerMessage(AbstractMessage, EntityPlayer)} on the server).
//     * @param message The message received.
//     * @param ctx The context of the message.
//     * @return An optional reply message. {@code null}, if no reply message is needed.
//     * @see IMessageHandler#onMessage(IMessage, MessageContext)
//     */
//    @Override
//    @SuppressWarnings("MethodCallSideOnly")
//    public IMessage onMessage(M message, MessageContext ctx) {
//        if( ctx.side.isClient() ) {
//            Minecraft.getInstance().addScheduledTask(() -> handleClientMessage(message, PlayerUtils.getClientPlayer()));
//        } else if( ctx.getServerHandler().player.getServer() != null ) {
//            ctx.getServerHandler().player.getServer().addScheduledTask(() -> handleServerMessage(message, ctx.getServerHandler().player));
//        }
//
//        return null;
//    }
//
//    /**
//     * Called when a packet is received on the client-side.
//     * @param packet The packet received.
//     * @param player The player receiving the packet.
//     */
//    @SideOnly(Side.CLIENT)
//    public abstract void handleClientMessage(M packet, EntityPlayer player);
//
//    /**
//     * Called when a packet is received on the server-side.
//     * @param packet The packet received.
//     * @param player The player that sent the packet.
//     */
//    public abstract void handleServerMessage(M packet, EntityPlayer player);
}
