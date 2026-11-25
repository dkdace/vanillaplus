package com.dace.vanillaplus.mixin.client.model;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.renderer.entity.state.UndeadRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnimationUtils.class)
public abstract class AnimationUtilsMixin implements VPMixin<AnimationUtils> {
    @Definition(id = "animateAttack", local = @Local(type = boolean.class, name = "animateAttack"))
    @Expression("animateAttack")
    @ModifyExpressionValue(method = "animateZombieArms", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static <T extends UndeadRenderState> boolean modifyAnimateAttackCondition(boolean animateAttack, @Local(argsOnly = true) T state) {
        return animateAttack && !(state.mainArm == HumanoidArm.RIGHT ? state.leftHandItemStack : state.rightHandItemStack).is(Items.SHIELD);
    }
}
