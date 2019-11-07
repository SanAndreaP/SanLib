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

import java.util.function.Supplier;

public final class GuiElementInst
{
    public String type;
    public String id;
    public int[] pos;
    public JsonObject data;
    public IGuiElement element;

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
}
