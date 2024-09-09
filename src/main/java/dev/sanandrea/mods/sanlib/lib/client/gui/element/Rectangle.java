package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.util.GuiUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A colored rectangle. This supports multicolor gradients with stop values.<br> Following JSON values are available:
 * <table valign="top" border="0" cellpadding="2">
 *     <tr>
 *         <th>Name</th>
 *         <th>Mandatory</th>
 *         <th>Type</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td><i>common values, <i>see {@link GuiElement}</i></i></td>
 *         <td>-/-</td>
 *         <td>-/-</td>
 *         <td>-/-</td>
 *     </tr>
 *     <tr>
 *         <td>color</td>
 *         <td>if <tt>colors</tt> isn't defined</td>
 *         <td>string</td>
 *         <td>A color value as defined in {@link ColorDef}.</td>
 *     </tr>
 *     <tr>
 *         <td>colors</td>
 *         <td>if <tt>color</tt> isn't defined</td>
 *         <td>array(ColorDef)</td>
 *         <td>An array of multiple {@link ColorDef} objects.
 *             Note, if you define a 'stop' value for one object, all other objects must also have it defined.</td>
 *     </tr>
 * </table>
 */
@SuppressWarnings("unused")
@GuiElement.Resizable
public class Rectangle
        extends GuiElement
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("rectangle");

    protected final List<ColorDef> colors               = new ArrayList<>();
    protected       boolean        isGradientHorizontal = false;

    private final List<ColorEntry> colorCache = new ArrayList<>();

    public Rectangle(String id) {
        super(id);
    }

    @Override
    @SuppressWarnings("java:S3776")
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        if( !this.colorCache.isEmpty() ) {
            if( this.colorCache.size() > 1 ) {
                for( int i = 0, max = this.colorCache.size() - 1; i < max; i++ ) {
                    ColorEntry curr = this.colorCache.get(i);
                    ColorEntry next = this.colorCache.get(i + 1);
                    float      size = next.relStop - curr.relStop;

                    if( size > 0.0F ) {
                        float fx = !this.isGradientHorizontal ? curr.relStop : 0;
                        float fy = this.isGradientHorizontal ? curr.relStop : 0;
                        float fw = !this.isGradientHorizontal ? size : this.getWidth();
                        float fh = this.isGradientHorizontal ? size : this.getHeight();

                        GuiUtils.drawGradient(graphics, x + fx, y + fy, fw, fh, curr.color, next.color, this.isGradientHorizontal);
                    }
                }
            } else {
                graphics.fill(x, y, x + this.getWidth(), y + getHeight(), this.colors.getFirst().color);
            }
            RenderSystem.enableBlend();
        }
    }

    void buildColorCache() {
        this.colorCache.clear();

        int colorsSize = this.colors.size();
        if( colorsSize > 1 ) {
            float maxSize    = this.isGradientHorizontal ? this.getWidth() : this.getHeight();
            Float linearStop = this.colors.getFirst().hasStop() ? null : maxSize / (colorsSize - 1);

            for( int i = 0; i < colorsSize; i++ ) {
                ColorDef def  = this.colors.get(i);
                float    stop = linearStop != null ? linearStop * i : def.stop * maxSize;

                this.colorCache.add(new ColorEntry(stop, def));
            }
        } else {
            this.colorCache.add(new ColorEntry(0.0F, this.colors.getFirst()));
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.isGradientHorizontal = JsonUtils.getBoolVal(data.get("isGradientHorizontal"), false);

        ColorDef.loadColors(data, this.colors, null);
        this.buildColorCache();
    }

    // region Getters & Setters
    public ColorDef[] getColors() {
        return this.colors.toArray(new ColorDef[0]);
    }

    public boolean isGradientHorizontal() {
        return this.isGradientHorizontal;
    }

    public void setColors(@Nonnull ColorDef... colors) {
        if( colors.length < 1 ) {
            throw new IllegalArgumentException("at least one color must be passed");
        }

        List<ColorDef> colorsList = Arrays.asList(colors);

        checkStops(colorsList, null);

        this.colors.clear();
        this.colors.addAll(colorsList);
        this.buildColorCache();
    }

    public void setGradientHorizontal(boolean gradientHorizontal) {
        this.isGradientHorizontal = gradientHorizontal;
    }

    private static Boolean checkStops(@Nonnull List<ColorDef> colors, Boolean hasStop) {
        for( ColorDef c : colors ) {
            hasStop = ColorDef.checkStop(c, hasStop);
        }

        return hasStop;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.buildColorCache();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.buildColorCache();
    }

    // endregion

    private static final class ColorEntry
    {
        float relStop;
        int   color;

        ColorEntry(float relStop, ColorDef colorDef) {
            this.relStop = relStop;
            this.color = colorDef.color;
        }
    }

    public static class Builder<T extends Rectangle>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {super(elem);}

        public Builder<T> withColor(ColorDef color) {
            return this.withColors(color);
        }

        public Builder<T> withColor(int color) {
            return this.withColors(new ColorDef(color));
        }

        public Builder<T> withColor(float stop, int color) {
            return this.withColors(new ColorDef(stop, color));
        }

        public Builder<T> withColors(@Nonnull ColorDef... color) {
            if( color.length < 1 ) {
                return this;
            }

            List<ColorDef> colors = Arrays.asList(color);
            Rectangle.checkStops(this.elem.colors, Rectangle.checkStops(colors, null));

            this.elem.colors.addAll(colors);
            this.elem.buildColorCache();

            return this;
        }

        public Builder<T> withHorizontalGradient() {
            this.elem.isGradientHorizontal = true;

            return this;
        }

        public Builder<T> withVerticalGradient() {
            this.elem.isGradientHorizontal = false;

            return this;
        }

        public static Builder<Rectangle> createRectangle() {
            return createRectangle(UUID.randomUUID().toString());
        }

        public static Builder<Rectangle> createRectangle(String id) {
            return new Builder<>(new Rectangle(id));
        }
    }
}
