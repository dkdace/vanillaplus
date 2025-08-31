package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.entity.monster.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Pillager.class)
public abstract class PillagerMixin {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;<init>(Lnet/minecraft/world/entity/monster/Monster;DF)V"), index = 2)
    private float modifyAttackRange(float attackRange) {
        return Rebalance.Crossbow.MOB_SHOOTING_RANGE;
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Pillager;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"), index = 1)
    private float modifyBulletSpeed(float speed) {
        return Rebalance.Crossbow.MOB_SHOOTING_POWER_ARROW;
    }
}
