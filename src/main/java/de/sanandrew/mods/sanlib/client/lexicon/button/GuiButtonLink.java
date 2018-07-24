/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon.button;

import de.sanandrew.mods.sanlib.client.lexicon.GuiLexicon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonLink
        extends GuiButton
{
    public final String link;
    public final FontRenderer fontRenderer;
    public final boolean trusted;

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

        this.linkColorActive = gui.lexicon.getLinkColor();
        this.linkColorVisited = gui.lexicon.getLinkVisitedColor();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            fontRenderer.drawString(this.displayString, this.x, this.y, this.enabled ? this.linkColorActive : this.linkColorVisited, false);
        }
    }
}
