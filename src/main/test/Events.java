/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = "santest")
public class Events
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        if( Keyboard.isKeyDown(38) && Minecraft.getInstance().currentScreen == null ) {
            Minecraft.getInstance().displayGuiScreen(new TestGui());
        }
    }
}
