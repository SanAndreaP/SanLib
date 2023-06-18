/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "java:S1104", "java:S2386"})
public class GuiDefinition
        implements ISelectiveResourceReloadListener, IGuiReference
{
    public static final Map<ResourceLocation, Supplier<GuiElement>> TYPES = new HashMap<>();
    static {
    }

    public int width;
    public int height;
    private ResourceLocation texture;

    EnumMap<GuiElement.PriorityTarget, GuiElement[]> foregroundElementPrios = new EnumMap<>(GuiElement.PriorityTarget.class);
    EnumMap<GuiElement.PriorityTarget, GuiElement[]> backgroundElementPrios = new EnumMap<>(GuiElement.PriorityTarget.class);

    private final Map<String, GuiElement> backgroundElements = new HashMap<>();
    private final Map<String, GuiElement> foregroundElements = new HashMap<>();

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

        GuiElement.PriorityTarget.forEach(tgt -> {
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

            JsonUtils.GSON.fromJson(jObj.get("backgroundElements"), JsonObject.class).entrySet().forEach(e ->
                this.backgroundElements.put(e.getKey(), this.loadElement(e))
            );
            JsonUtils.GSON.fromJson(jObj.get("foregroundElements"), JsonObject.class).entrySet().forEach(e ->
                this.foregroundElements.put(e.getKey(), this.loadElement(e))
            );

            if( this.loadHandler != null ) {
                this.loadHandler.accept(jObj);
            }
        }
    }

    private GuiElement loadElement(Map.Entry<String, JsonElement> e) {
        JsonObject           v       = e.getValue().getAsJsonObject();
        GuiElement           element = TYPES.get(new ResourceLocation(JsonUtils.getStringVal(v.get("type")))).get();

        element.loadFromJson(this.gui, v);

        return element;
    }

    public ResourceLocation getTexture(JsonElement texture) {
        if( texture != null ) {
            return new ResourceLocation(texture.getAsString());
        }

        return this.texture;
    }

    public static void renderElement(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks, GuiElementInst e) {
        renderElement(gui, stack, e.pos[0], e.pos[1], mouseX, mouseY, partialTicks, e, true);
    }

    public static void renderElement(IGui gui, MatrixStack stack, int x, int y, double mouseX, double mouseY, float partialTicks, GuiElementInst e) {
        renderElement(gui, stack, x, y, mouseX, mouseY, partialTicks, e, true);
    }

    public static void renderElement(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks, GuiElementInst e, boolean doRenderTick) {
        renderElement(gui, stack, e.pos[0], e.pos[1], mouseX, mouseY, partialTicks, e, doRenderTick);
    }

    public static void renderElement(IGui gui, MatrixStack stack, int x, int y, double mouseX, double mouseY, float partialTicks, GuiElementInst e, boolean doRenderTick) {
        IGuiElement ie = e.get();
        if( e.isVisible() ) {
            if( doRenderTick ) {
                ie.renderTick(gui, stack, partialTicks, x, y, mouseX, mouseY, e);
            }

            switch( e.getAlignmentH() ) {
                case RIGHT: x -= ie.getWidth(); break;
                case CENTER: x -= ie.getWidth() / 2; break;
                default: break;
            }
            switch( e.getAlignmentV() ) {
                case BOTTOM: y -= ie.getHeight(); break;
                case CENTER: y -= ie.getHeight() / 2; break;
                default: break;
            }

            ie.render(gui, stack, partialTicks, x, y, mouseX, mouseY, e);
        }
    }

    public void drawBackground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.backgroundElements).forEach(e -> renderElement(gui, stack, mouseX, mouseY, partialTicks, e));
    }

    public void drawBackgroundContainer(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        stack.pushPose();
        stack.translate(gui.getScreenPosX(), gui.getScreenPosY(), 0.0F);
        this.drawBackground(gui, stack, mouseX, mouseY, partialTicks);
        stack.popPose();
    }

    public void drawForeground(IGui gui, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.foregroundElements).forEach(e -> renderElement(gui, stack, mouseX, mouseY, partialTicks, e));
    }

    private static GuiElement[] getPrioritizedElements(Collection<GuiElement> elements, GuiElement.PriorityTarget target) {
        return elements.stream().sorted(Comparator.comparing(e -> getPriority(e, target))).toArray(GuiElement[]::new);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return doWorkB(e -> e.mouseClicked(gui, mouseX, mouseY, button), IGuiElement.PriorityTarget.MOUSE_INPUT);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double scroll) {
        return doWorkB(e -> e.mouseScrolled(gui, mouseX, mouseY, scroll), IGuiElement.PriorityTarget.MOUSE_INPUT);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return doWorkB(e -> e.mouseReleased(gui, mouseX, mouseY, button), IGuiElement.PriorityTarget.MOUSE_INPUT);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return doWorkB(e -> e.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY), IGuiElement.PriorityTarget.MOUSE_INPUT);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return doWorkB(e -> e.keyPressed(gui, keyCode, scanCode, modifiers), IGuiElement.PriorityTarget.KEY_INPUT);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return doWorkB(e -> e.keyReleased(gui, keyCode, scanCode, modifiers), IGuiElement.PriorityTarget.KEY_INPUT);
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return doWorkB(e -> e.charTyped(gui, typedChar, keyCode), IGuiElement.PriorityTarget.KEY_INPUT);
    }

    @Override
    public void onClose(IGui gui) {
        doWorkV(e -> e.onClose(gui));
    }

    void elementsAccept(Consumer<GuiElement> execElem, boolean forceInvisible) {
        this.backgroundElements.forEach((id, elem) -> { if( forceInvisible || elem.isVisible() ) execElem.accept(elem); });
        this.foregroundElements.forEach((id, elem) -> { if( forceInvisible || elem.isVisible() ) execElem.accept(elem); });
    }

    boolean elementsTest(Predicate<GuiElement> execElem, boolean forceInvisible) {
        for( Map.Entry<String, GuiElement> elem : this.backgroundElements.entrySet() ) {
            if( (forceInvisible || elem.getValue().isVisible()) && execElem.test(elem.getValue()) ) return true;
        }
        for( Map.Entry<String, GuiElement> elem : this.foregroundElements.entrySet() ) {
            if( (forceInvisible || elem.getValue().isVisible()) && execElem.test(elem.getValue()) ) return true;
        }

        return false;
    }

