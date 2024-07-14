package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui2.Spacing;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

//@SuppressWarnings("unused")
@GuiElement.Resizable
@GuiElement.Focusable
public class TextField
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("textfield");

    protected Text text = Text.Builder.createText().withColor(Text.DEFAULT_COLOR, 0xFFFF80D0).get();
    protected Text suggestedText = Text.Builder.createText().withColor(Text.DEFAULT_COLOR, 0xFFA0A0A0).get();

    protected int                  maxLength    = 32;
    protected boolean              canLoseFocus = true;
    protected boolean              isEditable   = true;
    protected String               suggestion;
    protected Consumer<String>     onChangeListener;
    protected Predicate<String>    filter       = s -> true;
    protected Map<String, Integer> colors       = new HashMap<>();
    @Nonnull
    protected Spacing              padding = Spacing.NONE;

    @Nonnull
    protected String  value = "";
    protected int     ticksExisted;
    protected boolean isShiftPressed;
    protected int     displayIndex;
    protected int     cursorIndex;
    protected int     highlightIndex;
    protected int renderOffsetX;
    protected int renderOffsetY;


    public TextField(String id) {
        super(id);

        this.loadTextElement();
    }

    @Override
    public void tick(IGui gui) {
        super.tick(gui);
        this.text.tick(gui);

        ++this.ticksExisted;
    }

    protected String getVisibleText() {
        FontRenderer font = this.text.getFont();
        return font.plainSubstrByWidth(this.value.substring(this.displayIndex), this.getWidth() - this.padding.getWidth() - font.width("_"));
    }

    protected void loadTextElement() {
        this.text.setTextFunc((gui, defStr) -> new StringTextComponent(this.getVisibleText()));
        this.suggestedText.setTextFunc((gui, defStr) -> new StringTextComponent(this.suggestion));
    }

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) {
        this.renderOffsetX = x + gui.getPosX();
        this.renderOffsetY = y + gui.getPosY();

        if( this.value.isEmpty() ) {
            this.suggestedText.render(gui, matrixStack, x + this.padding.getLeft(), y + this.padding.getTop(), mouseX, mouseY, partialTicks);
        } else {
            this.text.render(gui, matrixStack, x + this.padding.getLeft(), y + this.padding.getTop(), mouseX, mouseY, partialTicks);
        }

        this.text.render(gui, matrixStack, x + this.padding.getLeft(), y + this.padding.getTop(), mouseX, mouseY, partialTicks);

        this.renderCursor(matrixStack, x, y);
        this.renderHighlight(matrixStack, x, y);
    }

    @Override
    public void renderDebug(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks, int level) {
        super.renderDebug(gui, matrixStack, x, y, mouseX, mouseY, partialTicks, level);
        if( this.value.isEmpty() ) {
            this.suggestedText.renderDebug(gui, matrixStack, x + this.padding.getLeft(), y + this.padding.getTop(), mouseX, mouseY, partialTicks, level + 1);
        } else {
            this.text.renderDebug(gui, matrixStack, x + this.padding.getLeft(), y + this.padding.getTop(), mouseX, mouseY, partialTicks, level + 1);
        }
    }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) {
        this.padding = Spacing.loadSpacing(data.get("padding"), false);
    }

    @Override
    public int getHeight() {
        return this.text.getHeight() + this.padding.getHeight();
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        if( !this.isActive() ) {
            return super.keyPressed(gui, keyCode, scanCode, modifiers);
        } else {
            this.isShiftPressed = Screen.hasShiftDown();
            if( Screen.isSelectAll(keyCode) ) {
                this.moveCursor(-1, 0);
                return true;
            } else if( Screen.isCopy(keyCode) ) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                return true;
            } else if( Screen.isPaste(keyCode) ) {
                if( this.isEditable ) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }
                return true;
            } else if( Screen.isCut(keyCode) ) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                if( this.isEditable ) {
                    this.insertText("");
                }

                return true;
            } else {
                switch( keyCode ) {
                    case GLFW.GLFW_KEY_DELETE:
                    case GLFW.GLFW_KEY_BACKSPACE:
                        if( this.isEditable ) {
                            this.isShiftPressed = false;
                            this.deleteText(keyCode == GLFW.GLFW_KEY_BACKSPACE ? -1 : 1);
                            this.isShiftPressed = Screen.hasShiftDown();
                        }
                        return true;
                    case GLFW.GLFW_KEY_LEFT:
                    case GLFW.GLFW_KEY_RIGHT:
                        int shiftId = keyCode == GLFW.GLFW_KEY_LEFT ? -1 : 1;
                        if( Screen.hasControlDown() ) {
                            this.moveCursorTo(this.getWordPosition(shiftId));
                        } else {
                            this.moveCursor(shiftId);
                        }
                        return true;
                    case GLFW.GLFW_KEY_HOME:
                    case GLFW.GLFW_KEY_END:
                        this.moveCursorTo(keyCode == GLFW.GLFW_KEY_END ? -1 : 0);
                        return true;
                    default:
                        return false;
                }
            }
        }
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        if( this.isActive() && SharedConstants.isAllowedChatCharacter(typedChar) ) {
            if( this.isEditable ) {
                this.insertText(Character.toString(typedChar));
            }

            return true;
        }

        return super.charTyped(gui, typedChar, keyCode);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        if( this.isVisible() ) {
            if( this.canLoseFocus ) {
                this.setFocus(gui.getDefinition(), this.isHovering);
            }

            if( this.isFocused() && this.isHovering && button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
                int cursorPos = MathHelper.floor(mouseX) - this.renderOffsetX - this.padding.getLeft();

                FontRenderer font = this.text.getFont();
                String       s    = this.getVisibleText();
                this.moveCursorTo(font.plainSubstrByWidth(s, cursorPos).length() + this.displayIndex);

                return true;
            }
        }

        return super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public void onFocusChange(boolean focus) {
        if( focus ) {
            this.ticksExisted = 0;
        }
    }

    public void setOnChangeListener(Consumer<String> listener) {
        this.onChangeListener = listener;
    }

    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    public void setValue(@Nonnull String value) {
        if( this.filter.test(value) ) {
            this.value = value.length() > this.maxLength ? value.substring(0, this.maxLength) : value;

            this.moveCursor(-1, -1);
            this.onValueChange();
        }
    }

    public void onValueChange() {
        if( this.onChangeListener != null ) {
            this.onChangeListener.accept(this.value);
        }
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }

    public String getSelectedText() {
        int min = Math.min(this.cursorIndex, this.highlightIndex);
        int max = Math.max(this.cursorIndex, this.highlightIndex);

        return this.value.substring(min, max);
    }

    public void insertText(@Nonnull String text) {
        this.insertText(text, true);
    }

    protected void insertText(@Nonnull String text, boolean useInternalFilter) {
        int min = Math.min(this.cursorIndex, this.highlightIndex);
        int max = Math.max(this.cursorIndex, this.highlightIndex);

        if( useInternalFilter ) {
            text = SharedConstants.filterText(text);
        }

        if( this.filter.test(text) ) {
            this.setValue(new StringBuilder(this.value).replace(min, max, text).toString());
            this.moveCursorTo(min+1, false);
        }
    }

    protected void deleteText(int amount) {
        this.deleteText(amount, Screen.hasControlDown());
    }

    public void deleteText(int offset, boolean doWords) {
        if( !this.value.isEmpty() ) {
            if( this.highlightIndex != this.cursorIndex ) {
                this.insertText("");
            } else {
                if( doWords ) {
                    offset = this.getWordPosition(offset) - this.cursorIndex;
                }
                offset = this.getCursorIndex(offset);
                int minIdx = Math.min(offset, this.cursorIndex);
                int maxIdx = Math.max(offset, this.cursorIndex);

                if( minIdx != maxIdx ) {
                    String res = new StringBuilder(this.value).delete(minIdx, maxIdx).toString();
                    if( this.filter.test(res) ) {
                        this.value = res;
                        this.moveCursorTo(minIdx, false);
                    }
                }
            }
        }
    }

    protected int getCursorIndex(int offset) {
        return Util.offsetByCodepoints(this.value, this.cursorIndex, offset);
    }

    public int getWordPosition(int wordCount) {
        return this.getWordPosition(wordCount, this.cursorIndex, true);
    }

    protected int getWordPosition(int wordCount, int start, boolean trim) {
        boolean reverse = wordCount < 0;
        wordCount = Math.abs(wordCount);

        for( int i = 0; i < wordCount; i++ ) {
            if( reverse ) {
                while( trim && start > 0 && this.value.charAt(start - 1) == ' ' ) {
                    --start;
                }

                while( start > 0 && this.value.charAt(start - 1) != ' ' ) {
                    --start;
                }
            } else {
                int len = this.value.length();
                start = this.value.indexOf(' ', start);
                if( start == -1 ) {
                    start = len;
                } else {
                    while( trim && start < len && this.value.charAt(start) == ' ' ) {
                        ++start;
                    }
                }
            }
        }

        return wordCount;
    }

    public void setCursorIndex(int index) {
        this.cursorIndex = MathHelper.clamp(index, 0, this.value.length());
    }

    public void moveCursorTo(int index) {
        this.moveCursorTo(index, null);
    }

    protected void moveCursorTo(int index, Boolean overwriteShift) {
        if( (!this.isShiftPressed || Boolean.FALSE.equals(overwriteShift)) && !Boolean.TRUE.equals(overwriteShift) ) {
            this.moveCursor(index, index);
        } else {
            this.moveCursor(index, this.highlightIndex);
        }

        this.onValueChange();
    }

    public void moveCursor(int amount) {
        this.moveCursorTo(this.getCursorIndex(amount));
    }

    public void renderHighlight(MatrixStack stack, int x, int y) {
        if( this.cursorIndex != this.highlightIndex ) {
            Matrix4f pose = stack.last().pose();

            int minIdx = Math.min(this.cursorIndex, this.highlightIndex) - displayIndex;
            int maxIdx = Math.max(this.cursorIndex, this.highlightIndex) - displayIndex;

            FontRenderer font = this.text.getFont();
            String visible = this.getVisibleText();

            int start = font.width(visible.substring(0, MathHelper.clamp(minIdx, 0, visible.length())));
            int end = font.width(visible.substring(0, MathHelper.clamp(maxIdx, 0, visible.length())));

            if( start != end ) {
                Tessellator tessellator   = Tessellator.getInstance();
                BufferBuilder         bufferbuilder = tessellator.getBuilder();

                RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
                RenderSystem.disableTexture();
                RenderSystem.enableColorLogicOp();
                RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
                bufferbuilder.vertex(pose, (float) x + start, (float) y + this.getHeight(), 0.0F).endVertex();
                bufferbuilder.vertex(pose, (float) x + end, (float) y + this.getHeight(), 0.0F).endVertex();
                bufferbuilder.vertex(pose, (float) x + end, y, 0.0F).endVertex();
                bufferbuilder.vertex(pose, (float) x + start, y, 0.0F).endVertex();
                tessellator.end();
                RenderSystem.disableColorLogicOp();
                RenderSystem.enableTexture();
            }
        }
    }

    public void renderCursor(MatrixStack stack, int x, int y) {
        String visible = this.getVisibleText();
        int visibleLength = visible.length();
        int localCursorIndex = this.cursorIndex - this.displayIndex;

        if( this.isFocused() && this.ticksExisted / 6 % 2 == 0
            && MiscUtils.between(0, localCursorIndex, visibleLength) )
        {
            FontRenderer font = this.text.getFont();
            int cx = font.width(visible.substring(0, MathHelper.clamp(localCursorIndex, 0, visibleLength)));
            int textColor = this.text.getColor(Text.DEFAULT_COLOR);

            if( this.cursorIndex < this.value.length() ) {
                AbstractGui.fill(stack, x + cx, y - 1, x + cx + 1, y + font.lineHeight, textColor);
            } else {
                AbstractGui.fill(stack, x + cx, y + font.lineHeight - 2, x + cx + 5, y + font.lineHeight - 1, textColor);
            }
        }
    }

    public void moveCursor(int indexCursor, int indexHighlight) {
        int len = this.value.length();

        if( indexCursor < 0 ) {
            indexCursor = len + indexCursor + 1;
        }
        if( indexHighlight < 0 ) {
            indexHighlight = len + highlightIndex + 1;
        }

        this.cursorIndex = MathHelper.clamp(indexCursor, 0, len);
        this.highlightIndex = MathHelper.clamp(indexHighlight, 0, len);

        if( this.displayIndex > len ) {
            this.displayIndex = len;
        }

        int          dispIdxEnd = this.getVisibleText().length() + this.displayIndex;

        if( this.cursorIndex <= this.displayIndex ) {
            this.displayIndex -= 4;
        } else if( this.cursorIndex >= dispIdxEnd && this.value.length() > dispIdxEnd ) {
            this.displayIndex += 4;
        }

        this.displayIndex = MathHelper.clamp(this.displayIndex, 0, len);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public boolean isActive() {
        return this.isVisible() && this.isEnabled() && this.isFocused() && this.isEditable();
    }
}
