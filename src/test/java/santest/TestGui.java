////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TestGui
        extends JsonGuiScreen
{
    protected TestGui() {
        super(new StringTextComponent("test gui"));
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(new ResourceLocation("santest", "guis/test.json"));
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, ex);

            return null;
        }
    }
}
