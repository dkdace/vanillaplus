package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public abstract class SlimeMixin extends MobMixin<Slime, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(getThis(), AbstractVillager.class, true));
    }

    @Definition(id = "pEntity", local = @Local(type = Entity.class, argsOnly = true))
    @Definition(id = "IronGolem", type = IronGolem.class)
    @Expression("pEntity instanceof IronGolem")
    @ModifyExpressionValue(method = "push", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyPushConditions(boolean original, @Local(argsOnly = true) Entity entity) {
        return original || entity instanceof AbstractVillager;
    }
}
