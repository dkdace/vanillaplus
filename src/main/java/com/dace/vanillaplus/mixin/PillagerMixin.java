package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Pillager.class)
public final class PillagerMixin {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;<init>(Lnet/minecraft/world/entity/monster/Monster;DF)V"), index = 2)
    private float getAttackRange(float attackRange) {
        return 20;
    }

    @Overwrite
    public void performRangedAttack(LivingEntity entity, float speed) {
        Pillager pillager = (Pillager) (Object) this;
        pillager.performCrossbowAttack(pillager, 2.6F);
    }
}
