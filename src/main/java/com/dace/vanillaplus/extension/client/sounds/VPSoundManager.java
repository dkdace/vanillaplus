package com.dace.vanillaplus.extension.client.sounds;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.jspecify.annotations.NonNull;

/**
 * {@link SoundManager}를 확장하는 인터페이스.
 */
public interface VPSoundManager extends VPMixin<SoundManager> {
    @NonNull
    static VPSoundManager cast(@NonNull SoundManager object) {
        return (VPSoundManager) object;
    }

    /**
     * @return {@link SoundEngine}
     */
    @NonNull
    SoundEngine getSoundEngine();
}
