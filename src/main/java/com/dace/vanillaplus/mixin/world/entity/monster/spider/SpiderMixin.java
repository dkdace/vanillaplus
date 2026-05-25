package com.dace.vanillaplus.mixin.world.entity.monster.spider;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spider.class)
public abstract class SpiderMixin<T extends Spider> extends MonsterMixin<T> {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        if (getConfigComponents().getBoolean(EntityConfigComponentTypes.ATTACK_NPCS))
            targetSelector.addGoal(3, new Spider.SpiderTargetGoal<>(getThis(), AbstractVillager.class));
    }
}
