package com.dace.vanillaplus.mixin.world.effect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.RegenerationMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegenerationMobEffect.class)
public abstract class RegenerationMobEffectMixin extends MobEffectMixin<RegenerationMobEffect> {
    @Unique
    private static final String DEFINED_VALUE_NAME = "heal_per_tick";

    @Definition(id = "p_298417_", local = @Local(type = int.class, ordinal = 1, argsOnly = true))
    @Expression("50 >> p_298417_")
    @ModifyExpressionValue(method = "shouldApplyEffectTickThisTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyTicks(int ticks, @Local(ordinal = 1, argsOnly = true) int amplifier) {
        return getLevelBasedValuePreset()
                .map(levelBasedValuePreset -> (int) (1 / levelBasedValuePreset.calculate(DEFINED_VALUE_NAME, amplifier + 1)))
                .orElse(ticks);
    }
}
