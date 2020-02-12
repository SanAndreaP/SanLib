////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.sanlib.client.lexicon.button.GuiButtonEntry;
import de.sanandrew.mods.sanlib.client.lexicon.button.GuiButtonEntryDivider;
import de.sanandrew.mods.sanlib.client.lexicon.button.GuiButtonGroup;
import de.sanandrew.mods.sanlib.client.lexicon.button.GuiButtonLink;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiLexicon
        extends GuiScreen
        implements GuiYesNoCallback
{
    int guiLeft;
    int guiTop;
    int entryX;
    int entryY;
    int entryHeight;

    public final ILexicon lexicon;

    private ILexiconGroup group;
    private ILexiconEntry entry;
    @Nonnull
    private ILexiconPageRender render;
    @Nonnull
    private NBTTagCompound changedPageNbt = new NBTTagCompound();

    float scroll;
    int dHeight;
    private boolean isScrolling;
    private URI clickedURI;
    private boolean updateGUI;

    final List<GuiButton> entryButtons;
    private final ILexiconGuiHelper renderHelper;

    private final Deque<History> navHistory = new ArrayDeque<>();
    private final Deque<History> navFuture = new ArrayDeque<>();

    Runnable drawFrameLast;

    GuiLexicon(ILexicon lexicon) {
        this.entryButtons = new ArrayList<>();
        this.renderHelper = new LexiconGuiHelper(this);
        this.render = EmptyRenderer.INSTANCE;
        this.lexicon = lexicon;
    }

    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        int guiSizeX = this.lexicon.getGuiSizeX();
        int guiSizeY = this.lexicon.getGuiSizeY();

        ILexiconInst inst = LexiconRegistry.INSTANCE.getInstance(this.lexicon.getModId());

        this.guiLeft = (this.width - guiSizeX) / 2;
        this.guiTop = (this.height - guiSizeY) / 2;

        this.entryX = this.guiLeft + this.lexicon.getEntryPosX();
        this.entryY = this.guiTop + this.lexicon.getEntryPosY();
        this.entryHeight = this.lexicon.getEntryHeight();

        this.buttonList.clear();
        this.entryButtons.clear();

        int navOffsetY = this.lexicon.getNavButtonOffsetY();
        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + 30, this.guiTop + navOffsetY, 0, !navHistory.isEmpty()));
        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + (guiSizeX - 10) / 2, this.guiTop + navOffsetY, 1, true));
        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + guiSizeX - 48, this.guiTop + navOffsetY, 2, !navFuture.isEmpty()));

        if( group == null ) {
            int posX = 0;
            int posY = 0;
            for( ILexiconGroup group : inst.getGroups() ) {
                this.buttonList.add(new GuiButtonGroup(this, this.buttonList.size(), this.entryX + 2 + posX, this.entryY + 2 + posY, group, this::groupBtnMouseOver));
                if( (posX += 34) > this.lexicon.getEntryWidth() - 12 ) {
                    posX = 0;
                    posY += 34;
                }
            }
            this.render = EmptyRenderer.INSTANCE;
        } else if( entry == null ) {
            int posY = 0;
            int btnX = 2;
            group.sortEntries();
            for( ILexiconEntry entry : group.getEntries() ) {
                this.entryButtons.add(new GuiButtonEntry(this, this.entryButtons.size(), btnX, 19 + posY, entry, this.renderHelper.getFontRenderer()));
                posY += 14;
                if( entry.divideAfter() ) {
                    this.entryButtons.add(new GuiButtonEntryDivider(this, this.entryButtons.size(), btnX, 19 + posY));
                    posY += 5;
                }
            }
            this.render = EmptyRenderer.INSTANCE;
        } else {
            this.render = inst.getPageRender(entry.getPageRenderId());
            if( this.render != null ) {
                int shift = this.render.shiftEntryPosY();
                this.entryY += shift;
                this.entryHeight -= shift;
                this.render.initPage(entry, this.renderHelper, this.buttonList, this.entryButtons);
                this.render.loadPageState(this.changedPageNbt);
            } else {
                SanLib.LOG.log(Level.ERROR, String.format("cannot render lexicon page entry %s as render ID %s is not registered!", entry.getId(), entry.getPageRenderId()));
                this.render = EmptyRenderer.INSTANCE;
            }
        }

        this.updateScreen();
    }

    @Override
    public void updateScreen() {
        if( this.updateGUI ) {
            this.updateGUI = false;
            this.initGui();
        }

        if( entry != null ) {
            this.dHeight = this.render.getEntryHeight(entry, this.renderHelper) - this.entryHeight;
        } else if( group != null ) {
            this.dHeight = this.entryButtons.size() * 14 + 20 - this.entryHeight;
        } else {
            this.dHeight = 0;
        }

        for( GuiButton btn : this.entryButtons ) {
            btn.enabled = btn.y - Math.round(scroll * this.dHeight) > 0 && btn.y - Math.round(scroll * this.dHeight) + btn.height <= this.entryHeight;
        }

        this.render.updateScreen(this.renderHelper);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        boolean mouseDown = Mouse.isButtonDown(0);
        int entryWidth = this.lexicon.getEntryWidth();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(this.lexicon.getBackgroundTexture());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.lexicon.getGuiSizeX(), this.lexicon.getGuiSizeY());

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.entryX + entryWidth, this.entryY, 0.0F);
        drawRect(0, 0, 6, this.entryHeight, 0x30000000);
        if( this.dHeight > 0 ) {
            drawRect(0, Math.round((this.entryHeight - 16) * scroll), 6, Math.round((this.entryHeight - 16) * scroll + 16), 0x800000FF);
        }
        GlStateManager.popMatrix();

        if( entry != null ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
            this.render.renderPageOverlay(entry, this.renderHelper, mouseX - this.entryX, mouseY - entryY, partTicks);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.renderHelper.doEntryScissoring();
        GlStateManager.translate(this.entryX, this.entryY, 0.0F);

        GlStateManager.translate(0.0F, Math.round(-scroll * this.dHeight), 0.0F);

        if( entry != null ) {
            this.render.renderPageEntry(entry, this.renderHelper, mouseX - this.entryX, mouseY - entryY, Math.round(scroll * this.dHeight), partTicks);
        } else if( group != null ) {
            String s = TextFormatting.ITALIC + LangUtils.translate(LangUtils.LEXICON_GROUP_NAME.get(this.lexicon.getModId(), group.getId()));
            this.renderHelper.getFontRenderer().drawString(s, 2, 2, 0xFF33AA33, false);
            Gui.drawRect(2, 12, entryWidth - 2, 13, 0xFF33AA33);
        }

        for( GuiButton btn : this.entryButtons ) {
            btn.drawButton(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(scroll * this.dHeight), partTicks);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        if( !mouseDown && this.isScrolling ) {
            this.isScrolling = false;
        } else if( mouseDown && !this.isScrolling ) {
            if( mouseY >= this.entryY && mouseY < this.entryY + this.entryHeight ) {
                if( mouseX >= this.entryX + entryWidth && mouseX < this.entryX + entryWidth + 6 ) {
                    this.isScrolling = this.dHeight > 0;
                }
            }
        }

        if( this.isScrolling ) {
            int mouseDelta = Math.min(this.entryHeight - 16, Math.max(0, mouseY - (this.entryY + 8)));
            scroll = mouseDelta / (this.entryHeight - 16.0F);
        }

        super.drawScreen(mouseX, mouseY, partTicks);

        if( this.drawFrameLast != null ) {
            this.drawFrameLast.run();
            this.drawFrameLast = null;
        }
    }

    void changePage(ILexiconGroup group, ILexiconEntry entry, float scroll, boolean doHistory) {
        if( doHistory ) {
            History h = new History(this.group, this.entry, this.scroll);
            this.render.savePageState(h.nbt);
            navHistory.offer(h);
            navFuture.clear();
        }
        this.group = group;
        this.entry = entry;
        this.scroll = scroll;
        this.updateGUI = true;
    }

    private void groupBtnMouseOver(ILexiconGroup group, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(mouseX + 12, mouseY - 12, 32.0F);

        String title = LangUtils.translate(LangUtils.LEXICON_GROUP_NAME.get(this.lexicon.getModId(), group.getId()));
        int bkgColor = 0xF0101010;
        int lightBg = 0x50A0A0A0;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;
        int textWidth = this.renderHelper.getFontRenderer().getStringWidth(title);
        int tHeight = 8;

        this.drawGradientRect(-3,            -4,          textWidth + 3, -3,          bkgColor, bkgColor);
        this.drawGradientRect(-3,            tHeight + 3, textWidth + 3, tHeight + 4, bkgColor, bkgColor);
        this.drawGradientRect(-3,            -3,          textWidth + 3, tHeight + 3, bkgColor, bkgColor);
        this.drawGradientRect(-4,            -3,          -3,            tHeight + 3, bkgColor, bkgColor);
        this.drawGradientRect(textWidth + 3, -3,          textWidth + 4, tHeight + 3, bkgColor, bkgColor);

        this.drawGradientRect(-3,            -3 + 1,      -3 + 1,        tHeight + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(textWidth + 2, -3 + 1,      textWidth + 3, tHeight + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(-3,            -3,          textWidth + 3, -3 + 1,          lightBg, lightBg);
        this.drawGradientRect(-3,            tHeight + 2, textWidth + 3, tHeight + 3,     darkBg,  darkBg);

        this.renderHelper.getFontRenderer().drawString(title, 0, 0, 0xFFFFFFFF, true);
        GlStateManager.popMatrix();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if( this.dHeight > 0 ) {
            int dwheel = Mouse.getEventDWheel() / 120;
            if( dwheel != 0 ) {
                scroll = Math.min(1.0F, Math.max(0.0F, (scroll * this.dHeight - dwheel * 16.0F) / this.dHeight));
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if( !this.render.actionPerformed(button, this.renderHelper) ) {
            if( button instanceof GuiButtonNav ) {
                History h;
                switch( ((GuiButtonNav) button).buttonType ) {
                    case 0:
                        h = navHistory.pollLast();
                        if( h != null ) {
                            History fh = new History(group, entry, scroll);
                            this.render.savePageState(fh.nbt);
                            navFuture.offer(fh);
                            this.changedPageNbt = h.nbt;
                            this.changePage(h.group, h.entry, h.scroll, false);
                        }
                        break;
                    case 1:
                        this.changedPageNbt = new NBTTagCompound();
                        this.changePage(null, null, 0.0F, true);
                        break;
                    case 2:
                        h = navFuture.pollLast();
                        if( h != null ) {
                            History ph = new History(group, entry, scroll);
                            this.render.savePageState(ph.nbt);
                            navHistory.offer(ph);
                            this.changedPageNbt = h.nbt;
                            this.changePage(h.group, h.entry, h.scroll, false);
                        }
                        break;
                }
            } else if( button instanceof GuiButtonGroup ) {
                GuiButtonGroup grpButton = (GuiButtonGroup) button;
                List<ILexiconEntry> entries = grpButton.group.getEntries();
                this.changePage(grpButton.group, entries.size() == 1 ? entries.get(0) : null, 0.0F, true);
            } else if( button instanceof GuiButtonEntry ) {
                this.changePage(group, ((GuiButtonEntry) button).getEntry(), 0.0F, true);
            } else if( button instanceof GuiButtonLink ) {
                try {
                    GuiButtonLink btnLink = (GuiButtonLink) button;
                    this.clickedURI = new URI(btnLink.getLink());
                    if( this.mc.gameSettings.chatLinksPrompt ) {
                        this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.clickedURI.toString(), 0, btnLink.isTrusted()));
                    } else {
                        openLink(this.clickedURI);
                    }
                } catch( URISyntaxException e ) {
                    SanLib.LOG.log(Level.ERROR, "Cannot create invalid URI", e);
                    this.clickedURI = null;
                }
            } else {
                super.actionPerformed(button);
            }
        }
    }

    float getZLevel() {
        return this.zLevel;
    }

    @Override
    public void confirmClicked(boolean isYes, int id) {
        if( id == 0 ) {
            if( isYes ) {
                openLink(this.clickedURI);
            }

            this.clickedURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseBtn);

        if( mouseBtn == 0 ) {
            for( GuiButton btn : this.entryButtons ) {
                if( btn.mousePressed(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(scroll * this.dHeight)) ) {
                    btn.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(btn);
                }
            }
        }

        this.render.mouseClicked(mouseX, mouseY, mouseBtn, this.renderHelper);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.render.keyTyped(typedChar, keyCode, this.renderHelper);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static void openLink(URI uri) {
        try {
            java.awt.Desktop.getDesktop().browse(uri);
        } catch( Throwable throwable ) {
            SanLib.LOG.log(Level.ERROR, "Couldn\'t open link", throwable);
        }
    }

    private static final class History
    {
        private final ILexiconGroup group;
        private final ILexiconEntry entry;
        private final float scroll;
        private final NBTTagCompound nbt = new NBTTagCompound();

        private History(ILexiconGroup group, ILexiconEntry entry, float scroll) {
            this.group = group;
            this.entry = entry;
            this.scroll = scroll;
        }
    }

    private final class GuiButtonNav
            extends GuiButton
    {
        private final int buttonType;

        private GuiButtonNav(int id, int x, int y, int type, boolean visible) {
            super(id, x, y, (type == 1 ? 10 : 18), 10, "");
            this.buttonType = type;
            this.visible = visible;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
            if( this.visible ) {
                boolean over = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiLexicon.this.lexicon.getBackgroundTexture());
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                switch( this.buttonType ) {
                    case 0: this.drawTexturedModalRect(this.x, this.y, 220 + (over ? 18 : 0), 52, 18, 10); break;
                    case 1: this.drawTexturedModalRect(this.x, this.y, 236 + (over ? 10 : 0), 62, 10, 10); break;
                    case 2: this.drawTexturedModalRect(this.x, this.y, 220 + (over ? 18 : 0), 42, 18, 10); break;
                }
                GlStateManager.disableBlend();
            }
        }
    }

    private static final class EmptyRenderer
            implements ILexiconPageRender
    {
        private static final ILexiconPageRender INSTANCE = new EmptyRenderer();

        @Override public String getId() { return ""; }
        @Override public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) { }
        @Override public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) { }
        @Override public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) { return 0; }
    }

    List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}
