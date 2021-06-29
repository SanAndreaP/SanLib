////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "santest", value = Dist.CLIENT)
public class Events
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        // when up_arrow is pressed...
        if( event.getKey() == GLFW.GLFW_KEY_UP && Minecraft.getInstance().screen == null ) {
            Minecraft.getInstance().setScreen(new TestGui());
        }
        if( event.getKey() == GLFW.GLFW_KEY_DOWN && Minecraft.getInstance().screen == null ) {
//            try {
//                Minecraft.getInstance().displayGuiScreen(new GuiLexicon(Test.lexiconId));
//            } catch( IOException e ) {
//                e.printStackTrace();
//            }
        }
    }
}
