package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1450"})
public class StackPanel
        extends ElementParent<Integer>
{
    public static final ResourceLocation ID = new ResourceLocation("stack_panel");

    public static final String CONTENT = "content";

    protected int     currWidth;
    protected int     currHeight;
    protected int[]   padding; // top - right - bottom - left
    protected boolean horizontal;

    private int currPosMod;
    private int currPosMax;

    public StackPanel(int[] padding, boolean horizontal, GuiElementInst... elements) {
        this.padding = adjustPadding(padding);
        this.horizontal = horizontal;

        for( GuiElementInst element : elements ) {
            this.add(element);
        }
    }

    public void add(GuiElementInst inst) {
        this.put(this.namedChildren.size(), inst);
    }

    protected void calcSize() {
        this.currWidth = !this.horizontal ? 0 : this.padding[1] + this.padding[3];
        this.currHeight = this.horizontal ? 0 : this.padding[0] + this.padding[2];
        for( int i = 0, max = this.size(); i < max; i++ ) {
            GuiElementInst elem = this.get(i);
            if( this.horizontal ) {
                this.currWidth += elem.pos[0] + elem.get().getWidth();
                this.currHeight = Math.max(this.currHeight, elem.get().getHeight());
            } else {
                this.currWidth = Math.max(this.currWidth, elem.get().getWidth());
                this.currHeight += elem.pos[1] + elem.get().getHeight();
            }
        }
    }

    @Override
    protected void update(boolean isOnSetup) {
        super.update(isOnSetup);

        if( !isOnSetup ) {
            this.calcSize();
        }
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.calcSize();
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.currPosMax = 0;
        this.currPosMod = this.padding[this.horizontal ? 3 : 0];

        this.doWorkV(e -> {
            this.currPosMod += e.pos[this.horizontal ? 0 : 1];
            GuiDefinition.renderElement(gui, stack,
                                        x + (this.horizontal ? this.currPosMod : e.pos[0] + this.padding[3]),
                                        y + (this.horizontal ? e.pos[1] + this.padding[0] : this.currPosMod),
                                        mouseX, mouseY, partTicks, e, true);
            this.currPosMod += this.horizontal ? e.get().getWidth() : e.get().getHeight();
            this.currPosMax = Math.max(this.currPosMax, this.horizontal ? e.get().getHeight() : e.get().getWidth());
        });

        if( this.horizontal ) {
            this.currWidth = this.currPosMod + this.padding[1];
            this.currHeight = this.padding[0] + this.padding[2] + this.currPosMax;
        } else {
            this.currHeight = this.currPosMod + this.padding[2];
            this.currWidth = this.padding[1] + this.padding[3] + this.currPosMax;
        }
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    public static class Builder
            implements IBuilder<StackPanel>
    {
        protected int[] padding;
        protected boolean horizontal;
        protected GuiElementInst[] elements;

        public Builder() {
            this.horizontal = false;
        }

        public Builder padding(int[] padding)             { this.padding = padding;       return this; }
        public Builder horizontal(boolean horizontal)     { this.horizontal = horizontal; return this; }
        public Builder content(GuiElementInst[] elements) { this.elements = elements;     return this; }

        public Builder padding(int all)                                  { return this.padding(new int[] { all}); }
        public Builder padding(int topBottom, int leftRight)             { return this.padding(new int[] { topBottom, leftRight}); }
        public Builder padding(int top, int leftRight, int bottom)       { return this.padding(new int[] { top, leftRight, bottom}); }
        public Builder padding(int top, int right, int bottom, int left) { return this.padding(new int[] { top, right, bottom, left}); }

        @Override
        public void sanitize(IGui gui) {
            if( this.padding == null ) {
                this.padding = new int[] {0};
            }

            if( this.elements == null ) {
                this.elements = new GuiElementInst[0];
            }
        }

        @Override
        public StackPanel get(IGui gui) {
            this.sanitize(gui);

            for( GuiElementInst e : this.elements ) { e.initialize(gui); }

            return new StackPanel(this.padding, this.horizontal, this.elements);
        }

        protected GuiElementInst[] loadContent(IGui gui, JsonObject data) {
            if( data.has(CONTENT) ) {
                return JsonUtils.GSON.fromJson(data.get(CONTENT), GuiElementInst[].class);
            } else {
                return new GuiElementInst[0];
            }
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadContent);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<Builder, BiFunction<IGui, JsonObject, GuiElementInst[]>> loadContentFunc) {
            Builder b = new Builder();

            JsonUtils.fetchIntArray(data.get("padding"), b::padding, Range.between(0, 4));
            JsonUtils.fetchBool(data.get("horizontal"), b::horizontal);

            GuiElementInst[] content = MiscUtils.apply(loadContentFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data)));
            if( content != null ) {
                b.content(content);
            }

            return b;
        }

        public static StackPanel fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
