package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.Spacing;
import dev.sanandrea.mods.sanlib.lib.client.util.GuiUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ScrollPanel
        extends ElementParent
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("scroll_panel");

    public static final String SCROLL_BUTTON_ID = "__scroll_panel__scroll_button__";

    protected final List<GuiElement> orderedChildren = new ArrayList<>();

    protected int        areaWidth;
    protected int        areaHeight;
    protected GuiElement scrollBtn      = Empty.INSTANCE;
    protected int        scrollHeight;
    protected double     minScrollDelta = 0.0F;
    protected boolean    rasterized;
    @Nonnull
    protected Spacing    padding = Spacing.NONE;

    protected int     scrollButtonOffsetY = 0;
    protected boolean isMouseDownScrolling;

    protected double                   scroll          = 0.0D;
    protected Map<GuiElement, Integer> visibleChildren = Collections.emptyMap();

    protected final Map<GuiElement, Integer> childOffsetsY = new HashMap<>();

    public ScrollPanel(String id) {
        super(id);
    }

    @Override
    public GuiElement putElement(String id, GuiElement child) {
        GuiElement prev = super.putElement(id, child);

        if( prev != null ) {
            this.orderedChildren.remove(prev);
        }
        this.orderedChildren.add(child);
        child.addGeometryChangeListener(this::updateGeometry);

        this.updateGeometry();

        return prev;
    }

    @Override
    public GuiElement removeElement(String id) {
        GuiElement prev = super.removeElement(id);

        if( prev != null ) {
            prev.removeGeometryChangeListener(this::updateGeometry);
            this.orderedChildren.remove(prev);
        }

        this.updateGeometry();

        return prev;
    }

    @Override
    public void clear() {
        this.children.forEach((id, e) -> e.removeGeometryChangeListener(this::updateGeometry));

        super.clear();

        this.updateGeometry();
    }

    protected void updateGeometry() {
        this.childOffsetsY.clear();

        int cy = this.padding.getTop();
        int pw = this.padding.getWidth();

        this.width = pw;
        this.height = 0;

        for( GuiElement child : this.orderedChildren ) {
            if( child.isVisible() ) {
                int cw = child.getWidth();
                int ch = child.getHeight();

                this.width = Math.max(this.width, cw + pw);

                cy += child.getPosY();

                this.childOffsetsY.put(child, cy);

                cy += ch;
            }
        }

        this.height = cy + this.padding.getBottom();

        this.scrollBtn.setEnabled(this.canScroll());

        this.updateVisibleChildren();
    }

    protected List<Map.Entry<GuiElement, Integer>> getSortedChildOffsetsY() {
        return this.childOffsetsY.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
    }

    protected void updateVisibleChildren() {
        final int                      minY       = Mth.floor((this.height - this.areaHeight) * this.scroll);
        final int                      maxY       = minY + this.areaHeight;
        final Map<GuiElement, Integer> newVisible = new LinkedHashMap<>();

        this.getSortedChildOffsetsY().forEach(e -> {
            int        cy    = e.getValue();
            GuiElement child = e.getKey();

            if( child.isVisible() ) {
                int ch = child.getHeight();

                int scrollY = cy - minY;
                if( maxY > cy && minY < cy + ch ) {
                    newVisible.put(child, scrollY);
                }
            }
        });

        this.visibleChildren = newVisible;
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        GuiUtils.enableScissor(gui.getPosX() + x, gui.getPosY() + y, this.areaWidth, this.areaHeight);
        x += this.padding.getLeft();
        for( Map.Entry<GuiElement, Integer> childEntry : this.visibleChildren.entrySet() ) {
            GuiElement child = childEntry.getKey();
            if( child.isVisible() ) {
                int cx = x + child.getPosX();
                int cy = y + childEntry.getValue();

                if( this.isChildInFullView(child) ) {
                    child.updateHovering(gui, cx, cy, mouseX, mouseY);
                } else {
                    child.setHovering(false);
                }
                child.render(gui, graphics, cx, cy, mouseX, mouseY, partialTicks);
            }
        }
        RenderSystem.disableScissor();

        int scrollBtnX = this.scrollBtn.getPosX();
        int scrollBtnY = this.scrollBtn.getPosY() + this.scrollButtonOffsetY;

        this.scrollBtn.updateHovering(gui, scrollBtnX, scrollBtnY, mouseX, mouseY);
        this.scrollBtn.render(gui, graphics, scrollBtnX, scrollBtnY, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderDebug(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks, int level) {
        super.renderDebug(gui, graphics, x, y, mouseX, mouseY, partialTicks, level);

        x += this.padding.getLeft();
        for( Map.Entry<GuiElement, Integer> childEntry : this.visibleChildren.entrySet() ) {
            GuiElement child = childEntry.getKey();
            if( child.isVisible() ) {
                int cx = x + child.getPosX();
                int cy = y + childEntry.getValue();
                child.renderDebug(gui, graphics, cx, cy, mouseX, mouseY, partialTicks, level+1);
            }
        }
        int scrollBtnX = this.scrollBtn.getPosX();
        int scrollBtnY = this.scrollBtn.getPosY() + this.scrollButtonOffsetY;

        this.scrollBtn.renderDebug(gui, graphics, scrollBtnX, scrollBtnY, mouseX, mouseY, partialTicks, level+1);
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.padding = Spacing.loadSpacing(data.get("padding"), false);
        this.areaWidth = JsonUtils.getIntVal(data.get("areaWidth"));
        this.areaHeight = JsonUtils.getIntVal(data.get("areaHeight"));
        this.scrollHeight = JsonUtils.getIntVal(data.get("scrollHeight"), this.areaHeight);

        this.minScrollDelta = JsonUtils.getDoubleVal(data.get("minScrollDelta"), 0.0D);
        this.rasterized = JsonUtils.getBoolVal(data.get("rasterized"), false);

        JsonElement sbData = data.get("scrollButton");
        if( sbData != null && sbData.isJsonObject() ) {
            this.scrollBtn = guiDef.loadElement(SCROLL_BUTTON_ID, sbData.getAsJsonObject());
        }

        this.loadChildren(guiDef, data.get("children"));
    }

    @Override
    protected Collection<GuiElement> getVisibleChildren() {
        return this.visibleChildren.keySet();
    }

    @Override
    public int getWidth() {
        return this.areaWidth;
    }

    @Override
    public int getHeight() {
        return this.areaHeight;
    }

    public boolean canScroll() {
        int count = this.children.size();
        return count > 0 && this.visibleChildren.size() < count;
    }

    public void scroll(double percentage) {
        if( this.canScroll() ) {
            if( rasterized ) {
                GuiElement currChild = this.getElementAtPos(this.scroll);
                GuiElement nextChild = this.getElementAtPos(percentage);

                if( nextChild != null && !Objects.equals(currChild, nextChild) ) {
                    this.scrollTo(nextChild);
                }
            } else {
                this.setScroll(percentage);
            }
        }
    }

    public void scrollTo(GuiElement element) {
        this.setScroll(this.getScrollPos(element));
    }

    protected double getScrollPos(GuiElement element) {
        if( element == null ) {
            return 0.0D;
        }

        int elemOffset = this.childOffsetsY.get(element);

        return Math.max(0.0D, Math.min(1.0D, 1.0D / (this.height - this.areaHeight) * elemOffset));
    }

    protected GuiElement getElementAtPos(double scroll) {
        int px = Mth.floor((this.height - this.areaHeight) * scroll);
        for( Map.Entry<GuiElement, Integer> e : this.getSortedChildOffsetsY() ) {
            if( e.getValue() >= px ) {
                return e.getKey();
            }
        }

        return null;
    }

    protected void setScroll(double newScroll) {
        if( newScroll < 0.0D ) {
            newScroll = 0.0D;
        } else if( newScroll > 1.0D ) {
            newScroll = 1.0D;
        }

        double prevScroll = this.scroll;
        this.scroll = newScroll;

        if( !this.isMouseDownScrolling ) {
            this.scrollButtonOffsetY = Mth.floor((this.scrollHeight - this.scrollBtn.getHeight()) * this.scroll);
        }

        if( !MiscUtils.between(prevScroll - Double.MIN_VALUE, this.scroll, prevScroll + Double.MIN_VALUE) ) {
            this.updateVisibleChildren();
        }
    }

    protected void addScroll(double newScroll) {
        this.setScroll(this.scroll + MiscUtils.limitMin(newScroll, this.minScrollDelta, true));
    }

    protected void rasterScroll(int indexShift) {
        Double newScroll = null;

        if( indexShift != 0 ) {
            GuiElement currChild = this.visibleChildren.keySet().stream().findFirst().orElse(null);
            int        currIdx   = this.orderedChildren.indexOf(currChild);

            if( indexShift > 0 && currChild != null && currIdx >= 0 && currIdx < this.orderedChildren.size() - 1 ) {
                newScroll = this.getScrollPos(this.orderedChildren.get(currIdx + 1));
            } else if( indexShift < 0 && currChild != null && currIdx > 0 ) {
                newScroll = this.getScrollPos(this.orderedChildren.get(currIdx - 1));
            }
        }

        if( newScroll != null ) {
            this.setScroll(newScroll);
        }
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) {
        double dir = -1.0D * scroll / Math.abs(scroll);
        if( this.rasterized ) {
            this.rasterScroll(Math.round((float) dir));
        } else {
            this.addScroll(dir / this.visibleChildren.size());
        }
        return super.mouseScrolled(gui, mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean capture = false;

        if( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
            if( this.canScroll() && (this.isMouseDownScrolling || this.scrollBtn.isHovering()) ) {
                this.isMouseDownScrolling = true;
                capture = true;

                double scrollBtnHeight = this.scrollBtn.getHeight();
                double scrollHeightMax = this.scrollHeight - scrollBtnHeight;
                double mouseYBtn       = Math.max(0.0D, mouseY - gui.getPosY() - this.scrollBtn.getPosY() - scrollBtnHeight / 2.0D);
                mouseYBtn = Math.min(mouseYBtn, scrollHeightMax);

                this.scrollButtonOffsetY = Mth.floor(mouseYBtn);
                this.scroll(mouseYBtn / scrollHeightMax);
            } else {
                this.isMouseDownScrolling = false;
            }
        }

        return capture || super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        if( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
            this.isMouseDownScrolling = false;
        }

        return super.mouseReleased(gui, mouseX, mouseY, button);
    }

    @Override
    protected boolean evaluateChildren(Predicate<GuiElement> execElem) {
        return super.evaluateChildren(elem -> this.isChildInFullView(elem) && execElem.test(elem));
    }

    protected boolean isChildInFullView(GuiElement elem) {
        int offY = this.visibleChildren.getOrDefault(elem, -1);
        return offY >= 0 && offY + elem.getHeight() <= this.areaHeight;
    }

    public static class Builder<T extends ScrollPanel>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {
            super(elem);
        }

        public Builder<T> withPadding(Spacing padding) {
            this.elem.padding = padding;

            return this;
        }

        public Builder<T> withAreaSize(int width, int height) {
            this.elem.areaWidth = width;
            this.elem.areaHeight = height;

            return this;
        }

        public Builder<T> withScrollHeight(int height) {
            this.elem.scrollHeight = height;

            return this;
        }

        public Builder<T> withMinScrollDelta(double delta) {
            this.elem.minScrollDelta = delta;

            return this;
        }

        public Builder<T> withRasterized() {
            this.elem.rasterized = true;

            return this;
        }

        public Builder<T> withoutRasterized() {
            this.elem.rasterized = false;

            return this;
        }

        public Builder<T> withScrollButton(GuiElement button) {
            this.elem.scrollBtn = button;

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

        public static Builder<ScrollPanel> createScrollPanel() {
            return createScrollPanel(UUID.randomUUID().toString());
        }

        public static Builder<ScrollPanel> createScrollPanel(String id) {
            return new Builder<>(new ScrollPanel(id));
        }
    }
}
