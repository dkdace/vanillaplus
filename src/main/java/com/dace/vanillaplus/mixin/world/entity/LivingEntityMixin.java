package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.world.item.TridentConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin<T extends LivingEntity> extends EntityMixin<T> implements VPLivingEntity<T> {
    @Unique
    private static final Identifier RESISTANCE_EFFECT_VALUE_ID = IdentifierUtil.fromPath("resistance");
    @Unique
    private static final Identifier JUMP_STRENGTH_EFFECT_VALUE_ID = IdentifierUtil.fromPath("jump_strength");
    @Unique
    private static final int RENDER_HEALTH_DURATION = 60;
    @Shadow
    @Final
    private static Identifier SPRINTING_MODIFIER_ID;

    @Shadow
    protected Brain<?> brain;
    @Shadow
    protected int attackStrengthTicker;
    @Shadow
    @Final
    private AttributeMap attributes;
    @Unique
    @Nullable
    private DamageSource lastDamageSourceForKnockback;
    @Unique
    private int renderHealthTick = 0;

    @Shadow
    public abstract void setSprinting(boolean isSprinting);

    @Shadow
    public abstract void stopRiding();

    @Shadow
    public abstract boolean hasLineOfSight(Entity target);

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow
    public abstract void setItemSlot(EquipmentSlot slot, ItemStack itemStack);

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Shadow
    protected abstract boolean shouldDropLoot(ServerLevel level);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract boolean isAutoSpinAttack();

    @Shadow
    public void die(DamageSource source) {
    }

    @Shadow
    public abstract AttackRange getAttackRangeWith(ItemStack weaponItem);

    @Shadow
    public float getSecondsToDisableBlocking() {
        return 0;
    }

    @Shadow
    public abstract void stopUsingItem();

    @Unique
    private float getFinalSpeed(float speed) {
        LivingEntity controller = getControllingPassenger();
        return controller == null
                ? speed
                : (float) (speed * controller.getAttributeValue(VPAttributes.VEHICLE_SPEED_MULTIPLIER.getHolder().orElseThrow()));
    }

    @Override
    public double getFinalKnockbackResistance(double knockbackResistance, @Nullable DamageSource damageSource) {
        return Math.max(knockbackResistance, damageSource != null && damageSource.is(DamageTypeTags.IS_PROJECTILE)
                ? getAttributeValue(VPAttributes.PROJECTILE_KNOCKBACK_RESISTANCE.getHolder().orElseThrow())
                : 0);
    }

    @Override
    public void updateRenderHealth() {
        renderHealthTick = RENDER_HEALTH_DURATION;
    }

    @Override
    public boolean canRenderHealth() {
        return renderHealthTick > 0 || hasEffect(MobEffects.GLOWING);
    }

    @Definition(id = "invulnerableTime", field = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I")
    @Expression("this.invulnerableTime > 0")
    @Inject(method = "baseTick", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private void decreaseRenderHealthTick(CallbackInfo ci) {
        if (renderHealthTick > 0)
            renderHealthTick--;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void applyAttributes(EntityType<? extends LivingEntity> type, Level level, CallbackInfo ci) {
        getConfigComponents().get(EntityConfigComponentTypes.ATTRIBUTES).ifPresent(attributes::apply);
    }

    @ModifyArg(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z"),
            index = 1)
    private ClipContext.Block modifyLineOfSightClipContextBlock(ClipContext.Block blockCollidingContext) {
        return getConfigComponents().getBoolean(EntityConfigComponentTypes.SEE_THROUGH_TRANSPARENT_BLOCKS)
                ? ClipContext.Block.VISUAL
                : blockCollidingContext;
    }

    @Definition(id = "protection", local = @Local(type = DeathProtection.class, name = "protection"))
    @Expression("protection != null")
    @ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    protected boolean canUseTotem(boolean hasDeathProtection, @Local(name = "itemStack") ItemStack itemStack) {
        return hasDeathProtection;
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    protected void onUseTotem(DamageSource killingDamage, CallbackInfoReturnable<Boolean> cir,
                              @Local(name = "protectionItem") ItemStack protectionItem) {
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void setLastDamageSourceForKnockback(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        lastDamageSourceForKnockback = source;
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V",
            shift = At.Shift.AFTER))
    private void removeLastDamageSourceForKnockback(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        lastDamageSourceForKnockback = null;
    }

    @ModifyExpressionValue(method = "knockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyKnockbackResistance(double knockbackResistance) {
        return getFinalKnockbackResistance(knockbackResistance, lastDamageSourceForKnockback);
    }

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float modifyDamageAfterArmorAbsorb(float damage, @Local(argsOnly = true) DamageSource damageSource) {
        double damageResistance = getAttributeValue(VPAttributes.ENVIRONMENTAL_DAMAGE_RESISTANCE.getHolder().orElseThrow());
        return (float) (damageSource.is(VPTags.DamageTypes.ENVIRONMENTAL) ? damage * (1 - damageResistance) : damage);
    }

    @Definition(id = "absorbValue", local = @Local(type = int.class, name = "absorbValue"))
    @Expression("absorbValue")
    @ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyResistanceMultiplier(int absorbValue) {
        MobEffectInstance mobEffectInstance = Objects.requireNonNull(this.getEffect(MobEffects.RESISTANCE));

        return VPMobEffect.cast(mobEffectInstance.getEffect().value()).getConfig()
                .calculate(RESISTANCE_EFFECT_VALUE_ID, mobEffectInstance.getAmplifier())
                .map(Float::intValue)
                .orElse(absorbValue);
    }

    @ModifyReturnValue(method = "getDamageAfterMagicAbsorb", at = @At(value = "RETURN", ordinal = 3))
    private float modifyFinalDamage(float damage, @Local(argsOnly = true) DamageSource damageSource) {
        if (!(level() instanceof ServerLevel serverLevel))
            return damage;

        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(getThis(), (enchantmentHolder, level, _) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyFinalIncomingDamageMultiplier(serverLevel, level, getThis(), damageSource, value));

        return damage * Math.max(value.floatValue(), 0);
    }

    @WrapOperation(method = "travelInWater", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 0))
    private double modifyWaterMovementEfficiencyValue(LivingEntity instance, Holder<Attribute> attribute, Operation<Double> original) {
        return isAutoSpinAttack() ? 0 : original.call(instance, attribute);
    }

    @ModifyArg(method = "setSprinting", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
    private AttributeModifier modifySprintingSpeed(AttributeModifier modifier) {
        double sprintingSpeed = getAttributeValue(VPAttributes.SPRINTING_SPEED.getHolder().orElseThrow());
        return new AttributeModifier(SPRINTING_MODIFIER_ID, sprintingSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @ModifyArg(method = "travelFallFlying", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"),
            index = 1)
    private Vec3 modifyFallFlyingSpeed(Vec3 delta) {
        return delta.scale(getAttributeValue(VPAttributes.ELYTRA_FLYING_SPEED_MULTIPLIER.getHolder().orElseThrow()));
    }

    @ModifyArg(method = "travelFlying(Lnet/minecraft/world/phys/Vec3;FFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyFinalFlyingSpeed(float speed) {
        return getFinalSpeed(speed);
    }

    @ModifyExpressionValue(method = "getFrictionInfluencedSpeed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getSpeed()F"))
    private float modifyFinalAirSpeed0(float speed) {
        return getFinalSpeed(speed);
    }

    @ModifyExpressionValue(method = "getFrictionInfluencedSpeed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getFlyingSpeed()F"))
    private float modifyFinalAirSpeed1(float speed) {
        return getFinalSpeed(speed);
    }

    @ModifyArg(method = "travelInWater", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float modifyFinalFluidSpeed(float speed) {
        return getFinalSpeed(speed);
    }

    @WrapWithCondition(method = "checkAutoSpinAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;autoSpinAttackTicks:I",
            ordinal = 0, opcode = Opcodes.PUTFIELD))
    private boolean removeAutoSpinAttackTickReset(LivingEntity instance, int value) {
        return !TridentConfig.get().riptidePiercing();
    }

    @WrapWithCondition(method = "checkAutoSpinAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private boolean removeAutoSpinAttackCollision(LivingEntity instance, Vec3 deltaMovement) {
        return !TridentConfig.get().riptidePiercing();
    }

    @Expression("0.1 * (? + 1.0)")
    @ModifyExpressionValue(method = "getJumpBoostPower", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float modifyJumpBoostPower(float power) {
        MobEffectInstance mobEffectInstance = Objects.requireNonNull(getEffect(MobEffects.JUMP_BOOST));

        return VPMobEffect.cast(mobEffectInstance.getEffect().value()).getConfig()
                .calculate(JUMP_STRENGTH_EFFECT_VALUE_ID, mobEffectInstance.getAmplifier())
                .orElse(power);
    }
}
