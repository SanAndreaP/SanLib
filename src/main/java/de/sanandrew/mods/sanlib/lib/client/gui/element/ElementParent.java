////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({ "WeakerAccess", "unused" })
public abstract class ElementParent<K>
        implements IGuiElement
{
    protected GuiElementInst[]            children = new GuiElementInst[0];
    protected Map<K, GuiElementInst> namedChildren = new ConcurrentHashMap<>();

    public abstract void buildChildren(IGui gui, JsonObject data, Map<K, GuiElementInst> listToBuild);

    public GuiElementInst getChild(K id) {
        return this.namedChildren.get(id);
    }

    public GuiElementInst[] getChildren() {
        return this.children;
    }

    public void rebuildChildren(IGui gui, JsonObject data, boolean bakeData) {
        this.namedChildren.clear();

        Map<K, GuiElementInst> children = new LinkedHashMap<>();
        this.buildChildren(gui, data, children);

        this.children = children.values().stream().filter(Objects::nonNull).toArray(GuiElementInst[]::new);
        this.namedChildren.putAll(children);

        if( bakeData ) {
            for (GuiElementInst cinst : this.children) {
                cinst.get().bakeData(gui, cinst.data, cinst);
            }
        }
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.rebuildChildren(gui, data, true);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                inst.get().update(gui, inst.data);
            }
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        for( GuiElementInst inst : this.getChildren() ) {
            GuiDefinition.renderElement(gui, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, partTicks, inst);
        }
    }

    @Override
    public void handleMouseInput(IGui gui) throws IOException {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                inst.get().handleMouseInput(gui);
            }
        }
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() && inst.get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(IGui gui, int mouseX, int mouseY, int state) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                inst.get().mouseReleased(gui, mouseX, mouseY, state);
            }
        }
    }

    @Override
    public void mouseClickMove(IGui gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                inst.get().mouseClickMove(gui, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
            }
        }
    }

    @Override
    public void guiClosed(IGui gui) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                inst.get().guiClosed(gui);
            }
        }
    }

    @Override
    public boolean keyTyped(IGui gui, char typedChar, int keyCode) throws IOException {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() && inst.get().keyTyped(gui, typedChar, keyCode) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getWidth() {
        int w = 0;
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                w = Math.max(w, inst.pos[0] + inst.get().getWidth());
            }
        }
        return w;
    }

    @Override
    public int getHeight() {
        int h = 0;
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                h = Math.max(h, inst.pos[1] + inst.get().getHeight());
            }
        }
        return h;
    }

    @Override
    public boolean forceRenderUpdate(IGui gui) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() && inst.get().forceRenderUpdate(gui) ) {
                return true;
            }
        }

        return false;
    }
}
