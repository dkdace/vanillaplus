package com.dace.vanillaplus.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillagerMixin {
    @Shadow
    private long lastRestockGameTime;
    @Shadow
    private int numberOfRestocksToday;

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void updateDemand();

    @Shadow
    protected abstract void resendOffersToTradingPlayer();

    @Overwrite
    private boolean needsToRestock() {
        return true;
    }

    @Overwrite
    public void restock() {
        updateDemand();

        VillagerData villagerData = getVillagerData();
        ResourceKey<VillagerProfession> resourcekey = villagerData.profession().unwrapKey().orElse(null);
        if (resourcekey == null)
            return;

        MerchantOffers offers = getOffers();
        for (int i = 0; i < villagerData.level(); i++) {
            for (int j = 0; j < 2; j++) {
                int index = i * 2 + j;

                MerchantOffer offer = offers.get(index);
                if (offer.getResult().is(Items.EMERALD) || offer.getDemand() <= 0)
                    offers.set(index, VillagerTrades.TRADES.get(resourcekey).get(i + 1)[j].getOffer((Villager) (Object) this, random));
                else
                    offer.resetUses();
            }
        }

        resendOffersToTradingPlayer();
        lastRestockGameTime = level().getGameTime();
        numberOfRestocksToday++;
    }
}
