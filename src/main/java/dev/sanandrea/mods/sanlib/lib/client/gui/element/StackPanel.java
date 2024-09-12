package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.Spacing;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class StackPanel
        extends ElementParent
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("stack_panel");

    protected final List<GuiElement> orderedChildren = new ArrayList<>();

    protected Orientation orientation;
    @Nonnull
    protected Spacing     padding = Spacing.NONE;

    public StackPanel(String id) {
        super(id);
    }

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
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        int cx = x + this.padding.getLeft();
        int cy = y + this.padding.getTop();
        Boolean overwriteHover = !this.isHovering() ? false : null;

        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                GuiDefinition.OffsetShift shift = GuiDefinition.renderElement(gui, graphics, cx, cy, mouseX, mouseY, partialTicks, child, false, null, overwriteHover);

                cx += shift.x() + (this.orientation == Orientation.HORIZONTAL ? child.getPosX() + child.getWidth() : 0);
                cy += shift.y() + (this.orientation == Orientation.VERTICAL ? child.getPosY() + child.getHeight() : 0);
            }
        }
    }

    @Override
    public void renderDebug(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks, int level) {
        super.renderDebug(gui, graphics, x, y, mouseX, mouseY, partialTicks, level);

        int cx = x + this.padding.getLeft();
        int cy = y + this.padding.getTop();

        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                GuiDefinition.OffsetShift shift = GuiDefinition.renderElement(gui, graphics, cx, cy, mouseX, mouseY, partialTicks, child, true, level + 1, null);

                cx += shift.x() + (this.orientation == Orientation.HORIZONTAL ? child.getPosX() + child.getWidth() : 0);
                cy += shift.y() + (this.orientation == Orientation.VERTICAL ? child.getPosY() + child.getHeight() : 0);
            }
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.orientation = Orientation.fromString(JsonUtils.getStringVal(data.get("orientation"), Orientation.VERTICAL.toString()));
        this.padding = Spacing.loadSpacing(data.get("padding"), false);

        this.loadChildren(guiDef, data.get("children"));
    }

    protected void calcSize() {
        int pw         = this.padding.getWidth();
        int ph         = this.padding.getHeight();
        int prevWidth  = this.width;
        int prevHeight = this.height;

        this.width = pw;
        this.height = ph;

        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                if( this.orientation == Orientation.HORIZONTAL ) {
                    this.width += child.getPosX() + child.getWidth();
                    this.height = Math.max(this.height, child.getHeight() + child.getPosY() + ph);
                } else {
                    this.width = Math.max(this.width, child.getWidth() + child.getPosX() + pw);
                    this.height += child.getPosY() + child.getHeight();
                }
            }
        }

        if( prevWidth != this.width || prevHeight != this.height ) {
            this.runGeometryListeners();
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

        public static Builder<StackPanel> createStackPanel() {
            return createStackPanel(UUID.randomUUID().toString());
        }

        public static Builder<StackPanel> createStackPanel(String id) {
            return new Builder<>(new StackPanel(id));
        }
    }
}
