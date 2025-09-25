package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin<T extends EntityModifier.LivingEntityModifier> extends LivingEntityMixin<T> {
    @Shadow
    protected JumpControl jumpControl;

    @Shadow
    public abstract boolean isAggressive();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Inject(method = "setAggressive", at = @At("HEAD"))
    private void stopRidingIfAggressive(boolean isAggressive, CallbackInfo ci) {
        if (isAggressive && getVehicle() instanceof VehicleEntity)
            stopRiding();
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void preventRidingIfAggressive(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (isAggressive() && vehicle instanceof VehicleEntity)
            cir.setReturnValue(false);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStep(CallbackInfo ci) {
        LivingEntity target = getTarget();

        if (target == null)
            return;

        double yDiff = target.getY() - getY();
        double height = getBbHeight();

        if (!onGround() || !hasLineOfSight(target) || yDiff < height || yDiff >= height + 2
                || !position().horizontal().closerThan(target.position().horizontal(), getBbWidth() + 0.6))
            return;

        jumpControl.jump();
    }

    @ModifyReturnValue(method = "getAttackBoundingBox", at = @At("RETURN"))
    protected AABB modifyAttackBoundingBox(AABB aabb) {
        return aabb;
    }
}
