package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonTextLabel;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Rectangle;
import de.sanandrew.mods.sanlib.lib.client.gui.element.RedstoneFluxBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.RedstoneFluxText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({ "unused", "WeakerAccess", "MismatchedQueryAndUpdateOfCollection", "UnusedReturnValue" })
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
        TYPES.put(Label.ID, Label::new);
        TYPES.put(RedstoneFluxBar.ID, RedstoneFluxBar::new);
        TYPES.put(RedstoneFluxText.ID, RedstoneFluxText::new);
        TYPES.put(DynamicText.ID, DynamicText::new);
        TYPES.put(Button.ID, Button::new);
        TYPES.put(ButtonTextLabel.ID, ButtonTextLabel::new);
        TYPES.put(TextField.ID, TextField::new);
    }

    public int width;
    public int height;
    private ResourceLocation texture;

    GuiElementInst[] foregroundElements;
    GuiElementInst[] backgroundElements;

    EnumMap<IGuiElement.PriorityTarget, GuiElementInst[]> prioritizedFgElements = new EnumMap<>(IGuiElement.PriorityTarget.class);
    EnumMap<IGuiElement.PriorityTarget, GuiElementInst[]> prioritizedBgElements = new EnumMap<>(IGuiElement.PriorityTarget.class);

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
            this.texture = jObj.has("texture") ? new ResourceLocation(jObj.get("texture").getAsString()) : null;

            this.backgroundElements = JsonUtils.GSON.fromJson(jObj.get("backgroundElements"), GuiElementInst[].class);
            this.foregroundElements = JsonUtils.GSON.fromJson(jObj.get("foregroundElements"), GuiElementInst[].class);

            Arrays.stream(this.backgroundElements).forEach(this::initElement);
            Arrays.stream(this.foregroundElements).forEach(this::initElement);

            for( IGuiElement.PriorityTarget tgt : IGuiElement.PriorityTarget.VALUES ) {
                this.prioritizedBgElements.put(tgt, getPrioritizedElements(this.backgroundElements, tgt));
                this.prioritizedFgElements.put(tgt, getPrioritizedElements(this.foregroundElements, tgt));
            }
        }
    }

    public void initElement(GuiElementInst e) {
        if( e != null ) {
            if( !Strings.isNullOrEmpty(e.id) ) this.idToElementMap.put(e.id, e);
            if( e.data == null ) e.data = new JsonObject();
        }
    }

    public ResourceLocation getTexture(JsonElement texture) {
        if( texture != null ) {
            return new ResourceLocation(texture.getAsString());
        }

        return this.texture;
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

    private static GuiElementInst[] getPrioritizedElements(GuiElementInst[] elements, IGuiElement.PriorityTarget target) {
        return Arrays.stream(elements).sorted(Comparator.comparing(e -> getPriority(e, target))).toArray(GuiElementInst[]::new);
    }

    public void handleMouseInput(IGui gui) throws IOException {
        for( GuiElementInst e : this.prioritizedBgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().handleMouseInput(gui);
        }
        for( GuiElementInst e : this.prioritizedFgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().handleMouseInput(gui);
        }
    }

    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        for( GuiElementInst e : this.prioritizedBgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            if( e.get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }
        for( GuiElementInst e : this.prioritizedFgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            if( e.get().mouseClicked(gui, mouseX, mouseY, mouseButton) ) {
                return true;
            }
        }

        return false;
    }

    public void mouseReleased(IGui gui, int mouseX, int mouseY, int state) {
        for( GuiElementInst e : this.prioritizedBgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().mouseReleased(gui, mouseX, mouseY, state);
        }
        for( GuiElementInst e : this.prioritizedFgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().mouseReleased(gui, mouseX, mouseY, state);
        }
    }

    public void mouseClickMove(IGui gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for( GuiElementInst e : this.prioritizedBgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().mouseClickMove(gui, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
        for( GuiElementInst e : this.prioritizedFgElements.get(IGuiElement.PriorityTarget.MOUSE_INPUT) ) {
            e.get().mouseClickMove(gui, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    public boolean keyTyped(IGui gui, char typedChar, int keyCode) throws IOException {
        for( GuiElementInst e : this.prioritizedBgElements.get(IGuiElement.PriorityTarget.KEY_INPUT) ) {
            if( e.get().keyTyped(gui, typedChar, keyCode) ) {
                return true;
            }
        }
        for( GuiElementInst e : this.prioritizedFgElements.get(IGuiElement.PriorityTarget.KEY_INPUT) ) {
            if( e.get().keyTyped(gui, typedChar, keyCode) ) {
                return true;
            }
        }

        return false;
    }

    private static EventPriority getPriority(GuiElementInst elem, IGuiElement.PriorityTarget target) {
        IGuiElement.Priorities pAnnotation = elem.getClass().getAnnotation(IGuiElement.Priorities.class);
        if( pAnnotation == null ) {
            return EventPriority.NORMAL;
        }

        IGuiElement.Priority[] priorities = pAnnotation.value();
        for( IGuiElement.Priority priority : priorities ) {
            if( priority.target() == target ) {
                return priority.value();
            }
        }

        return EventPriority.NORMAL;
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
}
