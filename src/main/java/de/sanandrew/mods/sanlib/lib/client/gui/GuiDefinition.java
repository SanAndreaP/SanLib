package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "WeakerAccess", "MismatchedQueryAndUpdateOfCollection"})
public class GuiDefinition
        implements ISelectiveResourceReloadListener
{
    public int width;
    public int height;

    GuiElement[] foregroundElements;
    GuiElement[] backgroundElements;

    private Map<Integer, Button> buttons;

    private static final Map<ResourceLocation, Supplier<IGuiElement>> TYPES = new HashMap<>();
    static {
        TYPES.put(TextGuiElement.ID, TextGuiElement::new);
        TYPES.put(TextureGuiElement.ID, TextureGuiElement::new);
        TYPES.put(RectangleGuiElement.ID, RectangleGuiElement::new);
    }

    public static GuiDefinition getNewDefinition(ResourceLocation buildData) throws IOException {
        File src = Loader.instance().getIndexedModList().get(buildData.getNamespace()).getSource();
        File defFile;
        if( src.isFile() ) {
            try( FileSystem fs = FileSystems.newFileSystem(src.toPath(), null) ) {
                defFile = fs.getPath("/assets/" + buildData.getNamespace() + "/guis/" + buildData.getPath()).toFile();
            }
        } else if( src.isDirectory() ) {
            defFile = src.toPath().resolve("assets/" + buildData.getNamespace() + "/guis/" + buildData.getPath()).toFile();
        } else {
            throw new IOException("Cannot instanciate a data-driven GUI with an arbitrary mod directory!");
        }

        if( !defFile.isFile() ) {
            throw new IOException("Path to data-driven GUI definition is not a file!");
        }

        try( BufferedReader br = Files.newBufferedReader(defFile.toPath()) ) {
            GuiDefinition def = JsonUtils.fromJson(br, GuiDefinition.class);
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(def);
            return def;
        }
    }

    public void drawForeground(GuiScreen gui, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.foregroundElements).forEach(e -> e.get().render(gui, partialTicks, e.x, e.y, mouseX, mouseY, e.data));
    }

    public void drawBackground(GuiScreen gui, int mouseX, int mouseY, float partialTicks) {
        Arrays.stream(this.backgroundElements).forEach(e -> e.get().render(gui, partialTicks, e.x, e.y, mouseX, mouseY, e.data));
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
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        Arrays.stream(this.foregroundElements).forEach(e -> e.get().onResourceManagerReload(resourceManager, resourcePredicate));
        Arrays.stream(this.backgroundElements).forEach(e -> e.get().onResourceManagerReload(resourceManager, resourcePredicate));
    }

    static final class GuiElement
    {
        String type;
        int x;
        int y;
        JsonObject data;
        IGuiElement element;

        IGuiElement get() {
            if( this.element == null ) {
                Supplier<IGuiElement> cnst = TYPES.get(new ResourceLocation(this.type));
                if( cnst != null ) {
                    this.element = cnst.get();
                } else {
                    SanLib.LOG.log(Level.ERROR, String.format("A GUI Definition uses an unknown type %s for an element!", this.type));
                    this.element = new EmptyGuiElement();
                }
            }

            return this.element;
        }
    }

    static final class Button
    {
        int x;
        int y;
        int width;
        int height;
    }
}
