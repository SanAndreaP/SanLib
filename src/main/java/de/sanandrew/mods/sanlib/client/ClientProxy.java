/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.sanandrew.mods.sanlib.CommonProxy;
import de.sanandrew.mods.sanlib.SLibConfig;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.Lexicon;
import de.sanandrew.mods.sanlib.client.lexicon.LexiconRegistry;
import de.sanandrew.mods.sanlib.lib.util.AnnotatedInstanceUtil;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy
        extends CommonProxy
{
    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void init() {
//        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        if( SLibConfig.Client.setSplashTitle ) {
            setTitleSplash();
        }

        for( ILexicon lexicon : AnnotatedInstanceUtil.getInstances(Lexicon.class, ILexicon.class, SanLib.LOG) ) {
            LexiconRegistry.INSTANCE.registerLexicon(lexicon);
        }

//        ClientCommandHandler.instance.registerCommand(new CommandSanLibClient());
    }

    @Override
    public void loadComplete() {
        LexiconRegistry.INSTANCE.initialize();
    }

    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    private static void setTitleSplash() {
        String splashText = null;

        try( IResource iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASH_TEXTS) ) {
            List<String> list = Lists.newArrayList();
            try( BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)) ) {
                String s;

                while( (s = bufferedreader.readLine()) != null ) {
                    s = s.trim();

                    if( !s.isEmpty() ) {
                        list.add(s);
                    }
                }

                int tries = 0;
                if( !list.isEmpty() ) {
                    do {
                        splashText = list.get(MiscUtils.RNG.randomInt(list.size()));
                    } while( splashText.hashCode() == 125780783 || ++tries <= 20 );
                }
            }
        } catch( IOException ignored ) { }

        if( !Strings.isNullOrEmpty(splashText) && splashText.hashCode() != 125780783 ) {
            GLFW.glfwSetWindowTitle(Minecraft.getInstance().mainWindow.getHandle(), "Minecraft 1.13.2" + " - " + splashText);
        }
    }
}
