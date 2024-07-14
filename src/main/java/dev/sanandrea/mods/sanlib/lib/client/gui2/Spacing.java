package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import org.apache.commons.lang3.Range;

@SuppressWarnings("unused")
public class Spacing
{
    public static final Spacing NONE = new Spacing(0, false);
    private int top;
    private int bottom;
    private int left;
    private int right;

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
        if( !isMutable ) this.top = top;
    }

    public void setBottom(int bottom) {
        if( !isMutable ) this.bottom = bottom;
    }

    public void setLeft(int left) {
        if( !isMutable ) this.left = left;
    }

    public void setRight(int right) {
        if( !isMutable ) this.right = right;
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

    public static Spacing loadSpacing(JsonElement data, boolean isMutable) {
        if( data == null ) {
            return new Spacing(0, isMutable);
        }

        if( data.isJsonPrimitive() ) {
            return new Spacing(JsonUtils.getIntVal(data, 0), isMutable);
        } else if( data.isJsonArray() ) {
            int[] arr = JsonUtils.getIntArray(data, new int[] {0}, Range.between(1, 4));

            switch( arr.length ) {
                case 4: return new Spacing(arr[0], arr[1], arr[2], arr[3], isMutable);
                case 3: return new Spacing(arr[0], arr[1], arr[2], isMutable);
                case 2: return new Spacing(arr[0], arr[1], isMutable);
                case 1: return new Spacing(arr[0], isMutable);
                default: // return default at end of method
            }
        } else if( data.isJsonObject() ) {
            JsonObject jobj = data.getAsJsonObject();
            int top;
            int left;
            int bottom;
            int right;

            if( jobj.has("all") ) {
                top = bottom = left = right = JsonUtils.getIntVal(jobj.get("all"));
            } else {
                if( jobj.has("horizontal") ) {
                    left = right = JsonUtils.getIntVal(jobj.get("horizontal"));
                } else {
                    left = JsonUtils.getIntVal(jobj.get("left"), 0);
                    right = JsonUtils.getIntVal(jobj.get("right"), 0);
                }
                if( jobj.has("vertical") ) {
                    top = bottom = JsonUtils.getIntVal(jobj.get("vertical"));
                } else {
                    top = JsonUtils.getIntVal(jobj.get("top"), 0);
                    bottom = JsonUtils.getIntVal(jobj.get("bottom"), 0);
                }
            }

            return new Spacing(top, right, bottom, left, isMutable);
        }

        return new Spacing(0, isMutable);
    }
}
