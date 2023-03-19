package de.sanandrew.mods.sanlib.network;

import de.sanandrew.mods.sanlib.client.EntityRenderScreen;
import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class MessageEntityRender
        extends SimpleMessage
{
    @Nonnull
    private final ResourceLocation type;
    private final CompoundNBT nbt;
    private final int color;
    private final int tick;

    public MessageEntityRender(@Nonnull ResourceLocation type, CompoundNBT nbt, int color, int tick) {
        this.type = type;
        this.nbt = nbt;
        this.color = color;
        this.tick = tick;
    }

    public MessageEntityRender(PacketBuffer buffer) {
        this.type = buffer.readResourceLocation();
        this.nbt = buffer.readBoolean() ? buffer.readNbt() : null;
        this.color = buffer.readVarInt();
        this.tick = buffer.readVarInt();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.type);
        if( this.nbt != null ) {
            buffer.writeBoolean(true);
            buffer.writeNbt(this.nbt);
        } else {
            buffer.writeBoolean(false);
        }
        buffer.writeVarInt(this.color);
        buffer.writeVarInt(this.tick);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context c = context.get();
        if( c.getDirection().getReceptionSide() == LogicalSide.CLIENT ) {
            c.enqueueWork(this::showEntityRender);
        }
    }

    @SuppressWarnings("deprecation")
    @OnlyIn(Dist.CLIENT)
    private void showEntityRender() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new EntityRenderScreen(Registry.ENTITY_TYPE.getOptional(this.type)
                                                                                                          .orElseThrow(() -> new IllegalArgumentException("entity type not found")),
                                                                                      this.nbt, this.color, this.tick));
    }
}
