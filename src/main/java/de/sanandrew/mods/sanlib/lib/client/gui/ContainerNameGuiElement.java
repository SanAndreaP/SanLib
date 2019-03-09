package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class ContainerNameGuiElement
    extends TextGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("container_name");

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        if( gui instanceof IContainerName ) {
            return ((IContainerName) gui).getContainerName();
        } else {
            throw new RuntimeException("Cannot use container_name on a GUI which doesn't implement IContainerName");
        }
    }

    public interface IContainerName
    {
        String getContainerName();
    }
}
