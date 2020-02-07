package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.Range;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "WeakerAccess", "Duplicates", "unused" })
public class Button
        extends ElementParent
{
    public static final String LABEL = "label";

    public static final ResourceLocation ID = new ResourceLocation("button");

    public ResourceLocation texture;
    public int[]            size;
    public int[]            textureSize;
    public int[]            uvEnabled;
    public int[]            uvHover;
    public int[]            uvDisabled;
    public int[]            uvSize;
    public int              ctHorizontal;
    public int              ctVertical;
    public int              buttonFunction;

    protected GuiButton buttonDelegate;

    protected int     currMouseX;
    protected int     currMouseY;
    protected boolean isCurrHovering;

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        JsonElement lbl = data.get("label");
        GuiElementInst lblInst = null;
        if( lbl != null ) {
            lblInst = JsonUtils.GSON.fromJson(lbl, GuiElementInst.class).initialize(gui);
        } else {
            lbl = data.get("labelText");
            if( lbl != null ) {
                int[] lblPos = new int[] { this.size[0] / 2, this.size[1] / 2};
                if( lbl.isJsonPrimitive() ) {
                    JsonObject colors = new JsonObject();
                    colors.addProperty("default", "0xFFFFFFFF");
                    colors.addProperty("hover", "0xFFFFFFA0");
                    colors.addProperty("disabled", "0xFFA0A0A0");

                    JsonObject lblData = new JsonObject();
                    JsonUtils.addJsonProperty(lblData, "text", lbl.getAsString());
                    lblData.add("color", colors);

                    lblInst = new GuiElementInst(lblPos, new Text(), lblData).initialize(gui);
                } else {
                    lblInst = new GuiElementInst(lblPos, new Text(), lbl.getAsJsonObject()).initialize(gui);
                }
            }
        }

        if( lblInst != null ) {
            lblInst.alignHorizontal = GuiElementInst.Justify.fromString(JsonUtils.getStringVal(data.get("alignLabelHorizontal"), "center"));
            lblInst.alignVertical = GuiElementInst.Justify.fromString(JsonUtils.getStringVal(data.get("alignLabelVertical"), "center"));

            listToBuild.put(LABEL, lblInst);
        }
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( JsonUtils.getBoolVal(data.get("useVanillaTexture"), true) ) {
            this.texture = new ResourceLocation(JsonUtils.getStringVal(data.get("texture"), "textures/gui/widgets.png"));
        } else {
            this.texture = gui.getDefinition().getTexture(data.get("texture"));
        }
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.uvSize = JsonUtils.getIntArray(data.get("uvSize"), new int[] { 200, 20 }, Range.is(2));
        this.uvEnabled = JsonUtils.getIntArray(data.get("uvEnabled"), new int[] { 0, 66 }, Range.is(2));
        this.uvHover = JsonUtils.getIntArray(data.get("uvHover"), new int[] { this.uvEnabled[0], this.uvEnabled[1] + this.uvSize[1] }, Range.is(2));
        this.uvDisabled = JsonUtils.getIntArray(data.get("uvDisabled"), new int[] { this.uvEnabled[0], this.uvEnabled[1] - this.uvSize[1] }, Range.is(2));
        this.ctHorizontal = JsonUtils.getIntVal(data.get("ctHorizontal"), 190);
        this.ctVertical = JsonUtils.getIntVal(data.get("ctVertical"), 14);
        this.buttonFunction = JsonUtils.getIntVal(data.get("buttonFunction"));
        this.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] { 256, 256 }, Range.is(2));

        this.buttonDelegate = new GuiButton(this.buttonFunction, 0, 0, "");

        super.bakeData(gui, data, inst);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.currMouseX = mouseX;
        this.currMouseY = mouseY;

        this.isCurrHovering = isHovering(gui, x, y, mouseX, mouseY);
        boolean isEnabled = this.isEnabled();

        gui.get().mc.renderEngine.bindTexture(this.texture);
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate(x, y, 0.0D);
        drawRect(isEnabled, this.isCurrHovering);
        GlStateManager.popMatrix();

        GuiElementInst label = this.getChild(LABEL);
        if( label != null ) {
            IGuiElement e = label.get();
            if( e instanceof Text ) {
                ((Text) e).setColor(isEnabled ? this.isCurrHovering ? "hover"
                                                                    : "default"
                                              : "disabled");
            }

            GuiDefinition.renderElement(gui, x + label.pos[0], y + label.pos[1], mouseX, mouseY, partTicks, label);
        }
    }

    public boolean isHovering(IGui gui, int x, int y, int mouseX, int mouseY) {
        return IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]);
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        if( mouseButton == 0 && this.isEnabled() && this.isCurrHovering ) {
            GuiScreen gs = gui.get();
            GuiButton btn = this.buttonDelegate;
            List<GuiButton> btnList = new ArrayList<>(Collections.singletonList(this.buttonDelegate));
            GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(gs, btn, btnList);
            if( MinecraftForge.EVENT_BUS.post(event) ) {
                return true;
            }
            btn = event.getButton();

            btn.playPressSound(gs.mc.getSoundHandler());
            this.performAction(gui, btn.id);
            if( gs.equals(gs.mc.currentScreen) ) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(gs, btn, btnList));
            }

            return true;
        }

        return super.mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    public void performAction(IGui gui, int id) {
        gui.performAction(this, id);
    }

    protected void drawRect(boolean enabled, boolean hovered) {
        int[] uv = enabled
                   ? (hovered ? this.uvHover : this.uvEnabled)
                   : this.uvDisabled;

        if( this.uvSize[0] == this.size[0] && this.uvSize[1] == this.size[1] ) {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, uv[0], uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        } else {
            int cornerWidth = (this.uvSize[0] - this.ctHorizontal) / 2;
            int cornerHeight = (this.uvSize[1] - this.ctVertical) / 2;

            Gui.drawModalRectWithCustomSizedTexture(0, 0,
                                                    uv[0], uv[1],
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            Gui.drawModalRectWithCustomSizedTexture(0, this.size[1] - cornerHeight,
                                                    uv[0], uv[1] + this.uvSize[1] - cornerHeight,
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            Gui.drawModalRectWithCustomSizedTexture(this.size[0] - cornerWidth, 0,
                                                    uv[0] + this.uvSize[0] - cornerWidth, uv[1],
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);
            Gui.drawModalRectWithCustomSizedTexture(this.size[0] - cornerWidth, this.size[1] - cornerHeight,
                                                    uv[0] + this.uvSize[0] - cornerWidth, uv[1] + this.uvSize[1] - cornerHeight,
                                                    cornerWidth, cornerHeight,
                                                    this.textureSize[0], this.textureSize[1]);

            drawTiledTexture(0, cornerHeight,
                             uv[0], uv[1] + cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(cornerWidth, 0,
                             uv[0] + cornerWidth, uv[1],
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(this.size[0] - cornerWidth, cornerHeight,
                             uv[0] + this.uvSize[0] - cornerWidth, uv[1] + cornerHeight,
                             cornerWidth, this.uvSize[1] - cornerHeight * 2,
                             cornerWidth, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
            drawTiledTexture(cornerWidth, this.size[1] - cornerHeight,
                             uv[0] + cornerWidth, uv[1] + this.uvSize[1] - cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, cornerHeight,
                             this.size[0] - cornerWidth * 2, cornerHeight,
                             this.textureSize[0], this.textureSize[1]);

            drawTiledTexture(cornerWidth, cornerHeight,
                             uv[0] + cornerWidth, uv[1] + cornerHeight,
                             this.uvSize[0] - cornerWidth * 2, this.uvSize[1] - cornerHeight * 2,
                             this.size[0] - cornerWidth * 2, this.size[1] - cornerHeight * 2,
                             this.textureSize[0], this.textureSize[1]);
        }
    }

    protected static void drawTiledTexture(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, int sheetWidth, int sheetHeight) {
        int txWidth = width;
        int txHeight = height;
        int uvX = Math.min(uWidth, width);
        int uvY = Math.min(vHeight, height);
        while( uvX > 0 ) {
            while( uvY > 0 ) {
                Gui.drawModalRectWithCustomSizedTexture(x + txWidth - width, y + txHeight - height, u, v, uvX, uvY, sheetWidth, sheetHeight);

                height -= vHeight;
                uvY = Math.min(vHeight, height);
            }

            height = txHeight;
            uvY = Math.min(vHeight, height);

            width -= uWidth;
            uvX = Math.min(uWidth, width);
        }
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public void setEnabled(boolean enabled) {
        this.buttonDelegate.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.buttonDelegate.enabled;
    }
}
