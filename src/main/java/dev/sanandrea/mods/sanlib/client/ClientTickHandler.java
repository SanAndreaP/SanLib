/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.client;

import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.SanLibConfig;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import joptsimple.internal.Strings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.ID)
public class ClientTickHandler
{
    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");

    private static int     ticksInGame;
    private static String  splashText    = null;
    private static Boolean doSplashTitle = null;

    private ClientTickHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == TickEvent.Phase.END ) {
            Screen gui = Minecraft.getInstance().screen;
            if( gui == null || !gui.isPauseScreen() ) {
                ticksInGame++;
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("java:S2259")
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if( doSplashTitle == null ) {
            doSplashTitle = MiscUtils.get(SanLibConfig.Client.setSplashTitle.get(), false);
        }

        if( doSplashTitle && event.phase == TickEvent.Phase.END ) {
            if( splashText == null ) {
                try( IResource iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASH_TEXTS) ) {
                    List<String> list = new ArrayList<>();
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
                } catch( IOException ignored ) {
                    splashText = "";
                }
            }

            if( !Strings.isNullOrEmpty(splashText) && splashText.hashCode() != 125780783 ) {
                MainWindow window = Minecraft.getInstance().getWindow();
                window.setTitle(Minecraft.getInstance().createTitle() + " - " + splashText);
            }
        }
    }

    public static int getTicksInGame() {
        return ticksInGame;
    }
}
