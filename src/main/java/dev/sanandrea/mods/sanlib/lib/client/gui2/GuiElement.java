package dev.sanandrea.mods.sanlib.lib.client.gui2;

import java.util.Locale;
import java.util.UUID;

public abstract class GuiElement
{
    public final String id;

    protected int posX = 0;
    protected int posY = 0;
    protected Alignment hAlignment = Alignment.LEFT;
    protected Alignment vAlignment = Alignment.TOP;

    protected boolean isVisible = true;
    private boolean updateState = true;

    public GuiElement(String id) {
        this.id = id;
    }

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
}
