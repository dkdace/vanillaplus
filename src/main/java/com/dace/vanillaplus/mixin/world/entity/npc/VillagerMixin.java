package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.data.Trade;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillagerMixin<Villager, EntityModifier.LivingEntityModifier> {
    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void resendOffersToTradingPlayer();

    @Shadow
    protected abstract void stopTrading();

    @Unique
    private boolean isStoreClosed() {
        return !getOffers().isEmpty() && brain.getActiveNonCoreActivity().orElse(null) == Activity.REST;
    }

    @Unique
    private boolean isStoreOutOfStock() {
        return !getOffers().isEmpty() && getOffers().stream().allMatch(MerchantOffer::isOutOfStock);
    }

    @Unique
    private void addOffers(int level) {
        Trade trade = getVillagerData().profession().unwrapKey().map(Trade::fromVillagerProfession).orElse(null);
        if (trade == null)
            return;

        VillagerTrades.ItemListing[] itemListings = trade.getOfferList(level).toItemListings();
        MerchantOffers offers = getOffers();

        for (VillagerTrades.ItemListing itemListing : itemListings) {
            MerchantOffer offer = itemListing.getOffer(getThis(), random);
            if (offer != null)
                offers.add(offer);
        }
    }

    @Overwrite
    protected void updateTrades() {
        addOffers(getVillagerData().level());
    }

    @Inject(method = "resetNumberOfRestocks", at = @At("TAIL"))
    private void rerollOffers(CallbackInfo ci) {
        getOffers().clear();

        VillagerData villagerData = getVillagerData();
        for (int i = 1; i <= villagerData.level(); i++)
            addOffers(i);

        resendOffersToTradingPlayer();
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;isEmpty()Z"))
    private boolean preventTradingIfClosed(boolean flag) {
        return flag || isStoreClosed() || isStoreOutOfStock();
    }

    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 2))
    private void sendClosedMessage(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (isStoreClosed())
            player.displayClientMessage(Component.translatable("merchant.closed"), true);
        else if (isStoreOutOfStock())
            player.displayClientMessage(Component.translatable("merchant.out_of_stock"), true);
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void closeStore(ServerLevel serverLevel, CallbackInfo ci) {
        if (!isStoreClosed())
            return;

        getOffers().forEach(MerchantOffer::setToOutOfStock);
        if (isTrading())
            stopTrading();
    }
}
