package com.dace.vanillaplus.extension.advancements.critereon;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.PlayerPredicate;

import java.util.Optional;

/**
 * {@link PlayerPredicate}를 확장하는 인터페이스.
 */
public interface VPPlayerPredicate extends VPMixin<PlayerPredicate> {
    @NonNull
    static VPPlayerPredicate cast(@NonNull PlayerPredicate object) {
        return (VPPlayerPredicate) (Object) object;
    }

    /**
     * @return 리스폰 위치까지의 거리
     */
    @NonNull
    Optional<DistancePredicate> getDistanceToRespawn();

    /**
     * @param distanceToRespawn 리스폰 위치까지의 거리
     */
    void setDistanceToRespawn(@NonNull Optional<DistancePredicate> distanceToRespawn);
}
