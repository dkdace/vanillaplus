package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Silverfish.class)
public abstract class SilverfishMixin extends MobMixin<Silverfish, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/entity/player/Player"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(getThis(), AbstractVillager.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(getThis(), IronGolem.class, true));
    }
}
