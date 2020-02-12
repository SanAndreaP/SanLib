////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler
{
    public static int ticksInGame;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == TickEvent.Phase.END ) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if( gui == null || !gui.doesGuiPauseGame() ) {
                ticksInGame++;
            }
        }
    }
}
