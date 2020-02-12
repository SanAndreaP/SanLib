////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

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
            this.guiDef = GuiDefinition.getNewDefinition(new ResourceLocation("santest", "guis/test.json"));
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
    public void updateScreen() {
        this.guiDef.update(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0.0F);
        this.guiDef.drawBackground(this, mouseX, mouseY, partialTicks);
        this.guiDef.drawForeground(this, mouseX, mouseY, partialTicks);
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
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.posX;
    }

    @Override
    public int getScreenPosY() {
        return this.posY;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.guiDef.mouseReleased(this, mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.guiDef.mouseClickMove(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.guiDef.keyTyped(this, typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.guiDef.guiClosed(this);
    }
}
