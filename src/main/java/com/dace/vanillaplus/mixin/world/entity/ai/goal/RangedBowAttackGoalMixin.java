package com.dace.vanillaplus.mixin.world.entity.ai.goal;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin<T extends Mob & RangedAttackMob> implements VPMixin<RangedBowAttackGoal<T>> {
    @Shadow
    @Final
    private T mob;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;lookAt(Lnet/minecraft/world/entity/Entity;FF)V", shift = At.Shift.AFTER))
    private void lookAtTarget(CallbackInfo ci, @Local LivingEntity target) {
        mob.getLookControl().setLookAt(target, 30, 30);
    }
}
