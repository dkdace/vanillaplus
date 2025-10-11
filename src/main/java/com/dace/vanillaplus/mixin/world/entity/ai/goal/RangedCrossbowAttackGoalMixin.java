package com.dace.vanillaplus.mixin.world.entity.ai.goal;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
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

import java.util.Objects;

@Mixin(RangedCrossbowAttackGoal.class)
public abstract class RangedCrossbowAttackGoalMixin<T extends Monster & RangedAttackMob & CrossbowAttackMob> {
    @Definition(id = "p_25816_", local = @Local(type = float.class, argsOnly = true))
    @Expression("p_25816_")
    @ModifyExpressionValue(method = "<init>", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private float modifyAttackRange(float original, @Local(argsOnly = true) T monster) {
        return Objects.requireNonNull((EntityModifier.CrossbowAttackMobModifier) VPModifiableData.getDataModifier(monster.getType()))
                .getShootingRange();
    }
}
