package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillagerMixin<WanderingTrader, EntityModifier.LivingEntityModifier> {
    @Unique
    private static final float DISTANCE_MELEE = 8;
    @Unique
    private static final float DISTANCE_RANGED = 12;
    @Unique
    private static final float DISTANCE_LONG_RANGED = 15;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addAvoidMonsterGoals(CallbackInfo ci) {
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), AbstractSkeleton.class, DISTANCE_LONG_RANGED, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Witch.class, DISTANCE_RANGED, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Spider.class, DISTANCE_MELEE, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Slime.class, DISTANCE_MELEE, 0.5, 0.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), Silverfish.class, DISTANCE_MELEE, 0.5, 0.5));
    }
}
