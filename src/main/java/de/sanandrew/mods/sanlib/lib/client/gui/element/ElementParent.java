////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

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
        doWorkVI(e -> e.get().update(gui, e.data));
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        for( GuiElementInst inst : this.getChildren() ) {
            GuiDefinition.renderElement(gui, stack, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, partTicks, inst);
        }
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        return doWorkB(e -> e.mouseScrolled(gui, mouseX, mouseY, mouseScroll));
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return doWorkB(e -> e.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY));
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return doWorkB(e -> e.mouseReleased(gui, mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return doWorkB(e -> e.mouseReleased(gui, mouseX, mouseY, button));
    }

    @Override
    public void onClose(IGui gui) {
        doWorkV(e -> e.onClose(gui));
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return doWorkB(e -> e.charTyped(gui, typedChar, keyCode));
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return doWorkB(e -> e.keyPressed(gui, keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return doWorkB(e -> e.keyReleased(gui, keyCode, scanCode, modifiers));
    }

    protected boolean doWorkB(Function<IGuiElement, Boolean> execElem) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() && execElem.apply(inst.get()) ) {
                return true;
            }
        }

        return false;
    }

    protected void doWorkV(Consumer<IGuiElement> execElem) {
        this.doWorkVI(e -> execElem.accept(e.get()));
    }

    protected void doWorkVI(Consumer<GuiElementInst> execElem) {
        for( GuiElementInst inst : this.getChildren() ) {
            if( inst.isVisible() ) {
                execElem.accept(inst);
            }
        }
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
        return doWorkB(e -> e.forceRenderUpdate(gui));
    }
}
