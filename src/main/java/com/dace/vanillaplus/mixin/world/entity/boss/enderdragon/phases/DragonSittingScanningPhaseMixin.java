package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DragonSittingScanningPhase.class)
public abstract class DragonSittingScanningPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Unique
    private static final int SCAN_DISTANCE_MIN = 6;
    @Unique
    private static final int SCAN_DISTANCE_MAX = 14;

    @ModifyReturnValue(method = "lambda$new$0", at = @At("RETURN"))
    private static boolean modifyScanTargetingConditionsSelector(boolean condition, @Local(argsOnly = true) EnderDragon dragon,
                                                                 @Local(argsOnly = true) LivingEntity target) {
        return VPEnderDragon.cast(dragon).getEnderDragonConfig().phaseInfo().isPresent()
                ? condition && dragon.distanceToSqr(target) > SCAN_DISTANCE_MIN * SCAN_DISTANCE_MIN
                : condition;
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;selector(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions$Selector;)Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;"))
    private static TargetingConditions modifyScanTargetingConditions(TargetingConditions instance, TargetingConditions.Selector selector,
                                                                     Operation<TargetingConditions> original,
                                                                     @Local(argsOnly = true) EnderDragon dragon) {
        return VPEnderDragon.cast(dragon).getEnderDragonConfig().phaseInfo().isPresent()
                ? instance.range(SCAN_DISTANCE_MAX).ignoreLineOfSight().ignoreInvisibilityTesting()
                : original.call(instance, selector);
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=25"))
    private int modifyScanDuration(int duration) {
        return getVPEnderDragon().getEnderDragonConfig().phaseInfo().map(phaseInfo -> phaseInfo.sitting().scanDuration()).orElse(duration);
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "floatValue=0.8"))
    private float modifyRotationMultiplier(float multiplier) {
        return getVPEnderDragon().getEnderDragonConfig().phaseInfo().isPresent() ? 0.98F : multiplier;
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int modifyScanningIdleDuration(int duration) {
        return getVPEnderDragon().getEnderDragonConfig().phaseInfo()
                .map(phaseInfo -> (int) phaseInfo.sitting().scanIdleDuration().get(dragon))
                .orElse(duration);
    }

    @ModifyArg(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;",
            ordinal = 1), index = 0)
    private TargetingConditions modifyChargeTargetConditions(TargetingConditions targetConditions) {
        return getVPEnderDragon().getEnderDragonConfig().phaseInfo().isPresent()
                ? getVPEnderDragon().getDefaultTargetingConditions()
                : targetConditions;
    }
}
