package dev.sanandrea.mods.sanlib.lib.client.gui.element.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class StatedData<T>
{
    public static final String JSON_REGULAR        = "regular";
    public static final String JSON_HOVER          = "hover";
    public static final String JSON_DISABLED       = "disabled";

    public final T regular;
    public final T hover;
    public final T disabled;

    protected final Function<T, JsonElement> instToJson;

    protected StatedData(@Nonnull T regular, @Nullable T hover, @Nullable T disabled, @Nonnull Function<T, JsonElement> instToJson) {
        this.regular = regular;
        this.hover = hover;
        this.disabled = disabled;
        this.instToJson = instToJson;
    }

    public static <T, U extends StatedData<T>> U fromJson(GuiDefinition guiDef, JsonElement data,
                                                          DataConstructor<T, U> ctor, DataParser<T> instanceParser)
    {
        return fromJson(guiDef, data, null, ctor, instanceParser);
    }

    public static <T, U extends StatedData<?>> U fromJson(GuiDefinition guiDef, JsonElement data, String regularAltName,
                                                          DataConstructor<T, U> ctor, DataParser<T> instanceParser)
    {
        T regular = null;
        T hover = null;
        T disabled = null;

        if( data instanceof JsonObject dataObj ) {
            if( dataObj.has(JSON_REGULAR) ) {
                regular = instanceParser.apply(guiDef, dataObj.get(JSON_REGULAR), null);
            } else if( regularAltName != null && dataObj.has(regularAltName) ) {
                regular = instanceParser.apply(guiDef, dataObj.get(regularAltName), null);
            } else {
                regular = instanceParser.apply(guiDef, data, null);
            }
            if( dataObj.has(JSON_HOVER) ) {
                hover = instanceParser.apply(guiDef, dataObj.get(JSON_HOVER), regular);
            }
            if( dataObj.has(JSON_DISABLED) ) {
                disabled = instanceParser.apply(guiDef, dataObj.get(JSON_DISABLED), regular);
            }
        } else if( data != null && !data.isJsonNull() ) {
            regular = instanceParser.apply(guiDef, data, null);
        }

        return ctor.apply(regular, hover, disabled);
    }

    public final JsonObject toJson() {
        JsonUtils.ObjectBuilder builder = JsonUtils.ObjectBuilder.create();

        builder.value(JSON_REGULAR, this.instToJson.apply(this.regular));
        MiscUtils.accept(this.hover, v -> builder.value(JSON_HOVER, this.instToJson.apply(v)));
        MiscUtils.accept(this.disabled, v -> builder.value(JSON_HOVER, this.instToJson.apply(v)));

        this.buildJson(builder);

        return builder.get();
    }

    @Nonnull
    public T get(boolean isHovering, boolean isDisabled) {
        if( this.disabled != null && isDisabled ) {
            return this.disabled;
        } else if( this.hover != null && isHovering ) {
            return this.hover;
        } else {
            return this.regular;
        }
    }

    public void buildJson(JsonUtils.ObjectBuilder builder) { }

    @FunctionalInterface
    public interface DataParser<T>
    {
        T apply(GuiDefinition guiDef, JsonElement data, T regular);
    }

    @FunctionalInterface
    public interface DataConstructor<T, U extends StatedData<?>>
    {
        U apply(T regular, T hover, T disabled);
    }
}
