package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@GuiElement.Resizable
public class Button
        extends ElementParent
{
    public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("button");

    protected final String backgroundId = String.format("%s_background", this.id);
    protected final String labelId      = String.format("%s_labelId", this.id);

    protected static final ColorData DEFAULT_LABEL_COLOR = new ColorData(new ColorData.StatedColor(0xFFFFFFFF, 0xFFFFFFFF, 0xFFA0A0A0));

    protected GuiElement background;
    protected GuiElement label;

    protected ResourceLocation customSoundID;
    protected SoundEvent       customSound;

    protected Consumer<Button> onClickListener;

    public Button(String id) {
        super(id);

        this.addGeometryChangeListener(this::updateSize);
    }

    @Override
    public GuiElement putElement(String id, GuiElement child) {
        if( Objects.equals(id, backgroundId) ) {
            this.background = child;
            return super.putElement(id, child);
        } else if( Objects.equals(id, labelId) ) {
            this.label = child;
            return super.putElement(id, child);
        }

        return null;
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.customSoundID = JsonUtils.getLocation(data.get("clickSound"), null);

        JsonObject  bgDataObj = null;
        JsonElement bgData    = data.get("background");
        if( bgData == null ) {
            TextureData texture = new TextureData(new TextureData.TextureDef("widget/button"),
                                                  new TextureData.TextureDef("widget/button_highlighted"),
                                                  new TextureData.TextureDef("widget/button_disabled"),
                                                  200, 20);

            bgDataObj = JsonUtils.ObjectBuilder.create()
                                               .value(JSON_TYPE, Texture.ID.toString())
                                               .value(Texture.JSON_TEXTURE, texture.toJson())
                                               .value(Texture.JSON_IS_SPRITE, true)
                                               .get();

        } else if( bgData.isJsonObject() ) {
            bgDataObj = bgData.getAsJsonObject();
        }

        if( bgDataObj != null ) {
            this.putElement(this.backgroundId, guiDef.loadElement(this.backgroundId, bgDataObj));
        }

        JsonElement lblData = data.get("label");
        if( lblData != null ) {
            if( lblData.isJsonPrimitive() ) {
                String txt = lblData.getAsString();
                lblData = JsonUtils.ObjectBuilder.create().value("type", Text.ID).value("text", txt).get();
            }

            if( lblData.isJsonObject() ) {
                JsonObject lblDataObj = (JsonObject) lblData;

                JsonUtils.addDefaultJsonProperty(lblDataObj, Text.JSON_COLOR, DEFAULT_LABEL_COLOR.toJson());
                JsonUtils.addDefaultJsonProperty(lblDataObj, Text.JSON_SHADOW, true);
                JsonUtils.addDefaultJsonProperty(lblDataObj, JSON_HORIZONTAL_ALIGN, Alignment.CENTER.toString());
                JsonUtils.addDefaultJsonProperty(lblDataObj, JSON_VERTICAL_ALIGN, Alignment.CENTER.toString());

                this.putElement(this.labelId, guiDef.loadElement(this.labelId, lblDataObj));
            }
        } else {
            throw new JsonSyntaxException("Button needs a property named \"label\" as a text or a custom element object");
        }

        this.updateSize();
    }

    @Override
    protected Collection<GuiElement> getVisibleChildren() {
        return this.children.values();
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) {
        MiscUtils.accept(this.background, bkg -> {
            bkg.setHovering(this.isHovering());
            bkg.render(gui, graphics, x, y, mouseX, mouseY, partialTicks);
        });
        MiscUtils.accept(this.label, lbl -> {
            lbl.setHovering(this.isHovering());
            lbl.render(gui, graphics, x + lbl.getPosX(), y + lbl.getPosY(), mouseX, mouseY, partialTicks);
        });
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        if( this.isEnabled() && this.isHovering() ) {
            if( this.onClickListener != null ) {
                this.onClickListener.accept(this);
            }

            if( this.customSoundID != null ) {
                this.customSound = MiscUtils.get(this.customSound, () -> BuiltInRegistries.SOUND_EVENT.get(this.customSoundID));
            }

            Minecraft.getInstance().getSoundManager()
                     .play(SimpleSoundInstance.forUI(MiscUtils.get(this.customSound, SoundEvents.UI_BUTTON_CLICK.value()), 1.0F));

            return true;
        }

        return super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        this.updateEnabled();
    }

    protected void updateSize() {
        MiscUtils.accept(this.background, b -> {
            b.setWidth(this.getWidth());
            b.setHeight(this.getHeight());
        });

        MiscUtils.accept(this.label, l -> {
            if( l.getHorizontalAlignment() == Alignment.CENTER ) {
                l.setPosX(this.getWidth() / 2);
            }
            if( l.getVerticalAlignment() == Alignment.CENTER ) {
                l.setPosY(this.getHeight() / 2);
            }
        });
    }

    protected void updateEnabled() {
        boolean isEnabled = this.isEnabled();

        MiscUtils.accept(this.getElement(backgroundId), bkg -> bkg.setEnabled(isEnabled));
        MiscUtils.accept(this.getElement(labelId), lbl -> lbl.setEnabled(isEnabled));
    }

    public void setOnClickListener(Consumer<Button> listener) {
        this.onClickListener = listener;
    }

    public static class Builder<T extends Button>
            extends GuiElement.Builder<T>
    {
        protected Builder(T elem) {
            super(elem);
        }

        public Builder<T> withBackground(GuiElement background) {
            background.updateId(this.elem.backgroundId);
            this.elem.putElement(this.elem.backgroundId, background);

            return this;
        }

        public Builder<T> withDefaultBackground() {
            Texture bg = Texture.Builder.createTexture()
                                        .asSprite()
                                        .withData(new TextureData(new TextureData.TextureDef("widget/button"),
                                                                  new TextureData.TextureDef("widget/button_highlighted"),
                                                                  new TextureData.TextureDef("widget/button_disabled"),
                                                                  200, 20))
                                        .get();

            this.elem.putElement(this.elem.backgroundId, bg);

            return this;
        }

        public Builder<T> withLabel(GuiElement label) {
            label.updateId(this.elem.backgroundId);
            this.elem.putElement(this.elem.labelId, label);

            return this;
        }

        public Builder<T> withLabel(String lbl) {
            return this.withLabel(b -> b.withTranslatedText(lbl));
        }

        public Builder<T> withLabel(Component lbl) {
            return this.withLabel(b -> b.withText(lbl));
        }

        protected Builder<T> withLabel(Consumer<Text.Builder<Text>> lblTxtSetter) {
            Text.Builder<Text> b = Text.Builder.createText(this.elem.labelId);
            lblTxtSetter.accept(b);
            Text txt = b.withTextColor(DEFAULT_LABEL_COLOR.color())
                        .withShadow()
                        .withAlignment(Alignment.CENTER, Alignment.CENTER)
                        .get();

            this.elem.putElement(this.elem.labelId, txt);

            return this;
        }

        public Builder<T> withClickSound(SoundEvent sound) {
            this.elem.customSound = sound;

            return this;
        }

        @Override
        public T get() {
            T btn = super.get();
            btn.updateSize();
            btn.updateEnabled();

            return btn;
        }

        public static Builder<Button> create() {
            return create(UUID.randomUUID().toString());
        }

        public static Builder<Button> create(String id) {
            return new Builder<>(new Button(id));
        }
    }
}
