package com.dace.vanillaplus.mixin.world.entity.player;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Objects;

@Mixin(Player.class)
public abstract class PlayerMixin<T extends Player> extends LivingEntityMixin<T, EntityModifier.LivingEntityModifier> implements VPPlayer<T> {
    @Unique
    private static final String MINING_FATIGUE_DEFINED_VALUE_NAME = "mining_speed";

    @Unique
    protected boolean isProneKeyDown = false;
    @Unique
    private boolean canDisableBlocking = false;
    @Shadow
    @Final
    private Abilities abilities;
    @Shadow
    @Final
    private ItemCooldowns cooldowns;

    @Override
    protected boolean canUseTotem(boolean hasDeathProtection, ItemStack itemStack) {
        return hasDeathProtection && !cooldowns.isOnCooldown(itemStack);
    }

    @Override
    protected void onUseTotem(DamageSource killingDamage, CallbackInfoReturnable<Boolean> cir, ItemStack protectionItem) {
        UseCooldown useCooldown = protectionItem.get(DataComponents.USE_COOLDOWN);
        if (useCooldown != null)
            useCooldown.apply(protectionItem, getThis());
    }

    @Override
    public float getSecondsToDisableBlocking() {
        return canDisableBlocking ? super.getSecondsToDisableBlocking() : 0;
    }

    @Override
    public void setProneKeyDown(boolean isProneKeyDown) {
        this.isProneKeyDown = isProneKeyDown;
    }

    @ModifyReturnValue(method = "getDesiredPose", at = @At(value = "RETURN", ordinal = 4))
    private Pose modifyDesiredPose(Pose pose) {
        return isProneKeyDown && !abilities.flying && onGround() ? Pose.SWIMMING : pose;
    }

    @ModifyArg(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float modifyFoodExhaustion(float amount) {
        return (float) (amount * getAttributeValue(VPAttributes.FOOD_EXHAUSTION_MULTIPLIER.getHolder().orElseThrow()));
    }

    @Redirect(method = "causeExtraKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    private void removeSprintCancelOnAttack(Player player, boolean isSprinting) {
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> modifyTouchEntities(List<Entity> entities, @Local(name = "pickupArea") AABB pickupArea) {
        double pickupRange = (getAttributeValue(VPAttributes.ITEM_PICKUP_RANGE.getHolder().orElseThrow()) - 1) * 0.5;

        entities.addAll(level().getEntities(getThis(),
                pickupArea.inflate(pickupArea.getXsize() * pickupRange, pickupArea.getYsize() * pickupRange, pickupArea.getZsize() * pickupRange),
                entity -> entity instanceof ItemEntity || entity instanceof AbstractArrow || entity instanceof ExperienceOrb));

        return entities;
    }

    @Redirect(method = "doSweepAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getSweepHitBox(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/AABB;"))
    private AABB modifySweepHitbox(ItemStack instance, Player player, Entity target) {
        double sweepingRange = player.getAttributeValue(VPAttributes.SWEEPING_RANGE.getHolder().orElseThrow());
        return target.getBoundingBox().inflate(sweepingRange, 0.25, sweepingRange);
    }

    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D")
    @Expression("this.distanceToSqr(?) < ?")
    @ModifyExpressionValue(method = "doSweepAttack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean redirectSweepDistanceCheck(boolean original, @Local(name = "nearby") LivingEntity nearby) {
        double yRot = Math.toRadians(-getYRot() + 90);
        double distance = Math.cos(yRot) * (getX() - nearby.getX()) - Math.sin(yRot) * (getZ() - nearby.getZ());

        return distance > 0 && distance < getAttackRangeWith(getItemInHand(InteractionHand.MAIN_HAND)).effectiveMaxRange(getThis());
    }

    @ModifyArgs(method = "doSweepAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private void modifySweepParticleArgs(Args args) {
        args.set(5, getAttributeValue(VPAttributes.SWEEPING_RANGE.getHolder().orElseThrow()));
        args.set(7, 0.0);
        args.set(8, 1.0);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
    private void setCanDisableBlockingOnAttack(Entity entity, CallbackInfo ci, @Local(name = "fullStrengthAttack") boolean fullStrengthAttack) {
        canDisableBlocking = fullStrengthAttack;
    }

    @Inject(method = "stabAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private void setCanDisableBlockingOnStab(EquipmentSlot slot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback,
                                             boolean dismounts, CallbackInfoReturnable<Boolean> cir) {
        canDisableBlocking = true;
    }

    @Definition(id = "scale", local = @Local(type = float.class, name = "scale"))
    @Expression("scale")
    @ModifyExpressionValue(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F",
            at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private float modifyMiningFatigueMultiplier(float multiplier) {
        MobEffectInstance mobEffectInstance = Objects.requireNonNull(getEffect(MobEffects.MINING_FATIGUE));

        return VPMobEffect.cast(mobEffectInstance.getEffect().value()).getDataModifier()
                .map(mobEffectValues -> 1 + mobEffectValues.calculate(MINING_FATIGUE_DEFINED_VALUE_NAME,
                        mobEffectInstance.getAmplifier()))
                .orElse(multiplier);
    }
}
