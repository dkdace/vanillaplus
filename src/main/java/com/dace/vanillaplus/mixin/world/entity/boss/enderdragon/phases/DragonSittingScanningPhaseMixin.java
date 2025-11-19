package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPEnderDragon;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DragonSittingScanningPhase.class)
public abstract class DragonSittingScanningPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @ModifyReturnValue(method = "lambda$new$0", at = @At("RETURN"))
    private static boolean modifyScanTargetingConditionsSelector(boolean original, @Local(argsOnly = true) EnderDragon enderDragon,
                                                                 @Local(argsOnly = true) LivingEntity entity) {
        return original && enderDragon.distanceToSqr(entity) > 36;
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;selector(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions$Selector;)Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;"))
    private static TargetingConditions modifyScanTargetingConditions(TargetingConditions targetingConditions) {
        return targetingConditions.range(14).ignoreLineOfSight().ignoreInvisibilityTesting();
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=25"))
    private int modifyScanDuration(int duration) {
        return ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(dragon.getType())).getPhaseInfo().getSitting()
                .getScanDuration();
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "floatValue=0.8"))
    private float modifyRotationMultiplier(float multiplier) {
        return 0.98F;
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int modifyScanningIdleTime(int time) {
        float scanIdleDurationSeconds = ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(dragon.getType())).getPhaseInfo()
                .getSitting().getScanIdleDurationSeconds().get(dragon);

        return (int) (scanIdleDurationSeconds * 20.0);
    }

    @ModifyArg(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;",
            ordinal = 1), index = 0)
    private TargetingConditions modifyChargeTargetingConditions(TargetingConditions targetingConditions) {
        return VPEnderDragon.cast(dragon).getDefaultTargetingConditions();
    }
}
