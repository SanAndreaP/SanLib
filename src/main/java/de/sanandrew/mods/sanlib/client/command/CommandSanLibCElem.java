/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.command;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class CommandSanLibCElem
{
    static final Map<String, CommandSanLibClient.TriConsumerCommEx<MinecraftServer, ICommandSender, String[]>> COMMANDS = new HashMap<>();
    static {
        COMMANDS.put("reloadModels", (a,b,c) -> CommandSanLibCElem.onReloadModels());
    }

    static void onReloadModels() {
        ModelJsonLoader.REGISTERED_JSON_LOADERS.forEach(loader -> loader.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager()));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("commands.sanlibc.reloadModels"));
    }
}
