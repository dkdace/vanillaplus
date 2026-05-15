package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.monster.illager.Vindicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllagerMixin<Vindicator> {
    @Inject(method = "applyRaidBuffs", at = @At("TAIL"))
    private void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain, CallbackInfo ci) {
        applyCustomRaidBuffs();
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 3), index = 1)
    private Goal modifyOpenDoorGoal(Goal goal) {
        return getRaiderConfig().alwaysOpenDoors() ? new OpenDoorGoal(getThis(), false) : goal;
    }

    @Definition(id = "hasGroundPathNavigation", method = "Lnet/minecraft/world/entity/ai/util/GoalUtils;hasGroundPathNavigation(Lnet/minecraft/world/entity/Mob;)Z")
    @Expression("hasGroundPathNavigation(this)")
    @ModifyExpressionValue(method = "customServerAiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean removeCanOpenDoorsCondition(boolean hasGroundPathNavigation) {
        return !getRaiderConfig().alwaysOpenDoors() && hasGroundPathNavigation;
    }
}
