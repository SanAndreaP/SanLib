package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui2.Spacing;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StackPanel
        extends ElementParent
{
    public static final ResourceLocation ID = new ResourceLocation("stack_panel");

    protected final List<GuiElement> orderedChildren = new ArrayList<>();

    protected Orientation orientation;
    protected Spacing padding;

    @Override
    public GuiElement putElement(String id, GuiElement child) {
        GuiElement prev = super.putElement(id, child);

        if( prev != null ) {
            this.orderedChildren.remove(prev);
        }
        this.orderedChildren.add(child);
        this.calcSize();

        return prev;
    }

    @Override
    public GuiElement removeElement(String id) {
        GuiElement prev = super.removeElement(id);

        if( prev != null ) {
            this.orderedChildren.remove(prev);
        }

        this.calcSize();

        return prev;
    }

    @Override
    public void clear() {
        super.clear();
        this.calcSize();
    }

    @Override
    public void tick(IGui gui) {
        super.tick(gui);
        this.calcSize();
    }

    @Override
    protected Collection<GuiElement> getVisibleChildren() {
        return this.orderedChildren;
    }

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) {
        int cx = x + this.padding.getLeft();
        int cy = y + this.padding.getTop();
        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                child.render(gui, matrixStack, cx + child.getPosX(), cy + child.getPosY(), mouseX, mouseY, partialTicks);

                cx += this.orientation == Orientation.HORIZONTAL ? child.getPosX() + child.getWidth() : 0;
                cy += this.orientation == Orientation.VERTICAL ? child.getPosY() + child.getHeight() : 0;
            }
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.orientation = Orientation.fromString(JsonUtils.getStringVal(data.get("orientation"), Orientation.VERTICAL.toString()));
        this.padding = Spacing.loadSpacing(data.get("padding"));

        this.loadChildren(guiDef, data.get("children"));
    }

    protected void calcSize() {
        int pw = this.padding.getWidth();
        int ph = this.padding.getHeight();

        this.width = pw;
        this.height = ph;

        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                if( this.orientation == Orientation.HORIZONTAL ) {
                    this.width += child.getPosX() + child.getWidth();
                    this.height = Math.max(this.height, child.getHeight() + ph);
                } else {
                    this.width = Math.max(this.width, child.getWidth() + pw);
                    this.height += child.getPosY() + child.getHeight();
                }
            }
        }
    }

    public static class Builder<T extends StackPanel>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {
            super(elem);
        }
        
        public Builder<T> withPadding(Spacing padding) {
            this.elem.padding = padding;
            return this;
        }

        public Builder<T> withOrientation(Orientation orientation) {
            this.elem.orientation = orientation;
            return this;
        }

        public Builder<T> withChild(String id, GuiElement child) {
            this.elem.putElement(id, child);
            return this;
        }

        public Builder<T> withChild(GuiElement child) {
            this.elem.putElement(child);
            return this;
        }

        public static Builder<StackPanel> create() {
            return new Builder<>(new StackPanel());
        }
    }
}
