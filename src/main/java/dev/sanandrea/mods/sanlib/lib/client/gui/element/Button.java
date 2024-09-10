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

    protected GuiElement background;
    protected GuiElement label;

    protected ResourceLocation customSoundID;
    protected SoundEvent       customSound;

    protected static final int DEFAULT_TEXT_COLOR          = 0xFFFFFFFF;
    protected static final int DEFAULT_DISABLED_TEXT_COLOR = 0xFFA0A0A0;

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
            bgDataObj = JsonUtils.ObjectBuilder.create()
                                               .value("type", Texture.ID.toString())
                                               .value("texture", "widget/button")
                                               .value("textureWidth", 200)
                                               .value("textureHeight", 20)
                                               .value("isSprite", true)
                                               .value("hover", JsonUtils.ObjectBuilder.create().value("texture", "widget/button_highlighted").get())
                                               .value("disabled", JsonUtils.ObjectBuilder.create().value("texture", "widget/button_disabled").get())
                                               .get();

//            JsonUtils.addDefaultJsonProperty(bgDataObj, "type", TiledTexture.ID.toString());
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "texture", "textures/gui/widget/button.png");
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "v", 66);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "vHover", 86);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "vDisabled", 46);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "tileTextureWidth", 200);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "tileTextureHeight", 20);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "centralWidth", 190);
//            JsonUtils.addDefaultJsonProperty(bgDataObj, "centralHeight", 14);
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
                lblData = new JsonObject();
                JsonUtils.addJsonProperty((JsonObject) lblData, "type", "text");
                JsonUtils.addJsonProperty((JsonObject) lblData, "text", txt);
            }

            if( lblData.isJsonObject() ) {
                JsonObject lblDataObj = (JsonObject) lblData;

                JsonObject defaultColors = new JsonObject();
                defaultColors.addProperty(Text.DEFAULT_COLOR, DEFAULT_TEXT_COLOR);
                defaultColors.addProperty(Text.DISABLED_COLOR, DEFAULT_DISABLED_TEXT_COLOR);
                JsonUtils.addDefaultJsonProperty(lblDataObj, "colors", defaultColors);
                JsonUtils.addDefaultJsonProperty(lblDataObj, "shadow", true);
                JsonUtils.addDefaultJsonProperty(lblDataObj, "horizontalAlign", Alignment.CENTER.toString());
                JsonUtils.addDefaultJsonProperty(lblDataObj, "verticalAlign", Alignment.CENTER.toString());

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

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(MiscUtils.get(this.customSound, SoundEvents.UI_BUTTON_CLICK.value()), 1.0F));

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
//            TiledTexture bg = TiledTexture.Builder.createTiledTexture(this.elem.backgroundId)
//                                                  .withTileTextureSize(200, 20)
//                                                  .withCentralTextureSize(190, 14)
//                                                  .withData(ResourceLocation.withDefaultNamespace("textures/gui/widgets.png"))
//                                                  .withPosUV(0, 66)
//                                                  .withHoverPosUV(0, 86)
//                                                  .withDisabledPosUV(0, 46)
//                                                  .get();
            Texture bg = Texture.Builder.createTexture()
                                        .asSprite()
                                        .withData(new Texture.TextureData(ResourceLocation.withDefaultNamespace("widget/button")))
                                        .withHoverData(new Texture.TextureData(ResourceLocation.withDefaultNamespace("widget/button_highlighted")))
                                        .withDisabledData(new Texture.TextureData(ResourceLocation.withDefaultNamespace("widget/button_disabled")))
                                        .withSize(200, 20)
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
            Text txt = b.withTextColor(DEFAULT_TEXT_COLOR)
                        .withDisabledColor(DEFAULT_DISABLED_TEXT_COLOR)
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
