package de.sanandrew.mods.sanlib.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class EntityRenderScreen
        extends Screen
{
    private final EntityType<?> type;
    private final CompoundNBT nbt;
    private final int color;
    private final int tickTime;

    private Entity e;
    private float scale = 1.0F;
    private float rotation = 45.0F;

    public EntityRenderScreen(EntityType<?> entityType, CompoundNBT nbt, int color, int tickTime) {
        super(new StringTextComponent("Entity Renderer"));

        this.type = entityType;
        this.nbt = nbt;
        this.color = color;
        this.tickTime = tickTime;
    }

    @Override
    public void tick() {
        super.tick();

        if( this.e == null && this.minecraft != null && this.minecraft.level != null ) {
            this.e = this.type.create(this.minecraft.level);
            if( this.e != null ) {
                if( this.nbt != null ) {
                    this.e.load(this.nbt);
                }
                if( this.tickTime >= 0 ) {
                    this.e.tickCount = this.tickTime;
                }
            }
        }

        if( this.e != null && this.tickTime < 0 ) {
            this.e.tickCount++;
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scroll) {
        if( scroll > 0 ) {
            this.scale += 1.0F;

            return true;
        } else if( scroll < 0 ) {
            this.scale -= 1.0F;

            return true;
        }

        return super.mouseScrolled(mx, my, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if( keyCode == GLFW.GLFW_KEY_LEFT ) {
            this.rotation += 5.0F;
        } else if( keyCode == GLFW.GLFW_KEY_RIGHT ) {
            this.rotation -= 5.0F;
        }

        return super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks) {
        this.fillGradient(mStack, 0, 0, this.width, this.height, this.color, this.color);

        if( this.e == null ) {
            return;
        }
        this.e.setYHeadRot(0.0F);

        mStack.pushPose();
        mStack.translate(this.width / 2.0F, this.height / 2.0F, 500.0F);
        mStack.scale(this.scale + 20.0F, this.scale + 20.0F, this.scale + 20.0F);
        mStack.translate(0.0F, this.e.getEyeHeight() / 2.0F, 0.0F);

        mStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        mStack.mulPose(Vector3f.XP.rotationDegrees(22.5F));
        mStack.mulPose(Vector3f.YP.rotationDegrees(135.0F + this.rotation));
        mStack.mulPose(Vector3f.YP.rotationDegrees(-135.0F));


        EntityRendererManager erm = Minecraft.getInstance().getEntityRenderDispatcher();

        erm.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> erm.render(this.e, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, mStack, buffer, 0xF000F0));
        buffer.endBatch();
        erm.setRenderShadow(true);

        mStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
