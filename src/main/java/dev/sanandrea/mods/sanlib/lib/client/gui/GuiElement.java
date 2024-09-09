package dev.sanandrea.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.EventPriority;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class GuiElement
        implements IGuiReference
{
    protected String id;

    protected int posX = 0;
    protected int posY = 0;
    protected Alignment hAlignment = Alignment.LEFT;
    protected Alignment vAlignment = Alignment.TOP;
    protected int width = 0;
    protected int height = 0;

    private boolean isVisible = true;
    private boolean isEnabled = true;

    protected boolean isHovering = false;
    protected boolean isFocused = false;

    public final boolean isResizable = this.getClass().isAnnotationPresent(Resizable.class);
    public final boolean isFocusable = this.getClass().isAnnotationPresent(Focusable.class);

    protected final List<Runnable> geometryListeners = new ArrayList<>();

    protected GuiElement(String id) {
        this(id, 0, 0, 0, 0, Alignment.LEFT, Alignment.TOP);
    }

    protected GuiElement(String id, int posX, int posY, int width, int height, Alignment hAlignment, Alignment vAlignment) {
        this.id = id;

        this.posX = posX;
        this.posY = posY;
        this.hAlignment = hAlignment;
        this.vAlignment = vAlignment;
        this.width = width;
        this.height = height;
    }

    public void addGeometryChangeListener(@Nonnull Runnable listener) {
        this.geometryListeners.add(listener);
    }

    public void removeGeometryChangeListener(@Nonnull Runnable listener) {
        this.geometryListeners.remove(listener);
    }

    public void load(IGui gui) { }

    public void unload(IGui gui) { }

    public abstract void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks);

    public void loadFromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.setPosX(JsonUtils.getIntVal(data.get("x"), 0));
        this.setPosY(JsonUtils.getIntVal(data.get("y"), 0));
        this.setSize(JsonUtils.getIntVal(data.get("width"), 0), JsonUtils.getIntVal(data.get("height"), 0));
        this.setHorizontalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("horizontalAlign"), Alignment.LEFT.toString())));
        this.setVerticalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("verticalAlign"), Alignment.TOP.toString())));

        this.setVisible(JsonUtils.getBoolVal(data.get("visible"), true));

        this.fromJson(gui, guiDef, data);
    }

    public void renderDebug(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks, int level) {
        int maxX = x + this.getWidth();
        int           maxY          = y + this.getHeight();
        Matrix4f      pose          = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);

        int color = ColorObj.fromHSLA(Mth.clamp(level * 360.0F / 10.0F, 0, 360.0F), 1.0F, 0.5F, 1.0F).getColorInt();

        bufferBuilder.addVertex(pose, x,    y,    0.0F).setColor(color);
        bufferBuilder.addVertex(pose, maxX, y,    0.0F).setColor(color);
        bufferBuilder.addVertex(pose, maxX, y,    0.0F).setColor(color);
        bufferBuilder.addVertex(pose, maxX, maxY, 0.0F).setColor(color);
        bufferBuilder.addVertex(pose, maxX, maxY, 0.0F).setColor(color);
        bufferBuilder.addVertex(pose, x,    maxY, 0.0F).setColor(color);
        bufferBuilder.addVertex(pose, x,    maxY, 0.0F).setColor(color);
        bufferBuilder.addVertex(pose, x,    y,    0.0F).setColor(color);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public abstract void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data);

    protected static boolean checkHovering(IGui gui, int x, int y, double mouseX, double mouseY, int width, int height) {
        mouseX -= gui.getPosX();
        mouseY -= gui.getPosY();
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    //region Getters & Setters
    public boolean canHover(IGui gui, int x, int y, double mouseX, double mouseY) {
        return checkHovering(gui, x, y, mouseX, mouseY, this.getWidth(), this.getHeight());
    }

    public void updateHovering(IGui gui, int x, int y, double mouseX, double mouseY) {
        this.isHovering = this.canHover(gui, x, y, mouseX, mouseY);
    }

    public boolean isHovering() {
        return this.isHovering || this.isFocused;
    }

    public void setHovering(boolean hovering) {
        this.isHovering = hovering;
    }

    public void setFocus(GuiDefinition def, boolean focus) {
        if( this.isFocusable ) {
            if( isFocused && !focus ) {
                def.setFocusedElement(null);
            } else if( !isFocused && focus ) {
                def.setFocusedElement(this);
            }
        }
    }

    public void setFocus(GuiDefinition def, boolean focus, UUID defID) {
        if( def.checkID(defID) ) {
            this.isFocused = focus;
        }
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void onFocusChange(boolean focus) { }

    public String getId() {
        return this.id;
    }

    public void updateId(String newId) {
        this.id = newId;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        this.geometryListeners.forEach(Runnable::run);
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
    }

    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int x) {
        this.posX = x;
        this.geometryListeners.forEach(Runnable::run);
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int y) {
        this.posY = y;
        this.geometryListeners.forEach(Runnable::run);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        if( this.isResizable ) {
            this.width = width;
            this.geometryListeners.forEach(Runnable::run);
        }
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        if( this.isResizable ) {
            this.height = height;
            this.geometryListeners.forEach(Runnable::run);
        }
    }

    public Alignment getHorizontalAlignment() {
        return this.hAlignment.forHorizontal ? this.hAlignment : Alignment.LEFT;
    }

    public void setHorizontalAlignment(Alignment alignment) {
        if( alignment.forHorizontal ) {
            this.hAlignment = alignment;
            this.geometryListeners.forEach(Runnable::run);
        }
    }

    public Alignment getVerticalAlignment() {
        return this.vAlignment.forVertical ? this.vAlignment : Alignment.TOP;
    }

    public void setVerticalAlignment(Alignment alignment) {
        if( alignment.forVertical ) {
            this.vAlignment = alignment;
            this.geometryListeners.forEach(Runnable::run);
        }
    }

    protected void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.geometryListeners.forEach(Runnable::run);
    }
