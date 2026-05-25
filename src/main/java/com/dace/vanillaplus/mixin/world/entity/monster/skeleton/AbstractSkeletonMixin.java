package com.dace.vanillaplus.mixin.world.entity.monster.skeleton;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin<T extends AbstractSkeleton> extends MonsterMixin<T> {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        if (getConfigComponents().getBoolean(EntityConfigComponentTypes.ATTACK_NPCS))
            targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(getThis(), AbstractVillager.class, true));
    }
}
