////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.SanLib;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "java:S1104"})
public final class GuiElementInst
{
    public String      type;
    public String      id;
    public int[]       pos = new int[2];
    public JsonObject  data;
    public IGuiElement element;
    public String[]    alignment;

    private boolean visible = true;

    public static final GuiElementInst EMPTY = new GuiElementInst(new EmptyGuiElement());

    public GuiElementInst() { }

    public GuiElementInst(String type, @Nonnull JsonObject data) {
        this.type = type;
        this.data = data;
    }

    public GuiElementInst(int[] pos, String type, @Nonnull JsonObject data) {
        this.pos = pos;
        this.type = type;
        this.data = data;
    }

    public GuiElementInst(String id, String type, @Nonnull JsonObject data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public GuiElementInst(int[] pos, String id, String type, @Nonnull JsonObject data) {
        this.id = id;
        this.pos = pos;
        this.type = type;
        this.data = data;
    }

    public GuiElementInst(@Nonnull IGuiElement element) {
        this.element = element;
    }

    public GuiElementInst(int[] pos, @Nonnull IGuiElement element) {
        this.pos = pos;
        this.element = element;
    }

    public GuiElementInst(String id, @Nonnull IGuiElement element) {
        this.id = id;
        this.element = element;
    }

    public GuiElementInst(int[] pos, String id, @Nonnull IGuiElement element) {
        this.id = id;
        this.pos = pos;
        this.element = element;
    }

    public IGuiElement get() {
        return this.get(IGuiElement.class);
    }

    public <T extends IGuiElement> T get(Class<T> returnCls) {
        return returnCls.cast(this.element);
    }

    public boolean isVisible() {
        return this.visible && this.element != null && this.element.isVisible();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public GuiElementInst initialize(IGui gui) {
        if( this.element == null ) {
            if( this.data == null ) {
                this.data = new JsonObject();
            }

            BiFunction<IGui, JsonObject, IGuiElement> cnst = GuiDefinition.TYPES.get(new ResourceLocation(this.type));
            if( cnst != null ) {
                this.element = cnst.apply(gui, this.data);
            } else {
                SanLib.LOG.log(Level.ERROR, "A GUI Definition uses an unknown type {} for an element!", this.type);
                this.element = new EmptyGuiElement();
            }
        }

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
