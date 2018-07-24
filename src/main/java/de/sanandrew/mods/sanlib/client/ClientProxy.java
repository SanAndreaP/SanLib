/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client;

import de.sanandrew.mods.sanlib.CommonProxy;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.Lexicon;
import de.sanandrew.mods.sanlib.client.lexicon.LexiconRegistry;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
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
    public void reloadModels() {
        ModelJsonLoader.REGISTERED_JSON_LOADERS.forEach(loader -> loader.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager()));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        LexiconRegistry.INSTANCE.initialize();
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
}
