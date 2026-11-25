package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.entity.VPMob;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
public abstract class MobMixin<T extends Mob> extends LivingEntityMixin<T> implements VPMob<T> {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    public GoalSelector goalSelector;
    @Unique
    @Getter
    private int disableBlockingTime = 0;
    @Unique
    @Nullable
    @Getter
    @Setter
    private BlockPos trialSpawnerPos;

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
    private boolean canStopRiding(@NonNull Entity vehicle) {
        return is(VPTags.EntityTypes.DISMOUNTS_VEHICLE_WHEN_FOLLOWING) && getTarget() != null && vehicle.is(VPTags.EntityTypes.VEHICLES);
    }

    @Override
    protected void blockUsingItem(ServerLevel level, LivingEntity attacker, DamageSource source, float damage) {
        super.blockUsingItem(level, attacker, source, damage);

        ItemStack itemBlockingWith = getItemBlockingWith();
        float secondsToDisableBlocking = attacker.getSecondsToDisableBlocking();
        BlocksAttacks blocksAttacks = itemBlockingWith != null ? itemBlockingWith.get(DataComponents.BLOCKS_ATTACKS) : null;

        if (secondsToDisableBlocking <= 0 || blocksAttacks == null)
            return;

        blocksAttacks.disable(level, getThis(), secondsToDisableBlocking, itemBlockingWith);

        float seconds = secondsToDisableBlocking * blocksAttacks.disableCooldownScale();
        disableBlockingTime = seconds > 0 ? Math.round(seconds * 20) : 0;
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putInt("DisableBlockingTime", disableBlockingTime);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        disableBlockingTime = input.getIntOr("DisableBlockingTime", 0);
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void decreaseDisableBlockingTime(CallbackInfo ci) {
        if (disableBlockingTime > 0)
            disableBlockingTime--;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;aiStep()V", shift = At.Shift.AFTER))
    private void stopRidingIfHasTarget(CallbackInfo ci) {
        if (level().isClientSide())
            return;

        Entity vehicle = getVehicle();
        if (vehicle != null && canStopRiding(vehicle))
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
