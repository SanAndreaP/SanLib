package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
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
 *         <td>A color value as defined in {@link ColorData}.</td>
 *     </tr>
 *     <tr>
 *         <td>colors</td>
 *         <td>if <tt>color</tt> isn't defined</td>
 *         <td>array(ColorDef)</td>
 *         <td>An array of multiple {@link ColorData} objects.
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

    protected final List<ColorData> colors      = new ArrayList<>();
    protected       Orientation     orientation = Orientation.VERTICAL;

    private final List<ColorEntry> colorCache = new ArrayList<>();

    public Rectangle(String id) {
        super(id);
    }

    @Override
    @SuppressWarnings("java:S3776")
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        boolean disabled = !this.isEnabled();
        boolean hovering = this.isHovering();

        if( !this.colorCache.isEmpty() ) {
            if( this.colorCache.size() > 1 ) {
                for( int i = 0, max = this.colorCache.size() - 1; i < max; i++ ) {
                    ColorEntry curr = this.colorCache.get(i);
                    ColorEntry next = this.colorCache.get(i + 1);
                    float      size = next.relStop - curr.relStop;

                    if( size > 0.0F ) {
                        float fx = this.orientation == Orientation.VERTICAL ? curr.relStop : 0;
                        float fy = this.orientation == Orientation.HORIZONTAL ? curr.relStop : 0;
                        float fw = this.orientation == Orientation.VERTICAL ? size : this.getWidth();
                        float fh = this.orientation == Orientation.HORIZONTAL ? size : this.getHeight();

                        int currColor = curr.color.getColor(disabled, hovering);
                        int nextColor = next.color.getColor(disabled, hovering);
                        GuiUtils.drawGradient(graphics, x + fx, y + fy, fw, fh, currColor, nextColor, this.orientation == Orientation.HORIZONTAL);
                    }
                }
            } else {
                graphics.fill(x, y, x + this.getWidth(), y + getHeight(), this.colors.getFirst().getColor(disabled, hovering));
            }
            RenderSystem.enableBlend();
        }
    }

    void buildColorCache() {
        this.colorCache.clear();

        int colorsSize = this.colors.size();
        if( colorsSize > 1 ) {
            float maxSize    = this.orientation == Orientation.HORIZONTAL ? this.getWidth() : this.getHeight();
            Float linearStop = this.colors.getFirst().hasStop() ? null : maxSize / (colorsSize - 1);

            for( int i = 0; i < colorsSize; i++ ) {
                ColorData def  = this.colors.get(i);
                float     stop = linearStop != null ? linearStop * i : def.stop() * maxSize;

                this.colorCache.add(new ColorEntry(stop, def));
            }
        } else {
            this.colorCache.add(new ColorEntry(0.0F, this.colors.getFirst()));
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.orientation = Orientation.fromString(JsonUtils.getStringVal(data.get("orientation"), Orientation.VERTICAL.toString()));

        ColorData.loadColors(data, this.colors, ColorData.WHITE.color());
        this.buildColorCache();
    }

    // region Getters & Setters
    public ColorData[] getColors() {
        return this.colors.toArray(new ColorData[0]);
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public void setColors(@Nonnull ColorData... colors) {
        if( colors.length < 1 ) {
            throw new IllegalArgumentException("at least one color must be passed");
        }

        List<ColorData> colorsList = Arrays.asList(colors);

        checkStops(colorsList, null);

        this.colors.clear();
        this.colors.addAll(colorsList);
        this.buildColorCache();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    private static Boolean checkStops(@Nonnull List<ColorData> colors, Boolean hasStop) {
        for( ColorData c : colors ) {
            hasStop = ColorData.checkStop(c, hasStop);
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
        float     relStop;
        ColorData color;

        ColorEntry(float relStop, ColorData colorData) {
            this.relStop = relStop;
            this.color = colorData;
        }
    }

    public static class Builder<T extends Rectangle>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {super(elem);}

        public Builder<T> withColor(ColorData color) {
            return this.withColors(color);
        }

        public Builder<T> withColor(int color) {
            return this.withColors(new ColorData(color));
        }

        public Builder<T> withColor(float stop, int color) {
            return this.withColors(new ColorData(stop, color));
        }

        public Builder<T> withColor(int color, int hoverColor, int disabledColor) {
            return this.withColors(new ColorData(new ColorData.StatedColor(color, hoverColor, disabledColor)));
        }

        public Builder<T> withColor(float stop, int color, int hoverColor, int disabledColor) {
            return this.withColors(new ColorData(stop, new ColorData.StatedColor(color, hoverColor, disabledColor)));
        }

        public Builder<T> withColors(@Nonnull ColorData... color) {
            if( color.length < 1 ) {
                return this;
            }

            List<ColorData> colors = Arrays.asList(color);
            Rectangle.checkStops(this.elem.colors, Rectangle.checkStops(colors, null));

            this.elem.colors.addAll(colors);
            this.elem.buildColorCache();

            return this;
        }

        public Builder<T> withOrientation(Orientation orientation) {
            this.elem.orientation = orientation;

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
