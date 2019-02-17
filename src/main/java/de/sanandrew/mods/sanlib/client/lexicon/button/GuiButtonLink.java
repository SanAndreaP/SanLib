/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon.button;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.api.client.lexicon.IGuiButtonLink;
import de.sanandrew.mods.sanlib.client.lexicon.GuiLexicon;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import org.apache.logging.log4j.Level;

import java.net.URI;
import java.net.URISyntaxException;

public class GuiButtonLink
        extends GuiButton
        implements IGuiButtonLink
{
    private final String link;
    private final boolean trusted;
    public final FontRenderer fontRenderer;
    private final GuiLexicon gui;

    private int linkColorActive;
    private int linkColorVisited;

    public GuiButtonLink(GuiLexicon gui, int id, int x, int y, String text, String link, FontRenderer fontRenderer) {
        this(gui, id, x, y, text, link, fontRenderer, false);
    }

    public GuiButtonLink(GuiLexicon gui, int id, int x, int y, String text, String link, FontRenderer fontRenderer, boolean trusted) {
        super(id, x, y, fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT, text);
        this.link = link;
        this.fontRenderer = fontRenderer;
        this.trusted = trusted;
        this.gui = gui;

        this.linkColorActive = gui.lexicon.getLinkColor();
        this.linkColorVisited = gui.lexicon.getLinkVisitedColor();
    }

    @Override
    public void render(int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            fontRenderer.drawString(this.displayString, this.x, this.y, this.enabled ? this.linkColorActive : this.linkColorVisited);
        }
    }

    @Override
    public void onClick(double p_194829_1_, double p_194829_3_) {

        try {
            this.gui.clickedURI = new URI(this.link);
            if( this.gui.mc.gameSettings.chatLinksPrompt ) {
                this.gui.mc.displayGuiScreen(new GuiConfirmOpenLink(this.gui, this.gui.clickedURI.toString(), 0, this.isTrusted()));
            } else {
                GuiLexicon.openLink(this.gui.clickedURI);
            }
        } catch( URISyntaxException e ) {
            SanLib.LOG.log(Level.ERROR, "Cannot create invalid URI", e);
            this.gui.clickedURI = null;
        }
    }

    @Override
    public GuiButton get() {
        return this;
    }

    @Override
    public String getLink() {
        return this.link;
    }

    @Override
    public boolean isTrusted() {
        return this.trusted;
    }
}
