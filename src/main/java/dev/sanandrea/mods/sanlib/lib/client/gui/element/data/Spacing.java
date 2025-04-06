package dev.sanandrea.mods.sanlib.lib.client.gui.element.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Spacing
{
    public static final Spacing NONE            = new Spacing(0, false);

    public static final String  JSON_ALL        = "all";
    public static final String  JSON_HORIZONTAL = "horizontal";
    public static final String  JSON_LEFT       = "left";
    public static final String  JSON_RIGHT      = "right";
    public static final String JSON_VERTICAL = "vertical";
    public static final String JSON_TOP = "top";
    public static final String JSON_BOTTOM = "bottom";

    private             int     top;
    private             int     bottom;
    private             int     left;
    private             int     right;

    private boolean isMutable;

    public int getTop() {
        return this.top;
    }

    public int getBottom() {
        return this.bottom;
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }

    public int getWidth() {
        return this.left + this.right;
    }

    public int getHeight() {
        return this.top + this.bottom;
    }

    public void setTop(int top) {
        if( !isMutable ) {
            this.top = top;
        }
    }

    public void setBottom(int bottom) {
        if( !isMutable ) {
            this.bottom = bottom;
        }
    }

    public void setLeft(int left) {
        if( !isMutable ) {
            this.left = left;
        }
    }

    public void setRight(int right) {
        if( !isMutable ) {
            this.right = right;
        }
    }

    public boolean isMutable() {
        return this.isMutable;
    }

    public void setImmutable() {
        this.isMutable = false;
    }

    public Spacing(int top, int right, int bottom, int left, boolean isMutable) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.isMutable = isMutable;
    }

    public Spacing(int top, int right, int bottom, int left) {
        this(top, right, bottom, left, true);
    }

    public Spacing(int top, int horizontal, int bottom, boolean isMutable) {
        this(top, horizontal, bottom, horizontal, isMutable);
    }

    public Spacing(int top, int horizontal, int bottom) {
        this(top, horizontal, bottom, horizontal);
    }

    public Spacing(int vertical, int horizontal, boolean isMutable) {
        this(vertical, horizontal, vertical, horizontal, isMutable);
    }

    public Spacing(int vertical, int horizontal) {
        this(vertical, horizontal, vertical, horizontal);
    }

    public Spacing(int all, boolean isMutable) {
        this(all, all, all, all, isMutable);
    }

    public Spacing(int all) {
        this(all, all, all, all);
    }

    @Nonnull
    public static Spacing fromJson(JsonElement data, boolean isMutable, Spacing def) {
        if( def == null ) {
            def = new Spacing(0, isMutable);
        }

        if( data == null ) {
            return def;
        }

        if( data.isJsonPrimitive() ) {
            return new Spacing(JsonUtils.getIntVal(data, 0), isMutable);
        } else if( data.isJsonArray() ) {
            return fromJsonArray(JsonUtils.getIntArray(data, new int[] { 0 }, Range.of(1, 4)), isMutable, def);
        } else if( data.isJsonObject() ) {
            return fromJsonObject(data.getAsJsonObject(), isMutable, def);
        }

        return new Spacing(0, isMutable);
    }

    private static Spacing fromJsonArray(int[] arr, boolean isMutable, Spacing def) {
        return switch( arr.length ) {
            case 4 -> new Spacing(arr[0], arr[1], arr[2], arr[3], isMutable);
            case 3 -> new Spacing(arr[0], arr[1], arr[2], isMutable);
            case 2 -> new Spacing(arr[0], arr[1], isMutable);
            case 1 -> new Spacing(arr[0], isMutable);
            default -> def;
        };
    }

    private static Spacing fromJsonObject(JsonObject jobj, boolean isMutable, Spacing def) {
        Integer top;
        Integer left;
        Integer bottom;
        Integer right;

        if( jobj.has(JSON_ALL) ) {
            top = bottom = left = right = JsonUtils.getIntVal(jobj.get(JSON_ALL));
        } else {
            if( jobj.has(JSON_HORIZONTAL) ) {
                left = right = JsonUtils.getIntVal(jobj.get(JSON_HORIZONTAL));
            } else {
                left = JsonUtils.getIntValObj(jobj.get(JSON_LEFT), null);
                right = JsonUtils.getIntValObj(jobj.get(JSON_RIGHT), null);
            }
            if( jobj.has(JSON_VERTICAL) ) {
                top = bottom = JsonUtils.getIntVal(jobj.get(JSON_VERTICAL));
            } else {
                top = JsonUtils.getIntValObj(jobj.get(JSON_TOP), null);
                bottom = JsonUtils.getIntValObj(jobj.get(JSON_BOTTOM), null);
            }
        }

        if( top != null || left != null || right != null || bottom != null ) {
            return new Spacing(MiscUtils.get(top, 0), MiscUtils.get(right, 0), MiscUtils.get(bottom, 0), MiscUtils.get(left, 0), isMutable);
        } else {
            return def;
        }
    }
}
