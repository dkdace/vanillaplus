package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.util.IdentifierUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.HealOrHarmMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HealOrHarmMobEffect.class)
public abstract class HealOrHarmMobEffectMixin extends MobEffectMixin<HealOrHarmMobEffect> {
    @Unique
    private static final Identifier HEAL_VALUE_ID = IdentifierUtil.fromPath("heal");
    @Unique
    private static final Identifier DAMAGE_VALUE_ID = IdentifierUtil.fromPath("damage");

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private float modifyHealAmount0(float heal, @Local(argsOnly = true) int amplification) {
        return getValues().calculate(HEAL_VALUE_ID, amplification).orElse(heal);
    }

    @ModifyArg(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private float modifyHealAmount1(float heal, @Local(argsOnly = true) int amplification, @Local(argsOnly = true) double scale) {
        return getValues().calculate(HEAL_VALUE_ID, amplification).map(value -> (float) (value * scale)).orElse(heal);
    }

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 2)
    private float modifyHarmAmount0(float damage, @Local(argsOnly = true) int amplification) {
        return getValues().calculate(DAMAGE_VALUE_ID, amplification).orElse(damage);
    }

    @ModifyArg(method = "applyInstantenousEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 2)
    private float modifyHarmAmount1(float damage, @Local(argsOnly = true) int amplification, @Local(argsOnly = true) double scale) {
        return getValues().calculate(DAMAGE_VALUE_ID, amplification).map(value -> (float) (value * scale)).orElse(damage);
    }
}
