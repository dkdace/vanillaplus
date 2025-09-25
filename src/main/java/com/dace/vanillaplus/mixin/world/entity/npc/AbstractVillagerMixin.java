package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends MobMixin<EntityModifier.LivingEntityModifier> {
    @Shadow
    public abstract MerchantOffers getOffers();

    @Shadow
    public abstract boolean isTrading();
}
