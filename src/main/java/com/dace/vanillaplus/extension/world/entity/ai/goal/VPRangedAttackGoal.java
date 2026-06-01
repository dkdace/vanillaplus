package com.dace.vanillaplus.extension.world.entity.ai.goal;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

/**
 * {@link RangedAttackGoal}을 확장하는 인터페이스.
 */
public interface VPRangedAttackGoal extends VPMixin<RangedAttackGoal> {
    @NonNull
    static VPRangedAttackGoal cast(@NonNull RangedAttackGoal object) {
        return (VPRangedAttackGoal) object;
    }

    /**
     * @param minAttackCooldown 최소 공격 쿨타임
     */
    void setMinAttackCooldown(int minAttackCooldown);

    /**
     * @param maxAttackCooldown 최대 공격 쿨타임
     */
    void setMaxAttackCooldown(int maxAttackCooldown);
}
