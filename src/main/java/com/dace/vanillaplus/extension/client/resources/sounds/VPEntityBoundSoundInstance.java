package com.dace.vanillaplus.extension.client.resources.sounds;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;

/**
 * {@link EntityBoundSoundInstance}를 확장하는 인터페이스.
 */
public interface VPEntityBoundSoundInstance extends VPMixin<EntityBoundSoundInstance> {
    /**
     * @return 시드
     */
    long getSeed();
}
