package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.util.IdentifierUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.SaturationMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SaturationMobEffect.class)
public abstract class SaturationMobEffectMixin extends MobEffectMixin<SaturationMobEffect> {
    @Unique
    private static final Identifier VALUE_ID = IdentifierUtil.fromPath("food_per_tick");

    @ModifyArg(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), index = 0)
    private int modifyFoodLevel(int food, @Local(argsOnly = true) int amplification) {
        return getConfig().calculate(VALUE_ID, amplification).map(Float::intValue).orElse(food);
    }
}
