package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonObject;
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
{
    protected int posX = 0;
    protected int posY = 0;
    protected Alignment hAlignment = Alignment.LEFT;
    protected Alignment vAlignment = Alignment.TOP;

    protected boolean isVisible = true;
    private boolean updateState = true;

    public void updateState() {
        this.updateState = true;
    }

    boolean tick() {
        this.update();

        boolean b = updateState;
        this.updateState = false;
        return b;
    }

    public abstract void update();

    public void unload(IGui gui) { }

    void loadFromJson(IGui gui, JsonObject data) {
        this.setPosX(JsonUtils.getIntVal(data.get("posX"), 0));
        this.setPosY(JsonUtils.getIntVal(data.get("posY"), 0));
        this.setHorizontalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("horizontalAlign"), "")));
        this.setVerticalAlignment(Alignment.fromString(JsonUtils.getStringVal(data.get("verticalAlign"), "")));

        this.setVisible(JsonUtils.getBoolVal(data.get("visible"), true));

        this.fromJson(gui, data);
    }

    public abstract void fromJson(IGui gui, JsonObject data);

    //region Getters & Setters
    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        this.updateState();
    }

    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int x) {
        this.posX = x;
        this.updateState();
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int y) {
        this.posY = y;
        this.updateState();
    }

    public Alignment getHorizontalAlignment() {
        return this.hAlignment.forHorizontal ? this.hAlignment : Alignment.LEFT;
    }

    public void setHorizontalAlignment(Alignment alignment) {
        if( alignment.forHorizontal ) {
            this.hAlignment = alignment;
            this.updateState();
        }
    }

    public Alignment getVerticalAlignment() {
        return this.vAlignment.forVertical ? this.vAlignment : Alignment.TOP;
    }

    public void setVerticalAlignment(Alignment alignment) {
        if( alignment.forVertical ) {
            this.vAlignment = alignment;
            this.updateState();
        }
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Priorities
    {
        Priority[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Priorities.class)
    @Target(ElementType.TYPE)
    @interface Priority
    {
        EventPriority value();
        PriorityTarget target();
    }

    enum PriorityTarget
    {
        MOUSE_INPUT,
        KEY_INPUT;

        private static final PriorityTarget[] TARGETS = values();

        public static void forEach(Consumer<PriorityTarget> c) {
            for( PriorityTarget t : TARGETS ) {
                c.accept(t);
            }
        }
    }
}
