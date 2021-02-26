////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.util.ResourceLocation;

public class ContainerName
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("container_name");

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return gui.get().getTitle().getString();
//        if( gui instanceof IContainerName ) {
//            return ((IContainerName) gui).getContainerName();
//        } else {
//            throw new RuntimeException("Cannot use container_name on a GUI which doesn't implement IContainerName");
//        }
    }

    public interface IContainerName
    {
        String getContainerName();
    }
}
