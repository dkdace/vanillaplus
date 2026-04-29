package com.dace.vanillaplus.mixin.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.SaturationMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SaturationMobEffect.class)
public abstract class SaturationMobEffectMixin extends MobEffectMixin<SaturationMobEffect> {
    @Unique
    private static final String DEFINED_VALUE_NAME = "food_per_tick";

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), index = 0)
    private int modifyFoodLevel(int food, @Local(argsOnly = true) int amplification) {
        return getDataModifier()
                .map(mobEffectValues -> (int) mobEffectValues.calculate(DEFINED_VALUE_NAME, amplification))
                .orElse(food);
    }
}
