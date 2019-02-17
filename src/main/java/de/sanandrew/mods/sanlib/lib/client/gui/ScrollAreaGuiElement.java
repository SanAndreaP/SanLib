package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ScrollAreaGuiElement
        implements IGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("scroll_area");

    protected BakedData data;
    protected float scroll;

    private boolean prevLmbDown;

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

            if( this.data.scrollBtnActive.get() instanceof TextureGuiElement && this.data.scrollBtnDeactive.get() instanceof TextureGuiElement ) {
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
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        boolean isLmbDown = gui.get().mc.mouseHelper.isLeftDown();
        int cntAll = this.data.elementsView.size();
        BakedData.ScrollData sData = this.data.getScrollData(this.scroll);

        Map<Range<Integer>, GuiElementInst> renderedElements = this.data.getSubRange(sData.minY, sData.maxY, false);
        int cntSub = renderedElements.size();

        GuiElementInst btn = cntAll > cntSub ? this.data.scrollBtnActive : this.data.scrollBtnDeactive;
        TextureGuiElement btnElem = (TextureGuiElement) btn.get();


        if( cntAll > cntSub && isLmbDown ) {
            if( this.prevLmbDown
                || (mouseX >= this.data.scrollBtnActive.pos[0] && mouseX < this.data.scrollBtnActive.pos[0] + btnElem.data.size[0]
                    && mouseY >= this.data.scrollBtnActive.pos[1] && mouseY < this.data.scrollBtnActive.pos[1] + this.data.scrollHeight) )
            {
                int scrollAmt = mouseY - this.data.scrollBtnActive.pos[1] - btnElem.data.size[1] / 2;
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

        renderedElements.forEach((k, e) -> e.get().render(gui, partTicks, x + e.pos[0], y + e.pos[1] - sData.minY, mouseX - x, mouseY - y - sData.minY, e.data));

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean onMouseScroll(IGui gui, double scroll) {
        if( scroll < 0 ) {
            if( this.data.rasterized ) {
                this.scroll = this.getRasterScroll(true);
            } else {
                this.scroll += Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            }

            if( this.scroll > 1.0F ) {
                this.scroll = 1.0F;
            }

            return true;
        } else if( scroll > 0 ) {
            if( this.data.rasterized ) {
                this.scroll = this.getRasterScroll(false);
            } else {
                this.scroll -= Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            }

            if( this.scroll < 0.0F ) {
                this.scroll = 0.0F;
            }

            return true;
        }

        return false;
    }

    private float getRasterScroll(boolean next) {
        BakedData.ScrollData sData = this.data.getScrollData(this.scroll);
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

    protected GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return JsonUtils.GSON.fromJson(elementData.get("elements"), GuiElementInst[].class);
    }

    @Override
    public int getHeight() {
        return this.data.areaSize[1];
    }

    @SuppressWarnings("UnstableApiUsage")
    private static final class BakedData
    {
        private int[] areaSize;
        private GuiElementInst scrollBtnActive;
        private GuiElementInst scrollBtnDeactive;
        private int scrollHeight;
        private float maxScrollDelta;
        private boolean rasterized;

        @SuppressWarnings("UnstableApiUsage")
        public final RangeMap<Integer, GuiElementInst> elements = TreeRangeMap.create();
        public final Map<Range<Integer>, GuiElementInst> elementsView = this.elements.asMapOfRanges();

        private ScrollData getScrollData(float scroll) {
            ScrollData data = new ScrollData();

            data.totalHeight = this.elements.span().upperEndpoint();
            data.minY = Math.max(0, MathHelper.floor((data.totalHeight - this.areaSize[1]) * scroll));
            data.maxY = MathHelper.ceil((data.totalHeight - this.areaSize[1]) * scroll) + this.areaSize[1];

            return data;
        }

        public Map<Range<Integer>, GuiElementInst> getSubRange(Integer lower, Integer upper, boolean desc) {
            RangeMap<Integer, GuiElementInst> sub = this.elements.subRangeMap(Range.closedOpen(lower, upper));

            return desc ? sub.asDescendingMapOfRanges() : sub.asMapOfRanges();
        }

        private static final class ScrollData
        {
            private int totalHeight;
            private int minY;
            private int maxY;
        }
    }
}