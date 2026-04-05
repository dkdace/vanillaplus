package com.dace.vanillaplus.mixin.client.sounds;

import com.dace.vanillaplus.extension.client.resources.sounds.VPEntityBoundSoundInstance;
import com.dace.vanillaplus.extension.client.sounds.VPSoundEngine;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements VPSoundEngine {
    @Shadow
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow
    public abstract void stop(SoundInstance soundInstance);

    @Override
    public void stop(@NonNull SoundSource soundSource, long seed) {
        instanceBySource.get(soundSource).forEach(soundInstance -> {
            if (soundInstance instanceof VPEntityBoundSoundInstance vpEntityBoundSoundInstance && vpEntityBoundSoundInstance.getSeed() == seed)
                stop(soundInstance);
        });
    }

    @ModifyExpressionValue(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/Sound;getAttenuationDistance()I"))
    private int modifyAttenuationDistance(int attenuationDistance) {
        LocalPlayer player = Minecraft.getInstance().player;

        return player == null
                ? attenuationDistance
                : (int) (attenuationDistance * player.getAttributeValue(VPAttributes.HEARING_RANGE.getHolder().orElseThrow()));
    }
}
