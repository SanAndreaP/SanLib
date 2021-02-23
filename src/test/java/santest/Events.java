////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import de.sanandrew.mods.sanlib.client.lexicon2.GuiLexicon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.InputEvent;
import net.minecraftforge.api.distmarker.Dist;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = "santest", value = Dist.CLIENT)
public class Events
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        // when up_arrow is pressed...
        if( Keyboard.isKeyDown(Keyboard.KEY_UP) && Minecraft.getInstance().currentScreen == null ) {
            Minecraft.getInstance().displayGuiScreen(new TestGui());
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Minecraft.getInstance().currentScreen == null ) {
            try {
                Minecraft.getInstance().displayGuiScreen(new GuiLexicon(Test.lexiconId));
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
