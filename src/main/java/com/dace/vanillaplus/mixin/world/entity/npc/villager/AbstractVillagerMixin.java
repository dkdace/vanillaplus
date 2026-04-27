package com.dace.vanillaplus.mixin.world.entity.npc.villager;

import com.dace.vanillaplus.extension.world.item.trading.VPTradeSet;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.world.TradeSetOffer;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.*;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin<T extends AbstractVillager, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @Unique
    private static final int TRADE_XP_BASE = 3;
    @Unique
    private static final int TRADE_XP_RANDOM = 4;

    @Shadow
    protected abstract void addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> resourceKey);

    @Shadow
    public abstract MerchantOffers getOffers();

    @Shadow
    public abstract boolean isTrading();

    @Unique
    protected int getTradePlayerXP(int xp, @NonNull MerchantOffer merchantOffer) {
        ItemCost itemCost = merchantOffer.getItemCostA();

        if (itemCost.itemStack().is(Items.EMERALD)) {
            int count = itemCost.count();
            return TRADE_XP_BASE * count + random.nextInt(TRADE_XP_RANDOM * count);
        }

        return xp;
    }

    @WrapWithCondition(method = "addOffersFromTradeSet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListings(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V"))
    private boolean redirectAddOffer(LootContext lootContext, MerchantOffers merchantOffers, HolderSet<VillagerTrade> potentialOffers,
                                     int numberOfOffers, @Local(name = "tradeSet") TradeSet tradeSet) {
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
                                                      int numberOfOffers, @Local(name = "tradeSet") TradeSet tradeSet) {
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
