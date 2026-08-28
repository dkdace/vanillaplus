package com.dace.vanillaplus.mixin.client.renderer;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin implements VPMixin<ScreenEffectRenderer> {
    @ModifyArg(method = "lambda$submitFire$0", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;translate(FFF)Lorg/joml/Matrix4f;"), index = 1)
    private static float modifyRenderFireY(float yo) {
        return -0.55F;
    }
}
