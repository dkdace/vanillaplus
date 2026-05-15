package com.dace.vanillaplus.extension.world.entity.raid;

import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import lombok.NonNull;
import net.minecraft.world.entity.raid.Raider;

/**
 * {@link Raider}를 확장하는 인터페이스.
 *
 * @param <T> {@link Raider}를 상속받는 타입
 */
public interface VPRaider<T extends Raider> extends VPLivingEntity<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Raider> VPRaider<T> cast(@NonNull T object) {
        return (VPRaider<T>) object;
    }

    /**
     * @see RaiderConfig
     */
    @NonNull
    RaiderConfig getRaiderConfig();
}
