package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ScrollArea
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("scroll_area");

    public BakedData data;
    public float scroll;
    protected BakedData.ScrollData sData;
    protected int countAll;
    protected int countSub;
    protected Map<Range<Integer>, GuiElementInst> renderedElements;

    public boolean prevLmbDown;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.areaSize = JsonUtils.getIntArray(data.get("areaSize"), org.apache.commons.lang3.Range.is(2));
            this.data.scrollHeight = JsonUtils.getIntVal(data.get("scrollbarHeight"), this.data.areaSize[1]);
            this.data.rasterized = JsonUtils.getBoolVal(data.get("rasterized"), false);
            this.data.maxScrollDelta = JsonUtils.getFloatVal(data.get("maxScrollDelta"), 1.0F);

            this.data.scrollBtnActive = JsonUtils.GSON.fromJson(data.get("scrollButton"), GuiElementInst.class);
            this.data.scrollBtnDeactive = JsonUtils.GSON.fromJson(data.get("scrollButtonDeactive"), GuiElementInst.class);

            if( this.data.scrollBtnActive.get() instanceof Texture && this.data.scrollBtnDeactive.get() instanceof Texture) {
                this.data.scrollBtnActive.get().bakeData(gui, this.data.scrollBtnActive.data);
                this.data.scrollBtnDeactive.get().bakeData(gui, this.data.scrollBtnDeactive.data);
            } else {
                throw new JsonParseException("Scroll button must be of type \"texture\" or a subtype of it!");
            }

            GuiElementInst[] elements = this.getElements(gui, data);
            Arrays.stream(elements).forEach(e -> {
                IGuiElement elemInst = e.get();
                elemInst.bakeData(gui, e.data);
                this.data.elements.put(Range.closedOpen(e.pos[1], e.pos[1] + elemInst.getHeight()), e);
            });

            this.scroll = 0.0F;
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.sData = this.data.getScrollData(this.scroll, this.data.rasterized);
        this.renderedElements = this.data.getSubRange(this.sData.minY, this.sData.maxY, false);
        this.countAll = this.data.elementsView.size();
        this.countSub = this.renderedElements.size();
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.sData == null || this.renderedElements == null ) {
            return;
        }

        boolean isLmbDown = Mouse.isButtonDown(0);

        GuiElementInst btn = this.countAll > this.countSub ? this.data.scrollBtnActive : this.data.scrollBtnDeactive;
        Texture btnElem = (Texture) btn.get();

        int adjMouseX = mouseX - gui.getScreenPosX();
        int adjMouseY = mouseY - gui.getScreenPosY();
        if( this.countAll > this.countSub && isLmbDown ) {
            if( this.prevLmbDown
                    || (adjMouseX >= this.data.scrollBtnActive.pos[0] && adjMouseX < this.data.scrollBtnActive.pos[0] + btnElem.data.size[0]
                    && adjMouseY >= this.data.scrollBtnActive.pos[1] && adjMouseY < this.data.scrollBtnActive.pos[1] + this.data.scrollHeight) )
            {
                int scrollAmt = adjMouseY - this.data.scrollBtnActive.pos[1] - btnElem.data.size[1] / 2;
                this.scroll = Math.max(0.0F, Math.min(1.0F, 1.0F / (this.data.scrollHeight - btnElem.data.size[1]) * scrollAmt));

                this.prevLmbDown = true;
            }
        } else {
            this.prevLmbDown = false;
        }

        int scrollY = btn.pos[1] + Math.round(this.scroll * (this.data.scrollHeight - btnElem.data.size[1]));

        btn.get().render(gui, partTicks, btn.pos[0], scrollY, mouseX, mouseY, btn.data);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.data.areaSize[0], this.data.areaSize[1]);

        this.renderedElements.forEach((k, e) -> e.get().render(gui, partTicks, x + e.pos[0], y + e.pos[1] - this.sData.minY, mouseX, mouseY, e.data));

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void handleMouseInput(IGui gui) {
        if( this.countAll <= this.countSub ) {
            return;
        }

        int dWheelDir = Mouse.getEventDWheel();
        if( dWheelDir < 0 ) {
            if( this.data.rasterized ) {
                this.scroll = this.getRasterScroll(true);
            } else {
                this.scroll += Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            }
            this.clipScroll();
        } else if( dWheelDir > 0 ) {
            if( this.data.rasterized ) {
                this.scroll = this.getRasterScroll(false);
            } else {
                this.scroll -= Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            }
            this.clipScroll();
        }
    }

    public void clipScroll() {
        if( this.scroll < 0.0F ) {
            this.scroll = 0.0F;
        }
        if( this.scroll > 1.0F ) {
            this.scroll = 1.0F;
        }
    }

    public float getRasterScroll(boolean next) {
        BakedData.ScrollData sData = this.data.getScrollData(this.scroll, this.data.rasterized);
        Map<Range<Integer>, GuiElementInst> sRange = this.data.getSubRange(sData.minY, sData.maxY, false);
        Map.Entry<Range<Integer>, GuiElementInst> first = sRange.entrySet().stream().findFirst().orElse(null);

        if( first == null ) {
            return 0.0F;
        }

        GuiElementInst firstVal = first.getValue();
        Map.Entry<Range<Integer>, GuiElementInst> second;
        if( next ) {
            sRange = this.data.getSubRange(firstVal.pos[1] + firstVal.get().getHeight(), Integer.MAX_VALUE, false);
        } else {
            sRange = this.data.getSubRange(0, firstVal.pos[1], true);
        }

        second = sRange.entrySet().stream().findFirst().orElse(null);
        if( second == null ) {
            return 0.0F;
        }

        int scrollArea = sData.totalHeight - this.data.areaSize[1];

        return 1.0F / scrollArea * second.getValue().pos[1];
    }

    public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return JsonUtils.GSON.fromJson(elementData.get("elements"), GuiElementInst[].class);
    }

    @Override
    public int getWidth() {
        return this.data.areaSize[0];
    }

    @Override
    public int getHeight() {
        return this.data.areaSize[1];
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final class BakedData
    {
        public int[] areaSize;
        public GuiElementInst scrollBtnActive;
        public GuiElementInst scrollBtnDeactive;
        public int scrollHeight;
        public float maxScrollDelta;
        public boolean rasterized;

        @SuppressWarnings("UnstableApiUsage")
        public final RangeMap<Integer, GuiElementInst> elements = TreeRangeMap.create();
        public final Map<Range<Integer>, GuiElementInst> elementsView = this.elements.asMapOfRanges();

        public ScrollData getScrollData(float scroll, boolean rasterized) {
            ScrollData data = new ScrollData();

            data.totalHeight = this.elements.span().upperEndpoint();
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

        public static final class ScrollData
        {
            public int totalHeight;
            public int minY;
            public int maxY;
        }
    }
}
