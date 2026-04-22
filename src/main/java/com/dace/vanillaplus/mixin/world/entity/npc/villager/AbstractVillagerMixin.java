package com.dace.vanillaplus.mixin.world.entity.npc.villager;

import com.dace.vanillaplus.extension.world.item.trading.VPTradeSet;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.world.TradeSetOffer;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin<T extends AbstractVillager, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @Shadow
    protected abstract void addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> resourceKey);

    @Shadow
    public abstract MerchantOffers getOffers();

    @Shadow
    public abstract boolean isTrading();

    @WrapWithCondition(method = "addOffersFromTradeSet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListings(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V"))
    private boolean redirectAddOffer(LootContext lootContext, MerchantOffers merchantOffers, HolderSet<VillagerTrade> potentialOffers,
                                     int numberOfOffers, @Local TradeSet tradeSet) {
        List<TradeSetOffer> tradeSetOffers = ((VPTradeSet) tradeSet).getTradeSetOffers();
        if (tradeSetOffers == null)
            return true;

        int offersFound = 0;
        while (offersFound < numberOfOffers) {
            TradeSetOffer tradeSetOffer = tradeSetOffers.get(lootContext.getRandom().nextInt(tradeSetOffers.size()));
            MerchantOffer merchantOffer = tradeSetOffer.getRandomMerchantOffer(lootContext);

            if (merchantOffer != null) {
                merchantOffers.add(merchantOffer);
                offersFound++;
            }
        }

        return false;
    }

    @WrapWithCondition(method = "addOffersFromTradeSet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListingsWithoutDuplicates(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V"))
    private boolean redirectAddOfferWithoutDuplicates(LootContext lootContext, MerchantOffers merchantOffers, HolderSet<VillagerTrade> potentialOffers,
                                                      int numberOfOffers, @Local TradeSet tradeSet) {
        List<TradeSetOffer> tradeSetOffers = ((VPTradeSet) tradeSet).getTradeSetOffers();
        if (tradeSetOffers == null)
            return true;

        int offersFound = 0;
        List<TradeSetOffer> list = new ArrayList<>(tradeSetOffers);

        while (offersFound < numberOfOffers && !list.isEmpty()) {
            TradeSetOffer tradeSetOffer = list.remove(lootContext.getRandom().nextInt(list.size()));
            MerchantOffer merchantOffer = tradeSetOffer.getRandomMerchantOffer(lootContext);

            if (merchantOffer != null) {
                merchantOffers.add(merchantOffer);
                offersFound++;
            }
        }

        return false;
    }
}
