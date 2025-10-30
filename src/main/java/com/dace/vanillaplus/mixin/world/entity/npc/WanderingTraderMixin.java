package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillagerMixin<WanderingTrader, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/PathfinderMob;D)Lnet/minecraft/world/entity/ai/goal/PanicGoal;"))
    private void addAvoidMonsterGoals(CallbackInfo ci) {
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), AbstractSkeleton.class, 15F, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Witch.class, 12F, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Spider.class, 8F, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Slime.class, 8F, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Silverfish.class, 8F, 0.5, 0.5));
    }
}
