package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.client.util.GuiUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * A colored rectangle. This supports multicolor gradients with stop values.<br>
 * Following JSON values are available:
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
@GuiElement.Resizable
public class Rectangle
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("rectangle");

    protected final List<ColorDef> colors               = new ArrayList<>();
    protected boolean              isGradientHorizontal = false;

    private final List<ColorEntry> colorCache = new ArrayList<>();

    @Override
    public void update(IGui gui, boolean updateState) {
        if( updateState ) {
            this.buildColorCache();
        }
    }

    @Override
    @SuppressWarnings("java:S3776")
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) {
        if( !this.colorCache.isEmpty() ) {
            if( this.colorCache.size() > 1 ) {
                for( int i = 0, max = this.colorCache.size() - 1; i < max; i++ ) {
                    ColorEntry curr = this.colorCache.get(i);
                    ColorEntry next = this.colorCache.get(i + 1);
                    float size = next.relStop - curr.relStop;

                    if( size > 0.0F ) {
                        float fx = !this.isGradientHorizontal ? curr.relStop : 0;
                        float fy = this.isGradientHorizontal ? curr.relStop : 0;
                        float fw = !this.isGradientHorizontal ? size : this.getWidth();
                        float fh = this.isGradientHorizontal ? size : this.getHeight();

                        GuiUtils.drawGradient(matrixStack, x + fx, y + fy, fw, fh, curr.color, next.color, this.isGradientHorizontal);
                    }
                }
            } else {
                AbstractGui.fill(matrixStack, x, y, this.getWidth(), this.getHeight(), this.colors.get(0).color);
            }
            RenderSystem.enableBlend();
        }
    }

    private void buildColorCache() {
        this.colorCache.clear();

        int colorsSize = this.colors.size();
        if( colorsSize > 1 ) {
            float maxSize = this.isGradientHorizontal ? this.getWidth() : this.getHeight();
            Float linearStop = this.colors.get(0).hasStop() ? null : maxSize / (colorsSize - 1);

            for( int i = 0; i < colorsSize; i++ ) {
                ColorDef def = this.colors.get(i);
                float stop = linearStop != null ? linearStop * i : def.stop * maxSize;

                this.colorCache.add(new ColorEntry(stop, def));
            }
        } else {
            this.colorCache.add(new ColorEntry(0.0F, this.colors.get(0)));
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.isGradientHorizontal = JsonUtils.getBoolVal(data.get("isGradientHorizontal"), false);

        ColorDef.loadColors(data, this.colors);
    }

    private static final class ColorEntry
    {
        float relStop;
        int color;

        ColorEntry(float relStop, ColorDef colorDef) {
            this.relStop = relStop;
            this.color = colorDef.color;
        }
    }
}
