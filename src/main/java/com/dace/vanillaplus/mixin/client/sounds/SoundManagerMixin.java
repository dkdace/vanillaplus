package com.dace.vanillaplus.mixin.client.sounds;

import com.dace.vanillaplus.extension.client.sounds.VPSoundManager;
import lombok.Getter;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin implements VPSoundManager {
    @Shadow
    @Final
    @Getter
    private SoundEngine soundEngine;
}
