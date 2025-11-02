package com.dace.vanillaplus.mixin.world.entity.ai.goal;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedCrossbowAttackGoal.class)
public abstract class RangedCrossbowAttackGoalMixin<T extends Monster & RangedAttackMob & CrossbowAttackMob> implements VPMixin<RangedCrossbowAttackGoal<T>> {
    @Shadow
    @Final
    private T mob;
    @Shadow
    private int seeTime;

    @Definition(id = "pAttackRadius", local = @Local(type = float.class, argsOnly = true))
    @Expression("pAttackRadius")
    @ModifyExpressionValue(method = "<init>", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private float modifyAttackRadius(float original, @Local(argsOnly = true) T monster) {
        return ((EntityModifier.CrossbowAttackMobModifier) EntityModifier.fromEntityTypeOrThrow(monster.getType())).getShootingRange();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;stop()V",
            shift = At.Shift.AFTER))
    private void backupIfTooClose(CallbackInfo ci, @Local LivingEntity target, @Local(ordinal = 2) boolean flag2) {
        if (mob.getControlledVehicle() != null || seeTime < 20 || !target.closerThan(mob, 7))
            return;

        mob.getMoveControl().strafe(-0.75F, 0);
        mob.setYRot(Mth.rotateIfNecessary(mob.getYRot(), mob.yHeadRot, 0));
    }
}
