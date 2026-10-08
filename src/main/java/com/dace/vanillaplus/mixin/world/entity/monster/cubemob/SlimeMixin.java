package com.dace.vanillaplus.mixin.world.entity.monster.cubemob;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public abstract class SlimeMixin extends AbstractCubeMobMixin<Slime> {
    @Inject(method = "addTargetingGoals", at = @At("TAIL"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        if (getConfigComponents().getBoolean(EntityConfigComponentTypes.ATTACK_NPCS))
            targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(getThis(), AbstractVillager.class, true));
    }
}