//endregion

    public enum Alignment
    {
        TOP(false, true),
        LEFT(true, false),
        CENTER(true, true),
        RIGHT(true, false),
        BOTTOM(false, true),
        JUSTIFY(true, false);

        public final boolean forHorizontal;
        public final boolean forVertical;

        Alignment(boolean forHorizontal, boolean forVertical) {
            this.forHorizontal = forHorizontal;
            this.forVertical = forVertical;
        }

        /**
         * a case-insensitive version of {@link Alignment#valueOf(String)}
         * @param s the name of the constant to be fetched
         * @return the enum constant of the specified name
         * @throws IllegalArgumentException if this enum type has no constant with the specified name
         */
        public static Alignment fromString(String s) {
            return Alignment.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }

    public enum Orientation
    {
        HORIZONTAL,
        VERTICAL;

        /**
         * a case-insensitive version of {@link Orientation#valueOf(String)}
         * @param s the name of the constant to be fetched
         * @return the enum constant of the specified name
         * @throws IllegalArgumentException if this enum type has no constant with the specified name
         */
        public static Orientation fromString(String s) {
            return Orientation.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Priorities
    {
        Priority[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Priorities.class)
    @Target(ElementType.TYPE)
    public @interface Priority
    {
        EventPriority value();
        InputPriority target();
    }

    public enum InputPriority
    {
        NONE,
        MOUSE_INPUT,
        KEY_INPUT;

        private static final InputPriority[] TARGETS = values();

        public static void forEach(Consumer<InputPriority> c) {
            for( InputPriority t : TARGETS ) {
                c.accept(t);
            }
        }
    }

    @FunctionalInterface
    public interface HoverCallback
    {
        boolean check(IGui gui, int x, int y, double mouseX, double mouseY, int width, int height);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Resizable { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Focusable { }

    public abstract static class Builder<T extends GuiElement>
    {
        protected T elem;

        protected Builder(T elem) {
            this.elem = elem;
        }

        public Builder<T> withPos(int x, int y) {
            this.elem.posX = x;
            this.elem.posY = y;

            return this;
        }

        public Builder<T> withSize(int width, int height) {
            this.elem.width = width;
            this.elem.height = height;

            return this;
        }

        public Builder<T> withAlignment(Alignment horizontal, Alignment vertical) {
            this.elem.hAlignment = horizontal;
            this.elem.vAlignment = vertical;

            return this;
        }

        public Builder<T> withVisibility(boolean visible) {
            this.elem.setVisible(visible);

            return this;
        }

        public Builder<T> withEnabled(boolean enable) {
            this.elem.setEnabled(enable);

            return this;
        }

        public Builder<T> withGeometryChangeListener(@Nonnull Runnable listener) {
            this.elem.geometryListeners.add(listener);

            return this;
        }

        public T get() {
            return elem;
        }
    }
}
