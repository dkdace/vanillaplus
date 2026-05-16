package com.dace.vanillaplus.mixin.world.entity.npc.wanderingtrader;

import com.dace.vanillaplus.mixin.world.entity.npc.villager.AbstractVillagerMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillagerMixin<WanderingTrader> {
    @WrapOperation(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"))
    private void removeDefaultAvoidEntityGoals(GoalSelector instance, int prio, Goal goal, Operation<Void> original) {
        if (!(goal instanceof AvoidEntityGoal<?>) || getNpcConfig().avoidEntityDistanceMap().isEmpty())
            original.call(instance, prio, goal);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addAvoidEntityGoals(CallbackInfo ci) {
        Map<EntityType<?>, Integer> avoidEntityDistanceMap = getNpcConfig().avoidEntityDistanceMap();
        if (avoidEntityDistanceMap.isEmpty())
            return;

        HashMap<Integer, HashSet<EntityType<?>>> avoidEntitiesMap = new HashMap<>();
        avoidEntityDistanceMap.forEach((entityType, distance) ->
                avoidEntitiesMap.computeIfAbsent(distance, _ -> new HashSet<>()).add(entityType));

        avoidEntitiesMap.forEach((distance, entityTypes) ->
                goalSelector.addGoal(1, new AvoidEntityGoal<>(getThis(), LivingEntity.class, distance, 0.5, 0.5,
                        target -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && entityTypes.contains(target.getType()))));
    }

    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Definition(id = "random", field = "Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;random:Lnet/minecraft/util/RandomSource;")
    @Expression("3 + this.random.nextInt(4)")
    @ModifyExpressionValue(method = "rewardTradeXp", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyTradePlayerXP(int xp, @Local(argsOnly = true) MerchantOffer offer) {
        return getTradePlayerXP(xp, offer);
    }
}
