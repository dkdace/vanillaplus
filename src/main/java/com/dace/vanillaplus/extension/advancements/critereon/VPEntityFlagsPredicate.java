package com.dace.vanillaplus.extension.advancements.critereon;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;

import java.util.Optional;

/**
 * {@link EntityFlagsPredicate}를 확장하는 인터페이스.
 */
public interface VPEntityFlagsPredicate extends VPMixin<EntityFlagsPredicate> {
    @NonNull
    static VPEntityFlagsPredicate cast(@NonNull EntityFlagsPredicate object) {
        return (VPEntityFlagsPredicate) (Object) object;
    }

    /**
     * @return 회전 공격 여부
     */
    @NonNull
    Optional<Boolean> getIsSpinAttacking();

    /**
     * @param isSpinAttacking 회전 공격 여부
     */
    void setIsSpinAttacking(@NonNull Optional<Boolean> isSpinAttacking);

    /**
     * @return 비에 닿았는지 여부
     */
    @NonNull
    Optional<Boolean> getIsInRain();

    /**
     * @param isInRain 비에 닿았는지 여부
     */
    void setIsInRain(@NonNull Optional<Boolean> isInRain);
}
