package com.dace.vanillaplus.mixin.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.HealOrHarmMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HealOrHarmMobEffect.class)
public abstract class HealOrHarmMobEffectMixin extends MobEffectMixin<HealOrHarmMobEffect> {
    @Unique
    private static final String DEFINED_VALUE_NAME = "amount";

    @Unique
    private float getAmount(float amount, int amplification) {
        return getDataModifier()
                .map(mobEffectValues -> mobEffectValues.calculate(DEFINED_VALUE_NAME, amplification))
                .orElse(amount);
    }

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private float modifyHealAmount0(float heal, @Local(argsOnly = true) int amplification) {
        return getAmount(heal, amplification);
    }

    @ModifyArg(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private float modifyHealAmount1(float heal, @Local(argsOnly = true) int amplification, @Local(argsOnly = true) double scale) {
        return (float) (getAmount(heal, amplification) * scale);
    }

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 2)
    private float modifyHarmAmount0(float damage, @Local(argsOnly = true) int amplification) {
        return getAmount(damage, amplification);
    }

    @ModifyArg(method = "applyInstantenousEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 2)
    private float modifyHarmAmount1(float damage, @Local(argsOnly = true) int amplification, @Local(argsOnly = true) double scale) {
        return (float) (getAmount(damage, amplification) * scale);
    }
}
