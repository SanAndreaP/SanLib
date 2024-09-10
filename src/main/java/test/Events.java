////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////
package test;

import dev.sanandrea.mods.sanlib.Constants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Constants.ID, value = Dist.CLIENT)
public class Events
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        // when up_arrow is pressed...
        if( event.getKey() == GLFW.GLFW_KEY_UP && Minecraft.getInstance().screen == null ) {
            Minecraft.getInstance().setScreen(new TestGui());
        }
    }
}
