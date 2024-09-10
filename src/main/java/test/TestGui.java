////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////
package test;

import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.client.gui.JsonGuiScreen;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TestGui
        extends JsonGuiScreen
{
//    private int ticksOpen = 0;

    protected TestGui() {
        super(Component.literal("test gui"));
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(this, ResourceLocation.fromNamespaceAndPath(Constants.ID, "guis/test2.json"));
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, ex);

            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();

//        this.ticksOpen += 1;

//        if( this.ticksOpen >= this.getProcessDuration() * 4 ) {
//            this.ticksOpen = 0;
//        }
    }

    @Override
    protected void initGd() {
//        double pd = this.getProcessDuration();

//        this.guiDefinition.getElementById("prog_ltr").get(ProgressBar.class).setPercentFunc(p -> this.ticksOpen / pd);
//        this.guiDefinition.getElementById("prog_rtl").get(ProgressBar.class).setPercentFunc(p -> this.ticksOpen / pd - 1.0D);
//        this.guiDefinition.getElementById("prog_ttb").get(ProgressBar.class).setPercentFunc(p -> this.ticksOpen / pd - 2.0D);
//        this.guiDefinition.getElementById("prog_btt").get(ProgressBar.class).setPercentFunc(p -> this.ticksOpen / pd - 3.0D);

//        this.guiDefinition.getElementById("test-scroll-area").get(ScrollArea.class).setEnabled(false);
    }

//    private int getProcessDuration() {
//        return 160;
//    }
}
