package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin<T extends LivingEntity, U extends EntityModifier.LivingEntityModifier> extends EntityMixin<T, U> {
    @Shadow
    @Final
    private static ResourceLocation SPRINTING_MODIFIER_ID;

    @Shadow
    protected Brain<?> brain;
    @Unique
    @Nullable
    private DamageSource lastDamageSourceForKnockback;
    @Mutable
    @Shadow
    @Final
    private AttributeMap attributes;

    @Shadow
    public abstract void stopRiding();

    @Shadow
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    @Shadow
    public abstract boolean hasLineOfSight(Entity target);

    @Shadow
    public abstract void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack);

    @Shadow
    public abstract boolean hasItemInSlot(EquipmentSlot equipmentSlot);

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attributeHolder);

    @Shadow
    protected abstract boolean shouldDropLoot(ServerLevel serverLevel);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract boolean isAutoSpinAttack();

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        super.setDataModifier(dataModifier);

        if (dataModifier != null)
            attributes.apply(dataModifier.getPackedAttributes());
    }

    @Unique
    private float getFinalSpeed(float speed) {
        LivingEntity controller = getControllingPassenger();
        return controller == null
                ? speed
                : (float) (speed * controller.getAttributeValue(VPAttributes.VEHICLE_SPEED_MULTIPLIER.getHolder().orElseThrow()));
    }

    @ModifyArg(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z"),
            index = 1)
    private ClipContext.Block modifyLineOfSightClipContextBlock(ClipContext.Block block) {
        return ClipContext.Block.VISUAL;
    }

    @Inject(method = "die", at = @At("TAIL"))
    protected void onDie(DamageSource damageSource, CallbackInfo ci) {
        // 미사용
    }

    @Definition(id = "deathprotection", local = @Local(type = DeathProtection.class))
    @Expression("deathprotection != null")
    @ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    protected boolean canUseTotem(boolean canUse, @Local(ordinal = 1) ItemStack itemStack) {
        return canUse;
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    protected void onUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        // 미사용
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void setLastDamageSourceForKnockback(ServerLevel serverLevel, DamageSource damageSource, float damage,
                                                 CallbackInfoReturnable<Boolean> cir) {
        lastDamageSourceForKnockback = damageSource;
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V",
            shift = At.Shift.AFTER))
    private void removeLastDamageSourceForKnockback(ServerLevel serverLevel, DamageSource damageSource, float damage,
                                                    CallbackInfoReturnable<Boolean> cir) {
        lastDamageSourceForKnockback = null;
    }

    @ModifyExpressionValue(method = "knockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyKnockbackResistance(double original) {
        return VPAttributes.getFinalKnockbackResistance(getThis(), lastDamageSourceForKnockback);
    }

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float modifyDamageAfterArmorAbsorb(float damage, @Local(argsOnly = true) DamageSource damageSource) {
        double damageResistance = getAttributeValue(VPAttributes.ENVIRONMENTAL_DAMAGE_RESISTANCE.getHolder().orElseThrow());
        return (float) (damageSource.is(VPTags.DamageTypes.ENVIRONMENTAL) ? damage * (1 - damageResistance) : damage);
    }

    @ModifyReturnValue(method = "getDamageAfterMagicAbsorb", at = @At(value = "RETURN", ordinal = 3))
    private float modifyFinalDamage(float damage, @Local(argsOnly = true) DamageSource damageSource) {
        if (!(level() instanceof ServerLevel serverLevel))
            return damage;

        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(getThis(), (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyFinalIncomingDamageMultiplier(serverLevel, level, getThis(), damageSource, value));

        return damage * Math.max(value.floatValue(), 0);
    }

    @ModifyReturnValue(method = "getWaterSlowDown", at = @At("RETURN"))
    protected float modifyWaterSlowDown(float value) {
        return value;
    }

    @ModifyExpressionValue(method = "travelInFluid(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D",
                    ordinal = 0))
    private double modifyWaterMovementEfficiencyValue(double value) {
        return isAutoSpinAttack() ? 0 : value;
    }

    @ModifyArg(method = "setSprinting", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
    private AttributeModifier modifySprintingSpeed(AttributeModifier attributeModifier) {
        double sprintingSpeed = getAttributeValue(VPAttributes.SPRINTING_SPEED_MULTIPLIER.getHolder().orElseThrow());
        return new AttributeModifier(SPRINTING_MODIFIER_ID, sprintingSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @ModifyArg(method = "travelFallFlying", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"),
            index = 1)
    private Vec3 modifyFallFlyingSpeed(Vec3 speed) {
        return speed.scale(getAttributeValue(VPAttributes.ELYTRA_FLYING_SPEED_MULTIPLIER.getHolder().orElseThrow()));
    }

    @ModifyArg(method = "travelFlying(Lnet/minecraft/world/phys/Vec3;FFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyFinalFlyingSpeed(float original) {
        return getFinalSpeed(original);
    }

    @ModifyExpressionValue(method = "getFrictionInfluencedSpeed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getSpeed()F"))
    private float modifyFinalAirSpeed0(float original) {
        return getFinalSpeed(original);
    }

    @ModifyExpressionValue(method = "getFrictionInfluencedSpeed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getFlyingSpeed()F"))
    private float modifyFinalAirSpeed1(float original) {
        return getFinalSpeed(original);
    }

    @ModifyArg(method = "travelInFluid(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyFinalFluidSpeed(float original) {
        return getFinalSpeed(original);
    }
}
