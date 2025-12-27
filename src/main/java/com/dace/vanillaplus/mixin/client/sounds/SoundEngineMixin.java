package com.dace.vanillaplus.mixin.client.sounds;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements VPMixin<SoundEngine> {
    @ModifyExpressionValue(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/Sound;getAttenuationDistance()I"))
    private int modifyAttenuationDistance(int attenuationDistance) {
        LocalPlayer player = Minecraft.getInstance().player;

        return player == null
                ? attenuationDistance
                : (int) (attenuationDistance * player.getAttributeValue(VPAttributes.HEARING_RANGE.getHolder().orElseThrow()));
    }
}
