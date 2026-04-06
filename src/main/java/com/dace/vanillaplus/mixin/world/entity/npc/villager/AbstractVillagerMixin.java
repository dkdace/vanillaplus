package com.dace.vanillaplus.mixin.world.entity.npc.villager;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin<T extends AbstractVillager, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @Shadow
    protected abstract void addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> resourceKey);

    @Shadow
    public abstract MerchantOffers getOffers();

    @Shadow
    public abstract boolean isTrading();
}
