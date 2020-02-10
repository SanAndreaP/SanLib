/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.SanLib;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.util.Locale;
import java.util.function.Supplier;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class GuiElementInst
{
    public String type;
    public String id;
    public int[] pos = new int[2];
    public JsonObject data;
    public IGuiElement element;
    public String[] alignment;
    public boolean     firstRenderUpdate;

    private boolean visible = true;

    public GuiElementInst() { }

    public GuiElementInst(IGuiElement element) {
        this.element = element;
    }

    public GuiElementInst(IGuiElement element, JsonObject data) {
        this.element = element;
        this.data = data;
    }

    public GuiElementInst(int[] pos, IGuiElement element) {
        this.pos = pos;
        this.element = element;
    }

    public GuiElementInst(int[] pos, IGuiElement element, JsonObject data) {
        this.pos = pos;
        this.element = element;
        this.data = data;
    }

    public IGuiElement get() {
        return this.get(IGuiElement.class);
    }

    public <T extends IGuiElement> T get(Class<T> returnCls) {
        if( this.element == null ) {
            Supplier<IGuiElement> cnst = GuiDefinition.TYPES.get(new ResourceLocation(this.type));
            if( cnst != null ) {
                this.element = cnst.get();
            } else {
                SanLib.LOG.log(Level.ERROR, String.format("A GUI Definition uses an unknown type %s for an element!", this.type));
                this.element = new EmptyGuiElement();
            }
        }

        return returnCls.cast(this.element);
    }

    public boolean isVisible() {
        return this.visible && this.element != null && this.element.isVisible();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public GuiElementInst initialize(IGui gui) {
        gui.getDefinition().initElement(this);
        return this;
    }

    public Justify getAlignmentH() {
        if( this.alignment != null && this.alignment.length > 0 ) {
            return Justify.fromString(this.alignment[0]);
        }

        return Justify.LEFT;
    }

    public Justify getAlignmentV() {
        if( this.alignment != null && this.alignment.length > 1 ) {
            return Justify.fromString(this.alignment[1]);
        }

        return Justify.TOP;
    }

    public enum Justify
    {
        TOP,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM,
        JUSTIFY;

        public static Justify fromString(String s) {
            return Justify.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
