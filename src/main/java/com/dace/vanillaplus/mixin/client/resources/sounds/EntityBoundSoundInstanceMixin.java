package com.dace.vanillaplus.mixin.client.resources.sounds;

import com.dace.vanillaplus.extension.client.resources.sounds.VPEntityBoundSoundInstance;
import lombok.Getter;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
public abstract class EntityBoundSoundInstanceMixin implements VPEntityBoundSoundInstance {
    @Unique
    @Getter
    private long seed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setSeed(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, Entity entity, long seed, CallbackInfo ci) {
        this.seed = seed;
    }
}
