package com.dace.vanillaplus.mixin.world.entity.ai.goal;

import com.dace.vanillaplus.rebalance.modifier.DataModifiers;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RangedCrossbowAttackGoal.class)
public abstract class RangedCrossbowAttackGoalMixin<T extends Monster & RangedAttackMob & CrossbowAttackMob> {
    @Definition(id = "p_25816_", local = @Local(type = float.class, argsOnly = true))
    @Expression("p_25816_")
    @ModifyExpressionValue(method = "<init>", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private float init(float original, @Local(argsOnly = true) T monster) {
        return ((EntityModifier.CrossbowAttackMobModifier) DataModifiers.get(monster.registryAccess(), DataModifiers.ENTITY_MODIFIER_MAP,
                monster.getType())).getShootingRange();
    }
}
