package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.sanandrew.mods.sanlib.SanLib;
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
        TYPES.put(TextGuiElement.ID, TextGuiElement::new);
        TYPES.put(TextureGuiElement.ID, TextureGuiElement::new);
        TYPES.put(RectangleGuiElement.ID, RectangleGuiElement::new);
        TYPES.put(ScrollAreaGuiElement.ID, ScrollAreaGuiElement::new);
        TYPES.put(ContainerNameGuiElement.ID, ContainerNameGuiElement::new);
    }

    public int width;
    public int height;

    GuiElementInst[] foregroundElements;
    GuiElementInst[] backgroundElements;

    private Map<Integer, Button> buttons;

    private final ResourceLocation data;

    private GuiDefinition(ResourceLocation data) throws IOException {
        this.data = data;
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        this.reloadDefinition();
    }

    public static GuiDefinition getNewDefinition(ResourceLocation data) throws IOException {
        return new GuiDefinition(data);
    }

    private void reloadDefinition() throws IOException {
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

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
        try {
            this.reloadDefinition();
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Error whilst reloading GUI definition", ex);
        }
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
