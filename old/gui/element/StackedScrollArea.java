/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGuiElement;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class StackedScrollArea
        extends ScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation("stacked_scroll_area");

    private final List<GuiElementInst>         allElements = new ArrayList<>();
    private final Map<GuiElementInst, Integer> stackedYs   = new HashMap<>();

    public StackedScrollArea(int[] areaSize, int scrollHeight, boolean rasterized, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui) {
        super(areaSize, scrollHeight, rasterized, maxScrollDelta, scrollbarPos, scrollButton, gui);
    }

    @Override
    public void put(Range<Integer> id, @Nonnull GuiElementInst child) {
        super.put(id, child);

        this.allElements.add(child);
    }

    @Override
    public GuiElementInst remove(Range<Integer> id) {
        GuiElementInst elem = super.remove(id);

        this.allElements.remove(elem);

        return elem;
    }

    @Override
    public void clear() {
        super.clear();

        this.allElements.clear();
    }

    @Override
    public void update(IGui gui) {
        this.elements.clear();

        if( !this.prebuiltElements.isEmpty() ) {
            this.prebuiltElements.forEach(e -> e.get().setup(gui, e));
            this.allElements.addAll(this.prebuiltElements);
            this.prebuiltElements.clear();
        }

        List<GuiElementInst> elementsIntrn = new ArrayList<>(this.allElements);

        elementsIntrn = this.sortAndFilter(elementsIntrn);
        stackedYs.clear();

        int posY = 0;
        for( GuiElementInst e : elementsIntrn ) {
            if( e.isVisible() ) {
                int newPosY = posY + e.pos[1];
                int heightE = e.get().getHeight();

                posY = newPosY + heightE;
                this.elements.put(Range.closedOpen(newPosY, posY), e);
                this.stackedYs.put(e, newPosY);
            }
        }

        this.update();
    }

    protected List<GuiElementInst> sortAndFilter(List<GuiElementInst> elements) {
        return elements;
    }

    @Override
    protected void renderElements(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.doWorkV(e -> GuiDefinition.renderElement(gui, stack, x + e.pos[0], y + this.stackedYs.getOrDefault(e, 0), mouseX, mouseY, partTicks, e, true));
    }

    public static class Builder
            extends ScrollArea.Builder
    {
        public Builder(int[] areaSize) {
            super(areaSize);
        }

        @Override
        public StackedScrollArea get(IGui gui) {
            this.sanitize(gui);

            StackedScrollArea sa = new StackedScrollArea(this.areaSize, this.scrollHeight, this.rasterized, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, gui);

            if( this.elements != null && this.elements.length > 0 ) {
                Arrays.stream(this.elements).forEach(e -> sa.add(e.initialize(gui), true));
                sa.update();
            }

            return sa;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadElements);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<ScrollArea.Builder, BiFunction<IGui, JsonElement, GuiElementInst[]>> loadElementsFunc) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, null);
            Builder b = IGuiElement.IBuilder.copyValues(sab, new Builder(sab.areaSize));

            b.elements(MiscUtils.apply(loadElementsFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data.get("elements")))));

            return b;
        }

        public static StackedScrollArea fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
