package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.ai.goal.VPRangedAttackGoal;
import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
import com.dace.vanillaplus.world.entity.monster.WitchConfig;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public abstract class WitchMixin extends RaiderMixin<Witch> {
    @Unique
    private static final int SUPPORT_HEALING_HEALTH = 8;

    @Unique
    @Nullable
    private RangedAttackGoal rangedAttackGoal;
    @Unique
    private int attackCooldown;
    @Unique
    @Nullable
    private NearestAttackableWitchTargetGoal<IronGolem> attackIronGolemGoal;
    @Unique
    @Nullable
    private NearestAttackableWitchTargetGoal<AbstractVillager> attackVillagersGoal;

    @Unique
    private void setCanAttack(boolean canAttack) {
        if (attackVillagersGoal != null)
            attackVillagersGoal.setCanAttack(canAttack);
        if (attackIronGolemGoal != null)
            attackIronGolemGoal.setCanAttack(canAttack);
    }

    @Inject(method = "applyRaidBuffs", at = @At("TAIL"))
    private void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain, CallbackInfo ci) {
        applyCustomRaidBuffs();
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addGoals(CallbackInfo ci) {
        if (RaiderConfig.get(getThis()).alwaysOpenDoors())
            goalSelector.addGoal(2, new OpenDoorGoal(getThis(), false));

        if (!getConfigComponents().getBoolean(EntityConfigComponentTypes.ATTACK_NPCS))
            return;

        attackVillagersGoal = new NearestAttackableWitchTargetGoal<>(getThis(), AbstractVillager.class, 10, true, false,
                null);
        attackIronGolemGoal = new NearestAttackableWitchTargetGoal<>(getThis(), IronGolem.class, 10, true, false,
                null);
        targetSelector.addGoal(3, attackVillagersGoal);
        targetSelector.addGoal(3, attackIronGolemGoal);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Witch> type, Level level, CallbackInfo ci) {
        if (RaiderConfig.get(getThis()).alwaysOpenDoors())
            getNavigation().setCanOpenDoors(true);
    }

    @WrapOperation(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/monster/RangedAttackMob;DIF)Lnet/minecraft/world/entity/ai/goal/RangedAttackGoal;"))
    private RangedAttackGoal initAttackCooldown(RangedAttackMob mob, double speedModifier, int attackInterval, float attackRadius,
                                                Operation<RangedAttackGoal> original) {
        rangedAttackGoal = original.call(mob, speedModifier, attackInterval, attackRadius);
        attackCooldown = attackInterval;

        return rangedAttackGoal;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableWitchTargetGoal;setCanAttack(Z)V", ordinal = 0))
    private void setAttackGoalCanAttackTrue(CallbackInfo ci) {
        setCanAttack(true);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableWitchTargetGoal;setCanAttack(Z)V", ordinal = 1))
    private void setAttackGoalCanAttackFalse(CallbackInfo ci) {
        setCanAttack(false);
    }

    @ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "CONSTANT", args = "floatValue=4.0"))
    private float modifySupportHealthCondition(float health) {
        return WitchConfig.get().supportPotionModifiers().isEmpty() ? health : SUPPORT_HEALING_HEALTH;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack modifyPotionSelf(ItemStack itemStack) {
        return WitchConfig.get().selfPotionModifiers().apply(getThis(), itemStack);
    }

    @ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack modifyPotionThrow(ItemStack itemStack, @Local(argsOnly = true) LivingEntity target) {
        WitchConfig witchConfig = WitchConfig.get();
        return (target instanceof Raider ? witchConfig.supportPotionModifiers() : witchConfig.attackPotionModifiers()).apply(getThis(), itemStack);
    }

    @Inject(method = "performRangedAttack", at = @At("TAIL"))
    private void setAttackCooldown(LivingEntity target, float power, CallbackInfo ci) {
        if (rangedAttackGoal == null)
            return;

        float multiplier = WitchConfig.get().attackCooldownMultipliers().apply(getThis(), 1F);

        VPRangedAttackGoal vpRangedAttackGoal = VPRangedAttackGoal.cast(rangedAttackGoal);
        vpRangedAttackGoal.setMinAttackCooldown((int) (attackCooldown * multiplier));
        vpRangedAttackGoal.setMaxAttackCooldown((int) (attackCooldown * multiplier));
    }
}
