package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends MobMixin {
    @Shadow
    public abstract MerchantOffers getOffers();

    @Overwrite
    protected void addOffersFromItemListings(MerchantOffers offers, VillagerTrades.ItemListing[] itemListings, int count) {
        for (int i = 0; i < Math.min(itemListings.length, count); i++) {
            MerchantOffer offer = itemListings[i].getOffer((AbstractVillager) (Object) this, random);
            if (offer != null)
                offers.add(offer);
        }
    }
}
