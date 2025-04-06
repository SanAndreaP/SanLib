package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.element.data.ItemData;
import dev.sanandrea.mods.sanlib.lib.client.gui.element.data.Spacing;
import dev.sanandrea.mods.sanlib.lib.util.ColorUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class Item
        extends GuiElement
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("item");

    public static final String JSON_ITEM            = "item";
    public static final String JSON_WITH_DECO       = "withDecorations";
    public static final String JSON_PADDING         = "padding";
    public static final String JSON_SCALE           = "scale";
    public static final String JSON_HIGHLIGHT       = "withHighlight";
    public static final String JSON_HIGHLIGHT_COLOR = "highlightColor";

    protected ItemData item;
    protected boolean  withDecorations = true;
    protected Spacing  padding         = new Spacing(1, false);
    protected float    scale           = 1.0F;
    protected boolean  withHighlight   = false;
    protected int      highlightColor  = 0x80FFFFFF;
    
    protected       boolean sizeUpdated = false;
    protected final Font    font        = Minecraft.getInstance().font;

    public Item(String id) {
        super(id);

        this.width = 18;
        this.height = 18;
    }

    @Override
    public void tick(IGui gui) {
        if( !this.sizeUpdated ) {
            this.sizeUpdated = true;
            this.updateSize();
        }
    }

    protected void updateSize() {
        int size = Mth.ceil(16 * this.scale);
        this.width = size + this.padding.getWidth();
        this.height = size + this.padding.getHeight();
        this.runGeometryListeners();
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        boolean isDisabled = !this.isEnabled();
        boolean isHovering = this.isHovering();

        ItemStack stack = this.item.get(isHovering, isDisabled);

        graphics.pose().pushPose();
        graphics.pose().translate(x + this.padding.getLeft(), y + this.padding.getBottom(), 0.0F);
        graphics.pose().scale(this.scale, this.scale, 1.0F);
        graphics.renderItem(stack, 0, 0);
        if( withDecorations ) {
            float decoShift = (1.0F - this.scale) / this.scale;
            graphics.pose().translate(decoShift, decoShift, 0.0F);
            graphics.renderItemDecorations(this.font, stack, 0, 0);
        }
        graphics.pose().popPose();

        if( this.withHighlight && isHovering ) {
            graphics.fillGradient(RenderType.guiOverlay(), x, y, x + this.getWidth(), y + this.getHeight(), highlightColor, highlightColor, 0);
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.item = ItemData.fromJson(guiDef, data.get(JSON_ITEM));
        this.withDecorations = JsonUtils.getBoolVal(data.get(JSON_WITH_DECO), this.withDecorations);
        this.padding = Spacing.fromJson(data.get(JSON_PADDING), false, this.padding);
        this.scale = JsonUtils.getFloatVal(data.get(JSON_SCALE), this.scale);
        this.withHighlight = JsonUtils.getBoolVal(data.get(JSON_HIGHLIGHT), this.withHighlight);
        JsonUtils.fetchString(data.get(JSON_HIGHLIGHT_COLOR), s -> this.highlightColor = ColorUtils.parseColorString(s, this.highlightColor));
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        if( scale > 0.0F ) {
            this.scale = scale;
            this.updateSize();
        }
    }
}
