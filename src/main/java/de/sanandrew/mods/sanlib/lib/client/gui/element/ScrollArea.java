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
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    protected int countAll;
    protected int countSub;

    protected boolean prevLmbDown;

    protected int posX;
    protected int posY;
    protected boolean isVisible = true;

    public ScrollArea(int[] areaSize, int scrollHeight, boolean rasterized, float maxScrollDelta, GuiElementInst scrollButton) {
        this.areaSize = areaSize;
        this.scrollHeight = scrollHeight;
        this.rasterized = rasterized;
        this.maxScrollDelta = maxScrollDelta;

        this.scrollBtn = scrollButton;

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
    public void update() {
        this.sData = this.getScrollData(this.scroll, this.rasterized);

        this.children = this.getSubRange(this.sData.minY, this.sData.maxY, false).values().toArray(new GuiElementInst[0]);

        this.countAll = this.namedChildren.size();
        this.countSub = this.children.length;

        this.scrollBtn.get(ScrollButton.class).disabled = this.countAll <= this.countSub;
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
        super.render(gui, stack, partTicks, x, y - this.sData.minY, mouseX, mouseY, inst);
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        if( this.countAll <= this.countSub ) {
            return super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
        }

        if( mouseScroll < 0 ) {
            if( this.rasterized ) {
                this.scroll = this.getRasterScroll(true);
            } else {
                this.scroll += Math.min(1.0F / this.countAll, this.maxScrollDelta);
            }
            this.clipScroll();
        } else if( mouseScroll > 0 ) {
            if( this.rasterized ) {
                this.scroll = this.getRasterScroll(false);
            } else {
                this.scroll -= Math.min(1.0F / this.countAll, this.maxScrollDelta);
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
            Texture        btnElem = this.scrollBtn.get(ScrollButton.class);

            if( this.countAll > this.countSub
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

        GuiElementInst firstVal = first.getValue();
        Map.Entry<Range<Integer>, GuiElementInst> second;
        if( next ) {
            sRange = this.getSubRange(firstVal.pos[1] + firstVal.get().getHeight(), Integer.MAX_VALUE, false);
        } else {
            sRange = this.getSubRange(0, firstVal.pos[1], true);
        }

        second = sRange.entrySet().stream().findFirst().orElse(null);
        if( second == null ) {
            return next ? 1.0F : 0.0F;
        }

        int scrollArea = rsData.totalHeight - this.areaSize[1];

        return 1.0F / scrollArea * second.getValue().pos[1];
    }

    @Override
    public int getWidth() {
        return this.areaSize[0];
    }

    @Override
    public int getHeight() {
        return this.areaSize[1];
    }

    public ScrollData getScrollData(double scroll, boolean rasterized) {
        ScrollData data = new ScrollData();

        data.totalHeight = this.countAll > 0 ? this.elements.span().upperEndpoint() : 0;
        data.minY = Math.max(0, MathHelper.floor((data.totalHeight - this.areaSize[1]) * scroll));
        data.maxY = MathHelper.ceil((data.totalHeight - this.areaSize[1]) * scroll) + this.areaSize[1];

        if( rasterized ) {
            getSubRange(data.minY, data.maxY, false).entrySet().stream().findFirst().ifPresent(f -> {
                int heightAdj = f.getValue().get().getHeight() / 2;
                getSubRange(data.minY + heightAdj, data.maxY + heightAdj, false).entrySet().stream().findFirst().ifPresent(e -> {
                    data.minY = e.getValue().pos[1];
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

    @SuppressWarnings("java:S1104")
    public static final class ScrollData
    {
        public int totalHeight;
        public int minY;
        public int maxY;
    }

    public static class Builder
    {
        protected int[]   areaSize;
        protected int     scrollHeight;
        protected boolean rasterized;
        protected float   maxScrollDelta;
        protected int[]   scrollbarPos;

        protected GuiElementInst[] elements;

        ScrollButton.Builder scrollButton;

        public Builder(int areaWidth, int areaHeight) {
            this(new int[] { areaWidth, areaHeight });
        }

        public Builder(int[] areaSize) {
            this.areaSize = areaSize;
            this.scrollHeight = areaSize[1];
            this.rasterized = false;
            this.maxScrollDelta = 1.0F;
        }

        public Builder scrollHeight(int height)                     { this.scrollHeight = height;    return this; }
        public Builder rasterized(boolean rasterized)               { this.rasterized = rasterized;  return this; }
        public Builder maxScrollDelta(float delta)                  { this.maxScrollDelta = delta;   return this; }
        public Builder scrollbarPos(int[] pos)                      { this.scrollbarPos = pos;       return this; }
        public Builder scrollButton(ScrollButton.Builder sbBuilder) { this.scrollButton = sbBuilder; return this; }
        public Builder elements(GuiElementInst... elements)         { this.elements = elements ;     return this; }

        public Builder scrollbarPos(int x, int y) { return this.scrollbarPos(new int[] { x, y }); }

        public void sanitize(IGui gui) {
            if( scrollbarPos == null ) {
                scrollbarPos = new int[] { this.areaSize[0], 0 };
            }
        }

        public ScrollArea get(IGui gui) {
            this.sanitize(gui);

            ScrollArea sa = new ScrollArea(this.areaSize, this.scrollHeight, this.rasterized, this.maxScrollDelta,
                                           new GuiElementInst(this.scrollbarPos, this.scrollButton.get(gui)).initialize(gui));

            if( this.elements != null && this.elements.length > 0 ) {
                Arrays.stream(this.elements).forEach(e -> sa.add(e.initialize(gui), true));
                sa.update();
            }

            return sa;
        }

        protected static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("areaSize"), org.apache.commons.lang3.Range.is(2)))
                                   .scrollButton(ScrollButton.Builder.buildFromJson(gui, data.getAsJsonObject("scrollButton")));

            JsonUtils.fetchInt(data.get("scrollbarHeight"), b::scrollHeight);
            JsonUtils.fetchIntArray(data.get("scrollbarPos"), b::scrollbarPos);
            JsonUtils.fetchBool(data.get("rasterized"),     b::rasterized);
            JsonUtils.fetchFloat(data.get("maxScrollDelta"), b::maxScrollDelta);

            if( data.has("elements") ) {
                JsonElement je = data.get("elements");
                if( je.isJsonArray() ) {
                    b.elements(JsonUtils.GSON.fromJson(je, GuiElementInst[].class));
                } else {
                    throw new JsonParseException("elements property is not an array!");
                }
            }

            return b;
        }

        public static ScrollArea fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
