/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TestGui
        extends GuiScreen
        implements IGui
{
    private int posX;
    private int posY;
    private GuiDefinition guiDef;

    public TestGui() {
        try {
            this.guiDef = GuiDefinition.getNewDefinition(new ResourceLocation("test", "guis/test.json"));
        } catch( IOException e ) {
            SanLib.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if( guiDef == null ) {
            this.mc.displayGuiScreen(null);
            return;
        }

        this.posX = (this.width - this.guiDef.width) / 2;
        this.posY = (this.height - this.guiDef.height) / 2;

        this.guiDef.initGui(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0.0F);
        this.guiDef.drawBackground(this, mouseX - this.posX, mouseY - this.posY, partialTicks);
        this.guiDef.drawForeground(this, mouseX - this.posX, mouseY - this.posY, partialTicks);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDef.handleMouseInput(this);
    }

    @Override
    public GuiScreen get() {
        return this;
    }

    @Override
    public int getScreenPosX() {
        return this.posX;
    }

    @Override
    public int getScreenPosY() {
        return this.posY;
    }
}
