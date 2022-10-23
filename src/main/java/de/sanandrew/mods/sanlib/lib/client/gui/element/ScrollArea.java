////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "UnstableApiUsage"})
public class ScrollArea
        extends ElementParent<Range<Integer>>
{
    public static final ResourceLocation ID = new ResourceLocation("scroll_area");

    protected int[] areaSize;
    protected GuiElementInst scrollBtn;
    protected int scrollHeight;
    protected float maxScrollDelta;
    protected boolean rasterized;

    protected final RangeMap<Integer, GuiElementInst> elements = TreeRangeMap.create();
    protected final List<GuiElementInst> prebuiltElements = new ArrayList<>();

    protected double scroll;
    protected ScrollData sData = new ScrollData();

    protected boolean prevLmbDown;

    protected int posX;
    protected int posY;
    protected boolean isVisible = true;

    public ScrollArea(int[] areaSize, int scrollHeight, boolean rasterized, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui) {
        this.areaSize = areaSize;
        this.scrollHeight = scrollHeight;
        this.rasterized = rasterized;
        this.maxScrollDelta = maxScrollDelta;

        this.scrollBtn = new GuiElementInst(scrollbarPos, scrollButton).initialize(gui);

        this.namedChildren = this.elements.asMapOfRanges();
    }

    @Override
    public void put(Range<Integer> id, @Nonnull GuiElementInst child) {
        this.elements.put(id, child);
    }

    @Override
    public GuiElementInst remove(Range<Integer> id) {
        if( this.elements.asMapOfRanges().containsKey(id) ) {
            this.elements.remove(id);
        }

        return null;
    }

    @Override
    public GuiElementInst[] getAll() {
        return this.namedChildren.values().toArray(new GuiElementInst[0]);
    }

    public void add(@Nonnull GuiElementInst child) {
        this.add(child, false);
    }

    public void add(@Nonnull GuiElementInst child, boolean silent) {
        this.prebuiltElements.add(child);
    }

    @Override
    public void clear() {
        this.elements.clear();
        this.prebuiltElements.clear();
        this.scroll = 0.0D;
    }

    public void update(IGui gui) {
        if( !this.prebuiltElements.isEmpty() ) {
            this.prebuiltElements.forEach(e -> {
                e.get().setup(gui, e);
                this.elements.put(Range.closedOpen(e.pos[1], e.pos[1] + e.get().getHeight()), e);
            });
            this.prebuiltElements.clear();
        }

        this.update();
    }

    @Override
    protected void update(boolean isOnSetup) {
        this.sData = this.getScrollData(this.scroll, this.rasterized);

        RangeMap<Integer, GuiElementInst> sub = this.elements.subRangeMap(Range.closed(this.sData.minY, this.sData.maxY));
        this.children = sub.asMapOfRanges().values().toArray(new GuiElementInst[0]);

        Range<Integer> lastRange = this.children.length > 0
                                   ? this.elements.asDescendingMapOfRanges().entrySet().stream().findFirst().map(Map.Entry::getKey).orElse(null)
                                   : null;

        this.scrollBtn.get(ScrollButton.class).disabled = this.getTotalCount() <= this.children.length
                                                          && MiscUtils.apply(lastRange, Range::upperEndpoint, 0) <= this.sData.maxY
                                                          && MiscUtils.apply(lastRange, Range::lowerEndpoint, 0) >= this.sData.minY;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.scrollBtn.get().setup(gui, this.scrollBtn);
        this.scroll = 0.0F;

        this.update(gui);
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.scrollBtn.get().tick(gui, this.scrollBtn);
        super.tick(gui, inst);

        if( !this.prebuiltElements.isEmpty() ) {
            this.update(gui);
        }
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.posX = x;
        this.posY = y;

        int scrollY = this.scrollBtn.pos[1] + (int) Math.round(this.scroll * (this.scrollHeight - this.scrollBtn.get(ScrollButton.class).size[1]));

        this.scrollBtn.get().render(gui, stack, partTicks, this.scrollBtn.pos[0], scrollY, mouseX, mouseY, this.scrollBtn);

        GuiUtils.enableScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.areaSize[0], this.areaSize[1]);
        this.renderElements(gui, stack, partTicks, x, y - this.sData.minY, mouseX, mouseY, inst);
        RenderSystem.disableScissor();
    }

    protected void renderElements(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        if( this.scrollBtn.get(ScrollButton.class).disabled
            || (!IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.areaSize[0], this.areaSize[1])
                && !IGuiElement.isHovering(gui, this.scrollBtn.pos[0], this.scrollBtn.pos[1], mouseX, mouseY, this.scrollBtn.get().getWidth(), this.scrollHeight)) )
        {
            return super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
        }

        if( mouseScroll < 0 ) {
            if( this.rasterized ) {
                this.scroll = this.getRasterScroll(true);
            } else {
                int cnt = this.getTotalCount();
                this.scroll += Math.min(cnt > 0 ? 1.0F / cnt : 0.0F, this.maxScrollDelta);
            }
            this.clipScroll();
        } else if( mouseScroll > 0 ) {
            if( this.rasterized ) {
                this.scroll = this.getRasterScroll(false);
            } else {
                int cnt = this.getTotalCount();
                this.scroll -= Math.min(cnt > 0 ? 1.0F / cnt : 0.0F, this.maxScrollDelta);
            }
            this.clipScroll();
        }

        return super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        if( IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.areaSize[0], this.areaSize[1]) ) {
            return super.mouseClicked(gui, mouseX, mouseY, button);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
            ScrollButton btnElem = this.scrollBtn.get(ScrollButton.class);

            if( !btnElem.disabled
                && (this.prevLmbDown || IGuiElement.isHovering(gui, this.scrollBtn.pos[0], this.scrollBtn.pos[1], mouseX, mouseY, btnElem.size[0], this.scrollHeight)) )
            {
                double scrollAmt = mouseY - gui.getScreenPosY() - this.scrollBtn.pos[1] - btnElem.size[1] / 2.0D;
                this.scroll = Math.max(0.0F, Math.min(1.0F, 1.0F / (this.scrollHeight - btnElem.size[1]) * scrollAmt));
                this.clipScroll();

                this.prevLmbDown = true;
            } else {
                this.prevLmbDown = false;
            }
        }

        if( IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.areaSize[0], this.areaSize[1]) ) {
            return super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        if( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
            this.prevLmbDown = false;
        }

        if( IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.areaSize[0], this.areaSize[1]) ) {
            return super.mouseReleased(gui, mouseX, mouseY, button);
        }

        return false;
    }

    public void clipScroll() {
        if( this.scroll < 0.0F ) {
            this.scroll = 0.0F;
        }
        if( this.scroll > 1.0F ) {
            this.scroll = 1.0F;
        }

        this.update();
    }

    public float getRasterScroll(boolean next) {
        ScrollData rsData = this.getScrollData(this.scroll, this.rasterized);
        Map<Range<Integer>, GuiElementInst> sRange = this.getSubRange(rsData.minY, rsData.maxY, false);
        Map.Entry<Range<Integer>, GuiElementInst> first = sRange.entrySet().stream().findFirst().orElse(null);

        if( first == null ) {
            return 0.0F;
        }

        Range<Integer> firstRange = first.getKey();
        GuiElementInst firstVal = first.getValue();
        Map.Entry<Range<Integer>, GuiElementInst> second;
        if( next ) {
            sRange = this.getSubRange(firstRange.upperEndpoint(), Integer.MAX_VALUE, false);
        } else {
            sRange = this.getSubRange(0, firstRange.lowerEndpoint(), true);
        }

        second = sRange.entrySet().stream().findFirst().orElse(null);
        if( second == null ) {
            return next ? 1.0F : 0.0F;
        }

        int scrollArea = rsData.totalHeight - this.areaSize[1];

        return 1.0F / scrollArea * second.getKey().lowerEndpoint();
    }

    @Override
    public int getWidth() {
        return this.areaSize[0];
    }

    @Override
    public int getHeight() {
        return this.areaSize[1];
    }

    public int getScrollY() {
        return this.sData.minY;
    }

    @SuppressWarnings("java:S3518")
    public void scrollTo(GuiElementInst element) {
        this.namedChildren.entrySet().stream().filter(e -> e.getValue() == element)
                          .map(Map.Entry::getKey).findFirst().ifPresent(elemRange ->  {
                              this.scroll = elemRange.lowerEndpoint() / Math.max(this.getTotalHeight(), 1.0F);
                              this.clipScroll();
                          });
    }

    private int getTotalHeight() {
        return this.getTotalCount() > 0 ? this.elements.span().upperEndpoint() : 0;
    }

    public ScrollData getScrollData(double scroll, boolean rasterized) {
        ScrollData data = new ScrollData();

        data.totalHeight = this.getTotalHeight();
        data.minY = Math.max(0, MathHelper.floor((data.totalHeight - this.areaSize[1]) * scroll));
        data.maxY = MathHelper.ceil((data.totalHeight - this.areaSize[1]) * scroll) + this.areaSize[1];

        if( rasterized ) {
            Map<Range<Integer>, GuiElementInst> sr = getSubRange(data.minY, data.maxY, false);
            sr.entrySet().stream().findFirst().ifPresent(f -> {
                int heightAdj = f.getValue().get().getHeight() / 2;
                getSubRange(data.minY + heightAdj, data.maxY + heightAdj, false).entrySet().stream().findFirst().ifPresent(e -> {
                    data.minY = MiscUtils.apply(this.elements.getEntry(e.getKey().lowerEndpoint()), oe -> oe.getKey().lowerEndpoint(), data.minY);
                    data.maxY = data.minY + this.areaSize[1];
                });
            });
        }

        return data;
    }

    public Map<Range<Integer>, GuiElementInst> getSubRange(Integer lower, Integer upper, boolean desc) {
        RangeMap<Integer, GuiElementInst> sub = this.elements.subRangeMap(Range.closedOpen(lower, upper));

        return desc ? sub.asDescendingMapOfRanges() : sub.asMapOfRanges();
    }

    public int getTotalCount() {
        return this.elements.asMapOfRanges().size();
    }

    @SuppressWarnings("java:S1104")
    public static final class ScrollData
    {
        public int totalHeight;
        public int minY;
        public int maxY;
    }

    public static class Builder
            implements IBuilder<ScrollArea>
    {
        public final int[] areaSize;

        protected int              scrollHeight;
        protected boolean          rasterized;
        protected float            maxScrollDelta;
        protected int[]            scrollbarPos;
        protected GuiElementInst[] elements;
        protected ScrollButton     scrollButton;

        public Builder(int[] areaSize) {
            this.areaSize = areaSize;
            this.scrollHeight = areaSize[1];
            this.rasterized = false;
            this.maxScrollDelta = 1.0F;
        }

        public Builder scrollHeight(int height)             { this.scrollHeight = height;    return this; }
        public Builder rasterized(boolean rasterized)       { this.rasterized = rasterized;  return this; }
        public Builder maxScrollDelta(float delta)          { this.maxScrollDelta = delta;   return this; }
        public Builder scrollbarPos(int[] pos)              { this.scrollbarPos = pos;       return this; }
        public Builder scrollButton(ScrollButton sbBuilder) { this.scrollButton = sbBuilder; return this; }
        public Builder elements(GuiElementInst... elements) { this.elements = elements;      return this; }

        public Builder scrollbarPos(int x, int y) { return this.scrollbarPos(new int[] { x, y }); }

        public void sanitize(IGui gui) {
            if( scrollbarPos == null ) {
                scrollbarPos = new int[] { this.areaSize[0], 0 };
            }
        }

        public ScrollArea get(IGui gui) {
            this.sanitize(gui);

            ScrollArea sa = new ScrollArea(this.areaSize, this.scrollHeight, this.rasterized, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, gui);

            if( this.elements != null && this.elements.length > 0 ) {
                Arrays.stream(this.elements).forEach(e -> sa.add(e.initialize(gui), true));
                sa.update();
            }

            return sa;
        }

        @Nonnull
        protected GuiElementInst[] loadElements(IGui gui, JsonElement je) {
            if( je != null ) {
                if( je.isJsonArray() ) {
                    return JsonUtils.GSON.fromJson(je, GuiElementInst[].class);
                } else {
                    throw new JsonParseException("elements property is not an array!");
                }
            }

            return new GuiElementInst[0];
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadElements);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<Builder, BiFunction<IGui, JsonElement, GuiElementInst[]>> loadElementsFunc) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("areaSize"), org.apache.commons.lang3.Range.is(2)))
                                   .scrollButton(ScrollButton.Builder.buildFromJson(gui, data.getAsJsonObject("scrollButton")).get(gui));

            JsonUtils.fetchInt(data.get("scrollbarHeight"), b::scrollHeight);
            JsonUtils.fetchIntArray(data.get("scrollbarPos"), b::scrollbarPos);
            JsonUtils.fetchBool(data.get("rasterized"),     b::rasterized);
            JsonUtils.fetchFloat(data.get("maxScrollDelta"), b::maxScrollDelta);

            b.elements(MiscUtils.apply(loadElementsFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data.get("elements")))));

            return b;
        }

        public static ScrollArea fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
