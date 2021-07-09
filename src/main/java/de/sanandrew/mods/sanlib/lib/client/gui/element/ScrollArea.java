////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Map;

@SuppressWarnings({"UnstableApiUsage", "WeakerAccess", "unused"})
public class ScrollArea
        extends ElementParent<Object>
{
    public static final ResourceLocation ID = new ResourceLocation("scroll_area");

    public int[] areaSize;
    public GuiElementInst[] scrollBtn = new GuiElementInst[2];
    public int scrollHeight;
    public float maxScrollDelta;
    public boolean rasterized;

    public final RangeMap<Integer, GuiElementInst> elements = TreeRangeMap.create();
    public final Map<Range<Integer>, GuiElementInst> elementsView = this.elements.asMapOfRanges();

    public double scroll;
    protected ScrollData sData = new ScrollData();
    protected int countAll;
    protected int countSub;

    public boolean prevLmbDown;

    protected int posX;
    protected int posY;
    protected boolean isVisible = true;

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<Object, GuiElementInst> listToBuild) {
        this.sData = this.getScrollData(this.scroll, this.rasterized);

        listToBuild.putAll(this.getSubRange(this.sData.minY, this.sData.maxY, false));

        this.countAll = this.elementsView.size();
        this.countSub = listToBuild.size();
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.areaSize = JsonUtils.getIntArray(data.get("areaSize"), org.apache.commons.lang3.Range.is(2));
        this.scrollHeight = JsonUtils.getIntVal(data.get("scrollbarHeight"), this.areaSize[1]);
        this.rasterized = JsonUtils.getBoolVal(data.get("rasterized"), false);
        this.maxScrollDelta = JsonUtils.getFloatVal(data.get("maxScrollDelta"), 1.0F);

        int[] scrollBarPos = JsonUtils.getIntArray(data.get("scrollbarPos"), new int[] {inst.pos[0] + this.areaSize[0], inst.pos[1]}, org.apache.commons.lang3.Range.is(2));
        JsonObject scrollBtnData = MiscUtils.get(data.getAsJsonObject("scrollButton"), JsonObject::new);
        this.scrollBtn[0] = new GuiElementInst(scrollBarPos, new Texture(), scrollBtnData).initialize(gui);
        this.scrollBtn[0].get().bakeData(gui, scrollBtnData, this.scrollBtn[0]);
        scrollBtnData = JsonUtils.deepCopy(scrollBtnData);
        scrollBtnData.add("uv", scrollBtnData.get("uvDisabled"));
        this.scrollBtn[1] = new GuiElementInst(scrollBarPos, new Texture(), scrollBtnData).initialize(gui);
        this.scrollBtn[1].get().bakeData(gui, scrollBtnData, this.scrollBtn[1]);

        this.rebuildElements(gui, data);

        this.scroll = 0.0F;

        this.rebuildChildren(gui, data, false);
    }

    public boolean bakeElements() {
        return true;
    }

    public void rebuildElements(IGui gui, JsonObject data) {
        GuiElementInst[] elements = this.getElements(gui, data);
        boolean bake = this.bakeElements();

        this.elements.clear();
        Arrays.stream(elements).forEach(e -> {
            IGuiElement elemInst = e.get();
            if( bake ) {
                elemInst.bakeData(gui, e.data, e);
            }
            this.elements.put(Range.closedOpen(e.pos[1], e.pos[1] + elemInst.getHeight()), e);
        });
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        this.rebuildChildren(gui, data, false);

        super.tick(gui, data);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        this.posX = x;
        this.posY = y;

        if( this.prevLmbDown && !Minecraft.getInstance().mouseHandler.isLeftPressed() ) {
            this.prevLmbDown = false;
        }

        GuiElementInst btn = this.scrollBtn[this.countAll > this.countSub ? 0 : 1];
        int scrollY = btn.pos[1] + (int) Math.round(this.scroll * (this.scrollHeight - btn.get(Texture.class).size[1]));

        btn.get().render(gui, stack, partTicks, btn.pos[0], scrollY, mouseX, mouseY, btn.data);

        GuiUtils.enableScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.areaSize[0], this.areaSize[1]);
        super.render(gui, stack, partTicks, x, y - this.sData.minY, mouseX, mouseY, data);
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
                this.scroll += Math.min(1.0F / this.elementsView.size(), this.maxScrollDelta);
            }
            this.clipScroll();
        } else if( mouseScroll > 0 ) {
            if( this.rasterized ) {
                this.scroll = this.getRasterScroll(false);
            } else {
                this.scroll -= Math.min(1.0F / this.elementsView.size(), this.maxScrollDelta);
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
        GuiElementInst btn = this.scrollBtn[this.countAll > this.countSub ? 0 : 1];
        Texture btnElem = btn.get(Texture.class);

        if( this.countAll > this.countSub
            && (this.prevLmbDown || IGuiElement.isHovering(gui, btn.pos[0], btn.pos[1], mouseX, mouseY, btnElem.size[0], this.scrollHeight)) )
        {
            double scrollAmt = mouseY - gui.getScreenPosY() - btn.pos[1] - btnElem.size[1] / 2.0D;
            this.scroll = Math.max(0.0F, Math.min(1.0F, 1.0F / (this.scrollHeight - btnElem.size[1]) * scrollAmt));
            this.prevLmbDown = true;
        } else {
            this.prevLmbDown = false;
        }

        if( IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.areaSize[0], this.areaSize[1]) ) {
            return super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
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
    }

    public float getRasterScroll(boolean next) {
        ScrollData sData = this.getScrollData(this.scroll, this.rasterized);
        Map<Range<Integer>, GuiElementInst> sRange = this.getSubRange(sData.minY, sData.maxY, false);
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

        int scrollArea = sData.totalHeight - this.areaSize[1];

        return 1.0F / scrollArea * second.getValue().pos[1];
    }

    public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return JsonUtils.GSON.fromJson(elementData.get("elements"), GuiElementInst[].class);
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

        data.totalHeight = this.elementsView.size() > 0 ? this.elements.span().upperEndpoint() : 0;
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
