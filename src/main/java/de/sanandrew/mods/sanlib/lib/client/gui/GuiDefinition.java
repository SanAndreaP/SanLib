package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.element.*;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "WeakerAccess", "MismatchedQueryAndUpdateOfCollection"})
public class GuiDefinition
        implements ISelectiveResourceReloadListener
{
    public static final Map<ResourceLocation, Supplier<IGuiElement>> TYPES = new HashMap<>();
    static {
        TYPES.put(Text.ID, Text::new);
        TYPES.put(Texture.ID, Texture::new);
        TYPES.put(Rectangle.ID, Rectangle::new);
        TYPES.put(ScrollArea.ID, ScrollArea::new);
        TYPES.put(ContainerName.ID, ContainerName::new);
        TYPES.put(RedstoneFluxBar.ID, RedstoneFluxBar::new);
        TYPES.put(RedstoneFluxLabel.ID, RedstoneFluxLabel::new);
        TYPES.put(DynamicText.ID, DynamicText::new);
    }

    public int width;
    public int height;

    GuiElementInst[] foregroundElements;
    GuiElementInst[] backgroundElements;

    private Map<String, GuiElementInst> idToElementMap;

    private Map<Integer, Button> buttons;

    private final ResourceLocation data;

    private GuiDefinition(ResourceLocation data) throws IOException {
        this.data = data;
        this.idToElementMap = new HashMap<>();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        this.reloadDefinition();
    }

    public static GuiDefinition getNewDefinition(ResourceLocation data) throws IOException {
        return new GuiDefinition(data);
    }

    private void reloadDefinition() throws IOException {
        this.idToElementMap.clear();

        try( IResource r = Minecraft.getMinecraft().getResourceManager().getResource(this.data);
             InputStreamReader reader = new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8) )
        {
            JsonElement json = new JsonParser().parse(reader);
            if( !json.isJsonObject() ) {
                throw new IOException(String.format("Cannot read JSON of data-driven GUI %s as it isn't an object", this.data));
            }
            JsonObject jObj = json.getAsJsonObject();

            this.width = JsonUtils.getIntVal(jObj.get("width"));
            this.height = JsonUtils.getIntVal(jObj.get("height"));

            this.backgroundElements = JsonUtils.GSON.fromJson(jObj.get("backgroundElements"), GuiElementInst[].class);
            this.foregroundElements = JsonUtils.GSON.fromJson(jObj.get("foregroundElements"), GuiElementInst[].class);

            Arrays.stream(this.backgroundElements).forEach(e -> { if( !Strings.isNullOrEmpty(e.id) ) this.idToElementMap.put(e.id, e); });
            Arrays.stream(this.foregroundElements).forEach(e -> { if( !Strings.isNullOrEmpty(e.id) ) this.idToElementMap.put(e.id, e); });
        }
    }

    public void initGui(IGui gui) {
        Consumer<GuiElementInst> f = e -> e.get().bakeData(gui, e.data);
        Arrays.stream(this.backgroundElements).forEach(f);
        Arrays.stream(this.foregroundElements).forEach(f);
    }

    public void drawBackground(IGui gui, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.backgroundElements).forEach(e -> e.get().render(gui, partialTicks, e.pos[0], e.pos[1], mouseX, mouseY, e.data));
    }

    public void drawForeground(IGui gui, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.foregroundElements).forEach(e -> e.get().render(gui, partialTicks, e.pos[0], e.pos[1], mouseX, mouseY, e.data));
    }

    public void handleMouseInput(IGui gui) throws IOException {
        Consumer<GuiElementInst> f = e -> {
            try {
                e.get().handleMouseInput(gui);
            } catch( IOException ex ) {
                throw new IOExceptionWrapper(ex);
            }
        };

        try {
            Arrays.stream(this.backgroundElements).forEach(f);
            Arrays.stream(this.foregroundElements).forEach(f);
        } catch( IOExceptionWrapper ex ) {
            throw ex.ioex;
        }
    }

    public void mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        Consumer<GuiElementInst> f = e -> {
            try {
                e.get().mouseClicked(gui, mouseX, mouseY, mouseButton);
            } catch( IOException ex ) {
                throw new IOExceptionWrapper(ex);
            }
        };

        try {
            Arrays.stream(this.backgroundElements).forEach(f);
            Arrays.stream(this.foregroundElements).forEach(f);
        } catch( IOExceptionWrapper ex ) {
            throw ex.ioex;
        }
    }

    public void mouseReleased(IGui gui, int mouseX, int mouseY, int state) {
        Consumer<GuiElementInst> f = e -> e.get().mouseReleased(gui, mouseX, mouseY, state);

        Arrays.stream(this.backgroundElements).forEach(f);
        Arrays.stream(this.foregroundElements).forEach(f);
    }

    public void mouseClickMove(IGui gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        Consumer<GuiElementInst> f = e -> e.get().mouseClickMove(gui, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        Arrays.stream(this.backgroundElements).forEach(f);
        Arrays.stream(this.foregroundElements).forEach(f);
    }

    public GuiButton injectData(GuiButton button) {
        Button btn = this.buttons == null ? null : this.buttons.get(button.id);

        if( btn != null ) {
            button.x = btn.x;
            button.y = btn.y;
            button.width = btn.width;
            button.height = btn.height;
        }

        return button;
    }

    public GuiElementInst getElementById(String id) {
        return this.idToElementMap.get(id);
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
        try {
            this.reloadDefinition();
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Error whilst reloading GUI definition", ex);
        }
    }

    public void update(IGui gui) {
        Consumer<GuiElementInst> f = e -> e.get().update(gui, e.data);
        Arrays.stream(this.backgroundElements).forEach(f);
        Arrays.stream(this.foregroundElements).forEach(f);
    }

    static final class Button
    {
        int x;
        int y;
        int width;
        int height;
    }

    @SuppressWarnings("ExceptionClassNameDoesntEndWithException")
    private static class IOExceptionWrapper
            extends RuntimeException
    {
        private static final long serialVersionUID = 8878021439168468744L;

        public final IOException ioex;

        IOExceptionWrapper(IOException ex) {
            this.ioex = ex;
        }
    }
}
