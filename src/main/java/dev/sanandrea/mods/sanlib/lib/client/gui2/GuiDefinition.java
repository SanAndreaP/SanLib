/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Empty;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Rectangle;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Text;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Texture;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "java:S1104", "java:S2386"})
public class GuiDefinition
        implements ISelectiveResourceReloadListener, IGuiReference
{
    public static final Map<ResourceLocation, Supplier<GuiElement>> TYPES = new HashMap<>();
    static {
        TYPES.put(Empty.ID, Empty::new);
        TYPES.put(Rectangle.ID, Rectangle::new);
        TYPES.put(Texture.ID, Texture::new);
        TYPES.put(Text.ID, Text::new);
    }

    public int width;
    public int height;
    private ResourceLocation texture;

    EnumMap<GuiElement.InputPriority, List<GuiElement>> foregroundElementPrios = new EnumMap<>(GuiElement.InputPriority.class);
    EnumMap<GuiElement.InputPriority, List<GuiElement>> backgroundElementPrios = new EnumMap<>(GuiElement.InputPriority.class);

    private final Map<String, GuiElement> backgroundElements = new TreeMap<>();
    private final Map<String, GuiElement> foregroundElements = new TreeMap<>();

    private final ResourceLocation     data;
    private Consumer<JsonObject> loadHandler;

    private final IGui gui;

    GuiDefinition(IGui gui, ResourceLocation data) throws IOException {
        this.gui = gui;
        this.data = data;

        this.register();
    }

    void register() throws IOException {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        this.reloadDefinition();
    }

    public static GuiDefinition getNewDefinition(IGui gui, ResourceLocation data) throws IOException {
        return new GuiDefinition(gui, data);
    }

    public GuiDefinition withLoadHandler(Consumer<JsonObject> handler) {
        this.loadHandler = handler;

        return this;
    }

    public static boolean initialize(GuiDefinition guiDef, IGui gui) {
        if( guiDef == null ) {
            gui.get().getMinecraft().setScreen(null);

            return false;
        }

        // first tick upon initialization
        guiDef.tick(gui);

        return true;
    }

    private void reloadDefinition() throws IOException {
        this.elementsAccept(e -> e.unload(gui), true);

        this.backgroundElements.clear();
        this.foregroundElements.clear();
        this.backgroundElementPrios.clear();
        this.foregroundElementPrios.clear();

        this.loadFile(this.data);

        this.elementsAccept(e -> e.load(gui), true);

        GuiElement.InputPriority.forEach(tgt -> {
            this.backgroundElementPrios.put(tgt, getPrioritizedElements(this.backgroundElements.values(), tgt));
            this.foregroundElementPrios.put(tgt, getPrioritizedElements(this.foregroundElements.values(), tgt));
        });
    }

    private void loadFile(ResourceLocation data) throws IOException {
        try( IResource r = Minecraft.getInstance().getResourceManager().getResource(data);
             InputStreamReader reader = new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8) )
        {
            JsonElement json = new JsonParser().parse(reader);
            if( !json.isJsonObject() ) {
                throw new IOException(String.format("Cannot read JSON of data-driven GUI %s as it isn't an object", data));
            }
            JsonObject jObj = json.getAsJsonObject();

            if( jObj.has("parent") ) {
                this.loadFile(new ResourceLocation(JsonUtils.getStringVal(jObj.get("parent"))));
            }

            this.width = JsonUtils.getIntVal(jObj.get("width"), this.width);
            this.height = JsonUtils.getIntVal(jObj.get("height"), this.height);
            if( jObj.has("texture") ) {
                this.texture = new ResourceLocation(jObj.get("texture").getAsString());
            }

            if( jObj.has("backgroundElements") ) {
                JsonUtils.GSON.fromJson(jObj.get("backgroundElements"), JsonObject.class).entrySet().forEach(e ->
                    this.backgroundElements.put(e.getKey(), this.loadElement(e))
                );
            }
            if( jObj.has("foregroundElements") ) {
                JsonUtils.GSON.fromJson(jObj.get("foregroundElements"), JsonObject.class).entrySet().forEach(e ->
                    this.foregroundElements.put(e.getKey(), this.loadElement(e))
                );
            }

            if( this.loadHandler != null ) {
                this.loadHandler.accept(jObj);
            }
        }
    }

    private GuiElement loadElement(Map.Entry<String, JsonElement> e) {
        JsonObject v    = e.getValue().getAsJsonObject();
        String     type = JsonUtils.getStringVal(v.get("type"));
        if( type == null ) {
            SanLib.LOG.warn("Element '{}' has no type defined", e.getKey());
            type = Empty.ID.toString();
        }

        Supplier<GuiElement> s = TYPES.get(new ResourceLocation(type));
        if( s == null ) {
            SanLib.LOG.warn("Unknown type '{}' for element '{}'", type, e.getKey());
            s = TYPES.get(Empty.ID);
        }

        GuiElement element = s.get();
        try {
            element.loadFromJson(this.gui, this, v);
        } catch( Exception ex ) {
            SanLib.LOG.warn("Error loading element '{}'", e.getKey(), ex);
            element = new Empty();
        }

        return element;
    }

    public ResourceLocation getTexture(JsonElement texture) {
        if( texture != null ) {
            return new ResourceLocation(texture.getAsString());
        }

        return this.texture;
    }

    @SuppressWarnings("java:S107")
    public static void renderElement(IGui gui, MatrixStack matrixStack, int offsetX, int offsetY, double mouseX, double mouseY, float partialTicks, GuiElement e) {
        if( e.isVisible() ) {
            offsetX += e.getPosX();
            offsetY += e.getPosY();

            switch( e.getHorizontalAlignment() ) {
                case RIGHT: offsetX -= e.getWidth(); break;
                case CENTER: offsetX -= e.getWidth() / 2; break;
                default: break;
            }
            switch( e.getVerticalAlignment() ) {
                case BOTTOM: offsetY -= e.getHeight(); break;
                case CENTER: offsetY -= e.getHeight() / 2; break;
                default: break;
            }

            e.render(gui, matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        }
    }

    public void drawBackground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(gui, stack, 0, 0, mouseX, mouseY, partialTicks);
    }

    public void drawBackground(IGui gui, MatrixStack stack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        this.backgroundElements.forEach((k, e) -> renderElement(gui, stack, offsetX, offsetY, mouseX, mouseY, partialTicks, e));
    }

    public void drawBackgroundContainer(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(gui, stack, gui.getPosX(), gui.getPosY(), mouseX, mouseY, partialTicks);
    }

    public void drawForeground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.drawForeground(gui, stack, 0, 0, mouseX, mouseY, partialTicks);
    }

    public void drawForeground(IGui gui, MatrixStack stack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        this.foregroundElements.forEach((k, e) -> renderElement(gui, stack, offsetX, offsetY, mouseX, mouseY, partialTicks, e));
    }

    private static List<GuiElement> getPrioritizedElements(Collection<GuiElement> elements, GuiElement.InputPriority target) {
        return elements.stream().sorted(Comparator.comparing(e -> getPriority(e, target))).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.elementsTest(e -> e.mouseClicked(gui, mouseX, mouseY, button), GuiElement.InputPriority.MOUSE_INPUT);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) {
        return this.elementsTest(e -> e.mouseScrolled(gui, mouseX, mouseY, scroll), GuiElement.InputPriority.MOUSE_INPUT);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.elementsTest(e -> e.mouseReleased(gui, mouseX, mouseY, button), GuiElement.InputPriority.MOUSE_INPUT);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.elementsTest(e -> e.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY), GuiElement.InputPriority.MOUSE_INPUT);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.elementsTest(e -> e.keyPressed(gui, keyCode, scanCode, modifiers), GuiElement.InputPriority.KEY_INPUT);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.elementsTest(e -> e.keyReleased(gui, keyCode, scanCode, modifiers), GuiElement.InputPriority.KEY_INPUT);
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.elementsTest(e -> e.charTyped(gui, typedChar, keyCode), GuiElement.InputPriority.KEY_INPUT);
    }

    @Override
    public void onClose(IGui gui) {
        elementsAccept(e -> e.onClose(gui));
    }

    void elementsAccept(Consumer<GuiElement> execElem) {
        this.elementsAccept(execElem, false);
    }

    void elementsAccept(Consumer<GuiElement> execElem, boolean forceInvisible) {
        this.backgroundElements.forEach((id, elem) -> { if( forceInvisible || elem.isVisible() ) execElem.accept(elem); });
        this.foregroundElements.forEach((id, elem) -> { if( forceInvisible || elem.isVisible() ) execElem.accept(elem); });
    }

    boolean elementsTest(Predicate<GuiElement> execElem, GuiElement.InputPriority target) {
        Collection<GuiElement> bgElem = target == GuiElement.InputPriority.NONE ? this.backgroundElements.values() : this.backgroundElementPrios.get(target);
        for( GuiElement elem : bgElem ) {
            if( elem.isVisible() && execElem.test(elem) ) return true;
        }
        Collection<GuiElement> fgElem = target == GuiElement.InputPriority.NONE ? this.backgroundElements.values() : this.backgroundElementPrios.get(target);
        for( GuiElement elem : fgElem ) {
            if( elem.isVisible() && execElem.test(elem) ) return true;
        }

        return false;
    }

    private static EventPriority getPriority(GuiElement elem, GuiElement.InputPriority target) {
        GuiElement.Priorities pAnnotation = elem.getClass().getAnnotation(GuiElement.Priorities.class);
        if( pAnnotation == null ) {
            return EventPriority.NORMAL;
        }

        GuiElement.Priority[] priorities = pAnnotation.value();
        for( GuiElement.Priority priority : priorities ) {
            if( priority.target() == target ) {
                return priority.value();
            }
        }

        return EventPriority.NORMAL;
    }

    public GuiElement getElementById(String id) {
        return MiscUtils.get(this.backgroundElements.get(id), () -> this.foregroundElements.get(id));
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
        try {
            this.reloadDefinition();
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Error whilst reloading GUI definition", ex);
        }
    }

    public void tick(IGui gui) {
        BiConsumer<String, GuiElement> f = (k, e) -> {
            if( e.isVisible() ) {
                e.tick(gui);
            }
        };
        this.backgroundElements.forEach(f);
        this.foregroundElements.forEach(f);
    }

}
