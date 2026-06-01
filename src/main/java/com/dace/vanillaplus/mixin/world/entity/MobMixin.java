package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.storage.loot.LootTable;
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

import java.util.Map;

@Mixin(Mob.class)
public abstract class MobMixin<T extends Mob> extends LivingEntityMixin<T> implements EquipmentUser {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    public GoalSelector goalSelector;

    @Shadow
    public abstract MoveControl getMoveControl();

    @Shadow
    public abstract PathNavigation getNavigation();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Shadow
    protected AABB getAttackBoundingBox(double horizontalExpansion) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected abstract void customServerAiStep(ServerLevel level);

    @Shadow
    public abstract void equip(ResourceKey<LootTable> lootTable, Map<EquipmentSlot, Float> dropChances);

    @Unique
    private boolean canStopRiding(@Nullable Entity vehicle) {
        return getConfigComponents().getBoolean(EntityConfigComponentTypes.PREVENT_RIDING_IF_HAS_TARGET) && getTarget() != null
                && vehicle instanceof VehicleEntity;
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
