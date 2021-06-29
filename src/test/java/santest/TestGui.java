////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TestGui
        extends Screen
        implements IGui
{
    private int posX;
    private int posY;
    private GuiDefinition guiDef;

    protected TestGui() {
        super(new StringTextComponent("test gui"));

        try {
            this.guiDef = GuiDefinition.getNewDefinition(new ResourceLocation("santest", "guis/test.json"));
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, ex);
        }
    }

    @Override
    protected void init() {
        super.init();

        if( this.guiDef == null ) { this.onClose(); return; }

        this.posX = (this.width - this.guiDef.width) / 2;
        this.posY = (this.height - this.guiDef.height) / 2;

        this.guiDef.initGui(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.guiDef.update(this);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        stack.pushPose();
        stack.translate(this.posX, this.posY, 0.0D);
        this.guiDef.drawBackground(this, stack, mouseX, mouseY, partialTicks);
        this.guiDef.drawForeground(this, stack, mouseX, mouseY, partialTicks);
        stack.popPose();

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.guiDef.mouseClicked(this, mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.guiDef.mouseDragged(this, mouseX, mouseY, button, dragX, dragY) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.guiDef.mouseScrolled(this, mouseX, mouseY, delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.guiDef.mouseReleased(this, mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.guiDef.keyPressed(this, keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.guiDef.keyReleased(this, keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.guiDef.onClose(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public Screen get() {
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
}
