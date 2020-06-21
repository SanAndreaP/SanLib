package de.sanandrew.mods.sanlib.client.lexicon2;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class GuiLexicon
        extends GuiScreen
        implements IGui
{
    private static final List<ResourceLocation> LEXICA = new ArrayList<>();

    private final GuiDefinition guiDef;

    public GuiElementInst contentPane;

    protected int posX;
    protected int posY;

    static {
        GuiDefinition.TYPES.put(new ResourceLocation(Constants.ID, "lexicon_content_area"), ContentPane::new);
    }

    public GuiLexicon(int lexiconId) throws IOException {
        this.guiDef = GuiDefinition.getNewDefinition(getResource(LEXICA.get(lexiconId), "lexicon.json"));

        this.contentPane = this.guiDef.getElementById("contentPane");
        if( this.contentPane == null ) {
            throw new InvalidObjectException(String.format("There must be an element with the ID \"contentPane\" of type or a subclass of %s", ContentPane.class));
        }
    }

    public GuiLexicon(int lexiconId, ResourceLocation page) throws IOException {
        this(lexiconId);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.guiDef.width) / 2;
        this.posY = (this.height - this.guiDef.height) / 2;

        this.guiDef.initGui(this);
    }

    @Override
    public void updateScreen() {
        this.guiDef.update(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0.0F);
        this.guiDef.drawBackground(this, mouseX, mouseY, partialTicks);
        this.guiDef.drawForeground(this, mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDef.handleMouseInput(this);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.guiDef.mouseReleased(this, mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.guiDef.mouseClickMove(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.guiDef.keyTyped(this, typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.guiDef.guiClosed(this);
    }

    @Override
    public boolean performAction(IGuiElement element, int action) {
        return false;
    }

    @Override
    public GuiScreen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.posX;
    }

    @Override
    public int getScreenPosY() {
        return this.posY;
    }

    public static int register(ResourceLocation lexiconFolder) {
        LEXICA.add(lexiconFolder);

        return LEXICA.indexOf(lexiconFolder);
    }

    private static ResourceLocation getResource(ResourceLocation folder, String path) {
        return new ResourceLocation(folder.getNamespace(), folder.getPath() + "/" + path);
    }

    public static class ContentPane
            extends ScrollArea
    {
        @Override
        public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
            return super.getElements(gui, elementData);
        }
    }

    public static class Page
    {
        public final ResourceLocation parent;
        public final ItemStack icon;
        public final String title;
        public final GuiElementInst[] pageElements;
        public final GuiElementInst[] globalElements;

        public Page(JsonObject json) {
            this.parent = JsonUtils.getLocation(json.get("parent"), null);
            this.icon = JsonUtils.getItemStack(json.get("icon"));
            this.title = JsonUtils.getStringVal(json.get("title"));

            this.pageElements = JsonUtils.GSON.fromJson(json.get("elements"), GuiElementInst[].class);
            if( json.has("globalElements") ) {
                this.globalElements = JsonUtils.GSON.fromJson(json.get("elements"), GuiElementInst[].class);
            } else {
                this.globalElements = null;
            }
        }
    }
}
