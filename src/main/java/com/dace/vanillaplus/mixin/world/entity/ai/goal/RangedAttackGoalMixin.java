package com.dace.vanillaplus.mixin.world.entity.ai.goal;

import com.dace.vanillaplus.extension.world.entity.ai.goal.VPRangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RangedAttackGoal.class)
public abstract class RangedAttackGoalMixin implements VPRangedAttackGoal {
    @Mutable
    @Shadow
    @Final
    private int attackIntervalMin;
    @Mutable
    @Shadow
    @Final
    private int attackIntervalMax;

    @Override
    public void setMinAttackCooldown(int minAttackCooldown) {
        attackIntervalMin = minAttackCooldown;
    }

    @Override
    public void setMaxAttackCooldown(int maxAttackCooldown) {
        attackIntervalMax = maxAttackCooldown;
    }
}
