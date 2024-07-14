/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Button;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Empty;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.IElementContainer;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Rectangle;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.ScrollPanel;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.StackPanel;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Text;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.TextField;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Texture;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.TiledTexture;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.sanlib.lib.util.UuidUtils;
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
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "java:S1104", "java:S2386"})
public class GuiDefinition
        implements ISelectiveResourceReloadListener, IGuiReference, IElementContainer
{
    public static final GuiElement EMPTY = Empty.INSTANCE;

    public static final Map<ResourceLocation, Function<String, GuiElement>> TYPES = new HashMap<>();
    static {
        TYPES.put(Empty.ID, id -> Empty.INSTANCE);
        TYPES.put(Rectangle.ID, Rectangle::new);
        TYPES.put(Texture.ID, Texture::new);
        TYPES.put(Text.ID, Text::new);
        TYPES.put(StackPanel.ID, StackPanel::new);
        TYPES.put(ScrollPanel.ID, ScrollPanel::new);
        TYPES.put(TiledTexture.ID, TiledTexture::new);
        TYPES.put(Button.ID, Button::new);
        TYPES.put(TextField.ID, TextField::new);
    }

    public int width;
    public int height;
    private ResourceLocation texture;

    EnumMap<GuiElement.InputPriority, List<GuiElement>> foregroundElementPrios = new EnumMap<>(GuiElement.InputPriority.class);
    EnumMap<GuiElement.InputPriority, List<GuiElement>> backgroundElementPrios = new EnumMap<>(GuiElement.InputPriority.class);

    private final Map<String, GuiElement> backgroundElements = new TreeMap<>();
    private final Map<String, GuiElement> foregroundElements = new TreeMap<>();
    private final List<IElementContainer> childContainers = new ArrayList<>();

    private final ResourceLocation     data;
    private Consumer<JsonObject> loadHandler;

    private final IGui gui;

    private final UUID currentInstID = UUID.randomUUID();

    private GuiElement currentFocus = null;

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
        this.childContainers.clear();

        this.loadFile(this.data);

        this.elementsAccept(e -> e.load(gui), true);

        this.updatePriorities(true, true);
    }

    private void updatePriorities(boolean updateBg, boolean updateFg) {
        if( updateBg ) this.backgroundElementPrios.clear();
        if( updateFg ) this.foregroundElementPrios.clear();

        GuiElement.InputPriority.forEach(tgt -> {
            if( updateBg ) this.backgroundElementPrios.put(tgt, getPrioritizedElements(this.backgroundElements.values(), tgt));
            if( updateFg ) this.foregroundElementPrios.put(tgt, getPrioritizedElements(this.foregroundElements.values(), tgt));
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

    public GuiElement loadElement(String id, JsonObject data) {
        JsonObject v    = data.getAsJsonObject();
        String     type = JsonUtils.getStringVal(v.get("type"));
        if( type == null ) {
            SanLib.LOG.warn("Element '{}' has no type defined", id);
            type = Empty.ID.toString();
        }

        Function<String, GuiElement> s = TYPES.get(new ResourceLocation(type));
        if( s == null ) {
            SanLib.LOG.warn("Unknown type '{}' for element '{}'", type, id);
            s = TYPES.get(Empty.ID);
        }

        GuiElement element = s.apply(id);
        try {
            element.loadFromJson(this.gui, this, v);
        } catch( Exception ex ) {
            SanLib.LOG.warn("Error loading element '{}'", id, ex);
            element = Empty.INSTANCE;
        }

        return element;
    }

    private GuiElement loadElement(Map.Entry<String, JsonElement> e) {
        GuiElement element = loadElement(e.getKey(), e.getValue().getAsJsonObject());

        if( element instanceof IElementContainer ) {
            this.childContainers.add((IElementContainer) element);
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

            int eWidth = e.getWidth();
            int eHeight = e.getHeight();

            switch( e.getHorizontalAlignment() ) {
                case RIGHT: offsetX -= eWidth; break;
                case CENTER: offsetX -= eWidth / 2; break;
                default: break;
            }
            switch( e.getVerticalAlignment() ) {
                case BOTTOM: offsetY -= eHeight; break;
                case CENTER: offsetY -= eHeight / 2; break;
                default: break;
            }

            e.updateHovering(gui, offsetX, offsetY, mouseX, mouseY);
            e.render(gui, matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
            e.renderDebug(gui, matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks, 0);
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
        Collection<GuiElement> fgElem = target == GuiElement.InputPriority.NONE ? this.foregroundElements.values() : this.foregroundElementPrios.get(target);
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

    @Override
    public GuiElement putElement(String id, GuiElement child) {
        throw new UnsupportedOperationException("This container has multiple types of child containers. Use \"putBackgroundElement\" or \"putForegroundElement\".");
    }

    public GuiElement putBackgroundElement(String id, GuiElement child) {
        GuiElement prev = this.backgroundElements.put(id, child);
        if( prev instanceof IElementContainer ) {
            this.childContainers.remove((IElementContainer) prev);
        }
        if( child instanceof IElementContainer ) {
            this.childContainers.add((IElementContainer) child);
        }

        this.updatePriorities(true, false);

        return prev;
    }

    public GuiElement putForegroundElement(String id, GuiElement child) {
        GuiElement prev = this.foregroundElements.put(id, child);
        if( prev instanceof IElementContainer ) {
            this.childContainers.remove((IElementContainer) prev);
        }
        if( child instanceof IElementContainer ) {
            this.childContainers.add((IElementContainer) child);
        }

        this.updatePriorities(false, true);

        return prev;
    }

    public void setFocusedElement(GuiElement element) {
        if( element == null || element.isFocusable ) {
            MiscUtils.accept(this.currentFocus, e -> {
                e.setFocus(this, false, this.currentInstID);
                e.onFocusChange(false);
            });
            this.currentFocus = element;
            MiscUtils.accept(element, e -> {
                e.setFocus(this, true, this.currentInstID);
                e.onFocusChange(true);
            });
        }
    }

    @Override
    public GuiElement getElement(String id, boolean recursive) {
        GuiElement elem = MiscUtils.get(this.backgroundElements.get(id), () -> this.foregroundElements.get(id));
        if( elem == null && recursive ) {
            return MiscUtils.getFirst(this.childContainers, c -> c.getElement(id), Objects::nonNull);
        }

        return elem;
    }

    public GuiElement getFocusedElement() {
        return this.currentFocus;
    }

    @Override
    public String getElementId(GuiElement child) {
        for( Map.Entry<String, GuiElement> e : this.backgroundElements.entrySet() ) {
            if( Objects.equals(e.getValue(), child) ) {
                return e.getKey();
            }
        }
        for( Map.Entry<String, GuiElement> e : this.foregroundElements.entrySet() ) {
            if( Objects.equals(e.getValue(), child) ) {
                return e.getKey();
            }
        }

        return null;
    }

    @Override
    public GuiElement removeElement(String id) {
        throw new UnsupportedOperationException("This container has multiple types of child containers. Use \"removeBackgroundElement\" or \"removeForegroundElement\".");
    }

    public GuiElement removeBackgroundElement(String id) {
        GuiElement prev = this.backgroundElements.remove(id);
        if( prev instanceof IElementContainer ) {
            this.childContainers.remove((IElementContainer) prev);
        }

        this.updatePriorities(true, false);

        return prev;
    }

    public GuiElement removeForegroundElement(String id) {
        GuiElement prev = this.foregroundElements.remove(id);
        if( prev instanceof IElementContainer ) {
            this.childContainers.remove((IElementContainer) prev);
        }

        this.updatePriorities(true, false);

        return prev;
    }

    @Override
    public void clear() {
        this.backgroundElements.clear();
        this.foregroundElements.clear();
        this.childContainers.clear();

        this.updatePriorities(true, true);
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
        try {
            this.reloadDefinition();
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Error whilst reloading GUI definition", ex);
        }
    }

    @Override
    public void tick(IGui gui) {
        BiConsumer<String, GuiElement> f = (k, e) -> {
            if( e.isVisible() ) {
                e.tick(gui);
            }
        };
        this.backgroundElements.forEach(f);
        this.foregroundElements.forEach(f);
    }

    @Override
    public boolean isImmutable() {
        return true;
    }

    public boolean checkID(UUID defID) {
        return UuidUtils.areUuidsEqual(this.currentInstID, defID);
    }
}
