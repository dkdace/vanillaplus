package com.dace.vanillaplus.extension.client.sounds;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.NonNull;

/**
 * {@link SoundEngine}을 확장하는 인터페이스.
 */
public interface VPSoundEngine extends VPMixin<SoundEngine> {
    @NonNull
    static VPSoundEngine cast(@NonNull SoundEngine object) {
        return (VPSoundEngine) object;
    }

    /**
     * 지정한 효과음 유형과 시드에 해당하는 효과음을 정지한다.
     *
     * @param soundSource 효과음 유형
     * @param seed        시드
     */
    void stop(@NonNull SoundSource soundSource, long seed);
}
