package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Map;

public class ScrollAreaGuiElement
        implements IGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("scroll_area");

    protected BakedData data;

    protected float scroll;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.areaSize = JsonUtils.getIntArray(data.get("areaSize"), Range.is(2));
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
                this.data.elements.put(com.google.common.collect.Range.closedOpen(e.pos[1], e.pos[1] + elemInst.getHeight()), e);
            });

            this.scroll = 0.0F;
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        int cntAll = this.data.elementsView.size();
        int maxHeight = this.data.elements.span().upperEndpoint();
        int minY = Math.max(0, MathHelper.floor((maxHeight - this.data.areaSize[1]) * this.scroll));
        int maxY = MathHelper.ceil((maxHeight - this.data.areaSize[1]) * this.scroll) + this.data.areaSize[1];

        Map<com.google.common.collect.Range<Integer>, GuiElementInst> renderedElements = this.data.getSubRange(minY, maxY);
        int cntSub = renderedElements.size();

        GuiElementInst btn = cntAll > cntSub ? this.data.scrollBtnActive : this.data.scrollBtnDeactive;
        int scrollY = btn.pos[1] + Math.round(this.scroll * (this.data.scrollHeight - ((TextureGuiElement) btn.get()).data.size[1]));

        btn.get().render(gui, partTicks, btn.pos[0], scrollY, mouseX, mouseY, btn.data);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.data.areaSize[0], this.data.areaSize[1]);

        renderedElements.forEach((k, e) -> {
            e.get().render(gui, partTicks, x + e.pos[0], y + e.pos[1] - minY, mouseX - x, mouseY - y - minY, e.data);
        });

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    //TODO: respect rasterized setting
    @Override
    public void handleMouseInput(IGui gui) {
        int dWheelDir = Mouse.getEventDWheel();
        if( dWheelDir < 0 ) {
            this.scroll += Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            if( this.scroll > 1.0F ) {
                this.scroll = 1.0F;
            }
        } else if( dWheelDir > 0 ) {
            this.scroll -= Math.min(1.0F / this.data.elementsView.size(), this.data.maxScrollDelta);
            if( this.scroll < 0.0F ) {
                this.scroll = 0.0F;
            }
        }
    }

    protected GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return JsonUtils.GSON.fromJson(elementData.get("elements"), GuiElementInst[].class);
    }

    @Override
    public int getHeight() {
        return this.data.areaSize[1];
    }

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
        public final Map<com.google.common.collect.Range<Integer>, GuiElementInst> elementsView = this.elements.asMapOfRanges();

        public Map<com.google.common.collect.Range<Integer>, GuiElementInst> getSubRange(Integer lower, Integer upper) {
            return this.elements.subRangeMap(com.google.common.collect.Range.closedOpen(lower, upper)).asMapOfRanges();
        }
    }
}
