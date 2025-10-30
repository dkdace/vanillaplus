package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.util.ReflectionUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;

@Mixin(Spider.class)
public abstract class SpiderMixin<T extends Spider, U extends EntityModifier.LivingEntityModifier> extends MonsterMixin<T, U> {
    @Inject(method = "registerGoals", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/entity/animal/IronGolem"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        try {
            Class<?> spiderTargetGoalClass = ReflectionUtil.getClass("net.minecraft.world.entity.monster.Spider$SpiderTargetGoal");
            Constructor<?> spiderTargetGoalConstructor = ReflectionUtil.getConstructor(spiderTargetGoalClass, Spider.class, Class.class);

            targetSelector.addGoal(3, (Goal) spiderTargetGoalConstructor.newInstance(this, AbstractVillager.class));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
