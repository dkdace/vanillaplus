package com.dace.vanillaplus.mixin.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.HungerMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HungerMobEffect.class)
public abstract class HungerMobEffectMixin extends MobEffectMixin<HungerMobEffect> {
    @Unique
    private static final String DEFINED_VALUE_NAME = "exhaustion_per_tick";

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private float modifyExhaustion(float amount, @Local(argsOnly = true) int amplification) {
        return getDataModifier()
                .map(mobEffectValues -> mobEffectValues.calculate(DEFINED_VALUE_NAME, amplification))
                .orElse(amount);
    }
}
