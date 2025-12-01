package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
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

    @Definition(id = "random", field = "Lnet/minecraft/world/entity/npc/WanderingTrader;random:Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("3 + this.random.nextInt(4)")
    @ModifyExpressionValue(method = "rewardTradeXp", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyRewardXP(int xp, @Local(argsOnly = true) MerchantOffer merchantOffer) {
        ItemCost itemCost = merchantOffer.getItemCostA();

        if (itemCost.itemStack().is(Items.EMERALD)) {
            int count = itemCost.count();
            return 3 * count + random.nextInt(4 * count);
        }

        return xp;
    }
}
