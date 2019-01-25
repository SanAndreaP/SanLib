package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class ScrollAreaGuiElement
        implements IGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("scroll_area");

    BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.areaSize = JsonUtils.getIntArray(data.get("areaSize"), Range.is(2));
            this.data.scrollPos = JsonUtils.getIntArray(data.get("scrollbarPos"), Range.is(2));
            this.data.scrollSize = JsonUtils.getIntArray(data.get("scrollbarSize"), Range.is(2));
            this.data.btnTexture = new ResourceLocation(data.get("buttonTexture").getAsString());
            this.data.btnHeight = JsonUtils.getIntVal(data.get("buttonHeight"));
            this.data.btnUV = JsonUtils.getIntArray(data.get("buttonUV"), Range.is(2));
            this.data.btnDisabledUV = JsonUtils.getIntArray(data.get("buttonDisabledUV"), Range.is(2));
            this.data.btnDeactiveUV = JsonUtils.getIntArray(data.get("buttonDeactiveUV"), Range.is(2));
            this.data.rasterized = JsonUtils.getBoolVal(data.get("rasterized"), false);

            GuiElementInst[] elements = this.getElements(gui, data);
            Arrays.stream(elements).forEach(e -> {
                IGuiElement elemInst = e.get();
                elemInst.bakeData(gui, e.data);
                int min = e.pos[1];
                int max = e.pos[1] + elemInst.getHeight();
                this.data.elements.put(com.google.common.collect.Range.closedOpen(min, max), e);
            });
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.data.areaSize[0], this.data.areaSize[1]);
        
        this.data.elements.subRangeMap(com.google.common.collect.Range.closedOpen(0, this.data.areaSize[1])).asMapOfRanges().forEach((k, e) -> {
            e.get().render(gui, partTicks, x + e.pos[0], y + e.pos[1], mouseX - x, mouseY - y, e.data);
        });

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return JsonUtils.GSON.fromJson(elementData.get("elements"), GuiElementInst[].class);
    }

    @Override
    public int getHeight() {
        return this.data.areaSize[1];
    }

    private static final class BakedData
    {
        private ResourceLocation btnTexture;
        private int[] areaSize;
        private int[] scrollPos;
        private int[] scrollSize;
        private int btnHeight;
        private int[] btnUV;
        private int[] btnDisabledUV;
        private int[] btnDeactiveUV;
        private boolean rasterized;

        @SuppressWarnings("UnstableApiUsage")
        protected RangeMap<Integer, GuiElementInst> elements = TreeRangeMap.create();
    }
}
