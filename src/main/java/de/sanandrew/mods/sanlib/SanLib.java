////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.client.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(Constants.ID)
public class SanLib
{
    public static final Logger LOG = LogManager.getLogger(Constants.ID);

    public static  SanLib      instance;

    public SanLib() {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void setup(FMLCommonSetupEvent event) {
        //TODO: figure out configs
//        SLibConfig.initConfiguration(event);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        //TODO: figure out what AE2 does
//        if( SLibConfig.Client.enableEmissiveTextures && EmissiveModelLoader.isLightMapEnabled() ) {
//            ModelLoaderRegistry.registerLoader(new EmissiveModelLoader());
//        }

        if( SLibConfig.Client.setSplashTitle ) {
            setTitleSplash(event.getMinecraftSupplier().get());
        }
    }

    //TODO: FMLFingerprintViolationEvent is deprecated???
//    @Mod.EventHandler
//    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
//        LOG.log(Level.ERROR, "Invalid Fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author");
//    }

    //TODO: not feasible anymore without mixins...
    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    @OnlyIn(Dist.CLIENT)
    private static void setTitleSplash(Minecraft mc) {
//        String splashText = null;
//
//        try( IResource iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASH_TEXTS) ) {
//            List<String> list = Lists.newArrayList();
//            try( BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)) ) {
//                String s;
//
//                while( (s = bufferedreader.readLine()) != null ) {
//                    s = s.trim();
//
//                    if( !s.isEmpty() ) {
//                        list.add(s);
//                    }
//                }
//
//                int tries = 0;
//                if( !list.isEmpty() ) {
//                    do {
//                        splashText = list.get(MiscUtils.RNG.randomInt(list.size()));
//                    } while( splashText.hashCode() == 125780783 || ++tries <= 20 );
//                }
//            }
//        } catch( IOException ignored ) { }
//
//        if( !Strings.isNullOrEmpty(splashText) && splashText.hashCode() != 125780783 ) {
//            Minecraft mc = (Minecraft) getMcInst.get();
//            MainWindow window = mc.getMainWindow();
//            window.setWindowTitle(( + " - " + splashText);
//        }
    }
}
