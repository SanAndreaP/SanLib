////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.sanandrew.mods.sanlib.CommonProxy;
import de.sanandrew.mods.sanlib.SLibConfig;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.Lexicon;
import de.sanandrew.mods.sanlib.client.lexicon.LexiconRegistry;
import de.sanandrew.mods.sanlib.client.command.CommandSanLibClient;
import de.sanandrew.mods.sanlib.client.model.EmissiveModelLoader;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.Display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy
        extends CommonProxy
{
    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        if( SLibConfig.Client.enableEmissiveTextures && EmissiveModelLoader.isLightMapEnabled() ) {
            ModelLoaderRegistry.registerLoader(new EmissiveModelLoader());
        }

        if( SLibConfig.Client.setSplashTitle ) {
            setTitleSplash();
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        LexiconRegistry.INSTANCE.initialize();
        ClientCommandHandler.instance.registerCommand(new CommandSanLibClient());
    }

    @Override
    public void loadModLexica(ASMDataTable dataTable) {
        String annotationClassName = Lexicon.class.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = dataTable.getAll(annotationClassName);
        for( ASMDataTable.ASMData asmData : asmDatas ) {
            try {
                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends ILexicon> asmInstanceClass = asmClass.asSubclass(ILexicon.class);
                ILexicon instance = asmInstanceClass.getConstructor().newInstance();
                LexiconRegistry.INSTANCE.registerLexicon(instance);
            } catch( ClassNotFoundException | IllegalAccessException | ExceptionInInitializerError | InstantiationException | NoSuchMethodException | InvocationTargetException e ) {
                SanLib.LOG.log(Level.ERROR, "Failed to load: {}", asmData.getClassName(), e);
            }
        }
    }

    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    private static void setTitleSplash() {
        String splashText = null;

        try( IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS) ) {
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
            Display.setTitle(Display.getTitle() + " - " + splashText);
        }
    }
}
