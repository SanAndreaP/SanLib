package de.sanandrew.mods.sanlib.client.lexicon2;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiLexicon
        extends GuiScreen
        implements IGui
{
    private static final List<Lexicon> LEXICA = new ArrayList<>();

    private final GuiDefinition guiDef;

    public GuiElementInst contentPane;

    protected int posX;
    protected int posY;

    static {
        GuiDefinition.TYPES.put(new ResourceLocation(Constants.ID, "lexicon_content_area"), ContentPane::new);
    }

    public GuiLexicon(int lexiconId) throws IOException {
        this.guiDef = LEXICA.get(lexiconId).load();

        this.contentPane = this.guiDef.getElementById("contentPane");
        if( this.contentPane == null || !(this.contentPane.get() instanceof ContentPane) ) {
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
        Lexicon reg = new Lexicon(lexiconFolder);
        LEXICA.add(reg);

        return LEXICA.indexOf(reg);
    }

    private static ResourceLocation getResource(ResourceLocation folder, String path) {
        return new ResourceLocation(folder.getNamespace(), folder.getPath() + "/" + path);
    }

    public static class ContentPane
            extends ScrollArea
    {
        private GuiElementInst[] elements = new GuiElementInst[0];

        @Override
        public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
            return elements;
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

    private static class Lexicon
    {
        private final ResourceLocation path;
        private final Map<ResourceLocation, Page> pages = new HashMap<>();

        private Page currentPage;

        Lexicon(ResourceLocation path) {
            this.path = path;
        }

        private GuiDefinition load() throws IOException {
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            Set<ResourceLocation> pages = new HashSet<>();
            GuiDefinition         def   = GuiDefinition.getNewDefinition(getResource(this.path, "lexicon.json"));

            def.width = 192;
            def.height = 236;

            List<IResource> pagesDefs = null;
            try
            {
                pagesDefs = Lists.reverse(resourceManager.getAllResources(getResource(this.path, "pages.json")));
                for( IResource r : pagesDefs ) {
                    try( InputStreamReader reader = new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8) ) {
                        JsonElement json = new JsonParser().parse(reader);
                        if( json.isJsonArray() ) {
                            Arrays.stream(JsonUtils.getStringArray(json)).forEach(s -> {
                                ResourceLocation pageLocation = new ResourceLocation(s);
                                try( IResource rs = resourceManager.getResource(pageLocation);
                                     InputStream is = rs.getInputStream();
                                     InputStreamReader isr = new InputStreamReader(is) )
                                {
                                    JsonObject jObj = JsonUtils.GSON.fromJson(isr, JsonObject.class);
                                    this.pages.computeIfAbsent(pageLocation, rp -> new Page(jObj));
                                } catch( IOException e ) {
                                    SanLib.LOG.log(Level.ERROR, "Cannot load page", e);
                                }
                            });
                        }
                    }
                }
            } finally {
                if( pagesDefs != null ) {
                    for( IResource r : pagesDefs ) {
                        r.close();
                    }
                }
            }



            return def;
        }
    }
}
