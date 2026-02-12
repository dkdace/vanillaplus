package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin<T extends Mob, U extends EntityModifier.LivingEntityModifier> extends LivingEntityMixin<T, U> {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    public GoalSelector goalSelector;
    @Shadow
    protected JumpControl jumpControl;

    @Shadow
    public abstract PathNavigation getNavigation();

    @Shadow
    public abstract boolean isAggressive();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Shadow
    @UnknownNullability
    protected AABB getAttackBoundingBox(double range) {
        return null;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        if (!level().isClientSide())
            targetSelector.getAvailableGoals().forEach(wrappedGoal -> {
                if (wrappedGoal.getGoal() instanceof HurtByTargetGoal hurtByTargetGoal && hurtByTargetGoal.canUse())
                    hurtByTargetGoal.start();
            });
    }

    @Inject(method = "setAggressive", at = @At("HEAD"))
    private void stopRidingIfAggressive(boolean isAggressive, CallbackInfo ci) {
        if (isAggressive && getVehicle() instanceof VehicleEntity)
            stopRiding();
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void preventRidingIfAggressive(Entity vehicle, boolean force, boolean sendGameEvent, CallbackInfoReturnable<Boolean> cir) {
        if (isAggressive() && vehicle instanceof VehicleEntity)
            cir.setReturnValue(false);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;aiStep()V", shift = At.Shift.AFTER))
    private void jumpIfCannotReachTarget(CallbackInfo ci) {
        if (level().isClientSide())
            return;

        LivingEntity target = getTarget();
        if (target == null)
            return;

        double yDiff = target.getY() - getY();
        double height = getBbHeight();

        if (onGround() && yDiff > height && yDiff < height + 2
                && position().horizontal().closerThan(target.position().horizontal(), getBbWidth() + 1.0))
            jumpControl.jump();
    }

    @ModifyExpressionValue(method = "isWithinMeleeAttackRange", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/Mob;DEFAULT_ATTACK_REACH:D", opcode = Opcodes.GETSTATIC))
    private double modifyFallbackAttackReach(double reach) {
        return reach * getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow());
    }
}
