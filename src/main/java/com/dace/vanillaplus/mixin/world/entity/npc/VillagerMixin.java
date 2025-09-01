package com.dace.vanillaplus.mixin.world.entity.npc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillagerMixin {
    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void resendOffersToTradingPlayer();

    @Shadow
    protected abstract void stopTrading();

    @Unique
    private boolean isMerchantClosed() {
        return !getOffers().isEmpty() && brain.getActiveNonCoreActivity().orElse(null) == Activity.REST;
    }

    @Unique
    private boolean isMerchantOutOfStock() {
        return !getOffers().isEmpty() && getOffers().stream().allMatch(MerchantOffer::isOutOfStock);
    }

    @Inject(method = "resetNumberOfRestocks", at = @At("TAIL"))
    private void rerollOffers(CallbackInfo ci) {
        VillagerData villagerData = getVillagerData();
        ResourceKey<VillagerProfession> resourcekey = villagerData.profession().unwrapKey().orElse(null);
        if (resourcekey == null)
            return;

        MerchantOffers offers = getOffers();

        for (int i = 0; i < villagerData.level(); i++)
            for (int j = 0; j < 2; j++)
                offers.set(i * 2 + j, VillagerTrades.TRADES.get(resourcekey).get(i + 1)[j].getOffer((Villager) (Object) this, random));

        resendOffersToTradingPlayer();
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;isEmpty()Z"))
    private boolean preventTradingIfClosed(boolean flag) {
        return flag || isMerchantClosed() || isMerchantOutOfStock();
    }

    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 2))
    private void sendClosedMessage(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (isMerchantClosed())
            player.displayClientMessage(Component.translatable("merchant.closed"), true);
        else if (isMerchantOutOfStock())
            player.displayClientMessage(Component.translatable("merchant.out_of_stock"), true);
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void closeMerchant(ServerLevel serverLevel, CallbackInfo ci) {
        if (!isMerchantClosed())
            return;

        getOffers().forEach(MerchantOffer::setToOutOfStock);
        if (isTrading())
            stopTrading();
    }
}
