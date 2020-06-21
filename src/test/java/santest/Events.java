////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import de.sanandrew.mods.sanlib.client.lexicon2.GuiLexicon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = "santest", value = Side.CLIENT)
public class Events
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        // when up_arrow is pressed...
        if( Keyboard.isKeyDown(Keyboard.KEY_UP) && Minecraft.getMinecraft().currentScreen == null ) {
            Minecraft.getMinecraft().displayGuiScreen(new TestGui());
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Minecraft.getMinecraft().currentScreen == null ) {
            try {
                Minecraft.getMinecraft().displayGuiScreen(new GuiLexicon(Test.lexiconId));
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