//    boolean doWorkB(Predicate<IGuiElement> execElem, IGuiElement.PriorityTarget target) {
//        for( GuiElementInst e : (target != null ? this.backgroundElementPrios.get(target) : this.backgroundElements) ) {
//            if( e.isVisible() && execElem.test(e.get()) ) {
//                return true;
//            }
//        }
//        for( GuiElementInst e : (target != null ? this.foregroundElementPrios.get(target) : this.foregroundElements) ) {
//            if( e.isVisible() && execElem.test(e.get()) ) {
//                return true;
//            }
//        }
//
//        return false;
//    }

//    void doWorkV(Consumer<IGuiElement> execElem) {
//        for( GuiElementInst e : this.backgroundElements ) {
//            if( e.isVisible() ) {
//                execElem.accept(e.get());
//            }
//        }
//        for( GuiElementInst e : this.foregroundElements ) {
//            if( e.isVisible() ) {
//                execElem.accept(e.get());
//            }
//        }
//    }

    private static EventPriority getPriority(GuiElement elem, GuiElement.PriorityTarget target) {
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

    public GuiElementInst getElementById(String id) {
        return this.backgroundElements.get(id);
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
        Consumer<GuiElementInst> f = e -> {
            if( e.isVisible() ) {
                e.get().tick(gui, e);
            }
        };
        Arrays.stream(this.backgroundElements).forEach(f);
        Arrays.stream(this.foregroundElements).forEach(f);
    }

}
