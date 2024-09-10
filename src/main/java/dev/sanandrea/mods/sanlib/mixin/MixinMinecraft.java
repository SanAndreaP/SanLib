package dev.sanandrea.mods.sanlib.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    @Inject(method="createTitle", at = @At("RETURN"), cancellable = true)
    private void addSplashTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cir.getReturnValue() + " - Test!!!");
    }
}
