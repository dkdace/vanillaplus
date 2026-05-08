package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.entity.VPMob;
import com.dace.vanillaplus.world.entity.modifier.MobModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lombok.NonNull;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin<T extends Mob, U extends MobModifier> extends LivingEntityMixin<T, U> implements VPMob<T, U> {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    public GoalSelector goalSelector;

    @Shadow
    public abstract PathNavigation getNavigation();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Shadow
    protected AABB getAttackBoundingBox(double horizontalExpansion) {
        throw new UnsupportedOperationException();
    }

    @Unique
    private boolean canStopRiding(@Nullable Entity vehicle) {
        return getDataModifier().preventRidingIfHasTarget() && getTarget() != null && vehicle instanceof VehicleEntity;
    }

    @Override
    @NonNull
    public MobModifier getDefaultDataModifier() {
        return MobModifier.DEFAULT;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (!level().isClientSide())
            targetSelector.getAvailableGoals().forEach(wrappedGoal -> {
                if (wrappedGoal.getGoal() instanceof HurtByTargetGoal hurtByTargetGoal && hurtByTargetGoal.canUse())
                    hurtByTargetGoal.start();
            });
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;aiStep()V", shift = At.Shift.AFTER))
    private void stopRidingIfHasTarget(CallbackInfo ci) {
        if (!level().isClientSide() && canStopRiding(getVehicle()))
            stopRiding();
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void preventRidingIfHasTarget(Entity entity, boolean force, boolean sendEventAndTriggers, CallbackInfoReturnable<Boolean> cir) {
        if (canStopRiding(entity))
            cir.setReturnValue(false);
    }

    @ModifyExpressionValue(method = "isWithinMeleeAttackRange", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/Mob;DEFAULT_ATTACK_REACH:D", opcode = Opcodes.GETSTATIC))
    private double modifyFallbackAttackReach(double reach) {
        return reach * getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow());
    }
}
