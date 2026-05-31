package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.util.IdentifierUtil;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.RegenerationMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegenerationMobEffect.class)
public abstract class RegenerationMobEffectMixin extends MobEffectMixin<RegenerationMobEffect> {
    @Unique
    private static final Identifier VALUE_ID = IdentifierUtil.fromPath("heal_per_tick");

    @Definition(id = "amplification", local = @Local(type = int.class, ordinal = 1, argsOnly = true))
    @Expression("50 >> amplification")
    @ModifyExpressionValue(method = "shouldApplyEffectTickThisTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyInterval(int interval, @Local(ordinal = 1, argsOnly = true) int amplification) {
        return getConfig().calculate(VALUE_ID, amplification).map(value -> (int) (1 / value)).orElse(interval);
    }
}
