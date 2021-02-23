////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.command;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CommandSanLibCElem
{
    static void onReloadModels() {
//        ModelJsonLoader.REGISTERED_JSON_LOADERS.forEach(loader -> loader.onResourceManagerReload(Minecraft.getInstance().getResourceManager()));
//        Minecraft.getInstance().player.sendMessage(new TextComponentTranslation("commands.sanlibc.reloadModels"));
    }
}
