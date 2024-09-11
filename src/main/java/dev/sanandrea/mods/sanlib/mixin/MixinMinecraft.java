package dev.sanandrea.mods.sanlib.mixin;

import dev.sanandrea.mods.sanlib.SanLibConfig;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "ConstantValue", "java:S2696", "java:S3008", "java:S100" })
@Mixin(net.minecraft.client.Minecraft.class)
public class MixinMinecraft
{
    @Unique
    private static final net.minecraft.resources.ResourceLocation SPLASH_TEXTS             = net.minecraft.resources.ResourceLocation.withDefaultNamespace("texts/splashes.txt");
    @Unique
    private static       boolean                                  sanLib$doTitleSplashText = true;
    @Unique
    private static       String                                   sanLib$titleSplashText   = null;

    @Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
    private void addSplashTitle(CallbackInfoReturnable<String> cir) {
        if( !SanLibConfig.setSplashTitle ) {
            return;
        }

        if( sanLib$doTitleSplashText && sanLib$titleSplashText == null ) {
            var res = sanLib$tryGetSplashTexts();
            if( res == null ) {
                return;
            }

            try( BufferedReader reader = res.openAsReader() ) {
                sanLib$readSplashTexts(reader);
            } catch( IOException ignored ) {
                sanLib$doTitleSplashText = false;
                return;
            }
        }

        if( sanLib$doTitleSplashText && !MiscUtils.get(sanLib$titleSplashText, "").isEmpty() ) {
            cir.setReturnValue(cir.getReturnValue() + " - " + sanLib$titleSplashText);
        } else {
            sanLib$doTitleSplashText = false;
        }
    }

    @Unique
    private static void sanLib$readSplashTexts(BufferedReader reader) throws IOException {
        String       s;
        List<String> list = new ArrayList<>();

        while( (s = reader.readLine()) != null ) {
            s = s.trim();

            if( !s.isEmpty() ) {
                list.add(s);
            }
        }

        int tries = 0;
        if( !list.isEmpty() ) {
            do {
                sanLib$titleSplashText = list.get(MiscUtils.RNG.randomInt(list.size()));
            } while( sanLib$titleSplashText.hashCode() == 125780783 || ++tries <= 20 );
        }
    }

    @Unique
    private static net.minecraft.server.packs.resources.Resource sanLib$tryGetSplashTexts() {
        var resMgr = net.minecraft.client.Minecraft.getInstance().getResourceManager();
        if( resMgr == null ) {
            return null;
        }
        var res = resMgr.getResource(SPLASH_TEXTS);

        return res.orElse(null);
    }
}
