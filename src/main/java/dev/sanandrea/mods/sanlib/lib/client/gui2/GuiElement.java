package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.element.Texture;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraftforge.eventbus.api.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class GuiElement
        implements IGuiReference
{
    protected int posX = 0;
    protected int posY = 0;
    protected Alignment hAlignment = Alignment.LEFT;
    protected Alignment vAlignment = Alignment.TOP;
    protected int width = 0;
    protected int height = 0;

    protected boolean isVisible = true;

    private final boolean isResizable = this.getClass().isAnnotationPresent(Resizable.class);

    protected GuiElement() {
        this(0, 0, 0, 0, Alignment.LEFT, Alignment.TOP);
    }

    protected GuiElement(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, Alignment.LEFT, Alignment.TOP);
    }

    protected GuiElement(int posX, int posY, int width, int height, Alignment hAlignment, Alignment vAlignment) {
        this.posX = posX;
        this.posY = posY;
        this.hAlignment = hAlignment;
        this.vAlignment = vAlignment;
        this.width = width;
        this.height = height;
    }

    public void load(IGui gui) { }

    public void unload(IGui gui) { }

    public abstract void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks);

    public void loadFromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.setPosX(JsonUtils.getIntVal(data.get("x"), 0));
        this.setPosY(JsonUtils.getIntVal(data.get("y"), 0));
        this.setSize(JsonUtils.getIntVal(data.get("width"), 0), JsonUtils.getIntVal(data.get("height"), 0));
        this.setHorizontalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("horizontalAlign"), Alignment.LEFT.toString())));
        this.setVerticalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("verticalAlign"), Alignment.TOP.toString())));

        this.setVisible(JsonUtils.getBoolVal(data.get("visible"), true));

        this.fromJson(gui, guiDef, data);
    }

    public abstract void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data);

//region Getters & Setters
    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int x) {
        this.posX = x;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int y) {
        this.posY = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        if( this.isResizable ) {
            this.width = width;
        }
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        if( this.isResizable ) {
            this.height = height;
        }
    }

    public Alignment getHorizontalAlignment() {
        return this.hAlignment.forHorizontal ? this.hAlignment : Alignment.LEFT;
    }

    public void setHorizontalAlignment(Alignment alignment) {
        if( alignment.forHorizontal ) {
            this.hAlignment = alignment;
        }
    }

    public Alignment getVerticalAlignment() {
        return this.vAlignment.forVertical ? this.vAlignment : Alignment.TOP;
    }

    public void setVerticalAlignment(Alignment alignment) {
        if( alignment.forVertical ) {
            this.vAlignment = alignment;
        }
    }

    protected void setSize(int width, int height) {
        this.width = width;
        this.height = height;
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Resizable { }

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
            this.elem.isVisible = visible;

            return this;
        }

        public T get() {
            return elem;
        }
    }
}
