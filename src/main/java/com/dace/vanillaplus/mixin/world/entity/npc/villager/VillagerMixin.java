package com.dace.vanillaplus.mixin.world.entity.npc.villager;

import com.dace.vanillaplus.data.Trade;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.apache.commons.lang3.mutable.MutableFloat;
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
    @Unique
    private static final String COMPONENT_CLOSED = "merchant.closed";
    @Unique
    private static final String COMPONENT_OUT_OF_STOCK = "merchant.out_of_stock";

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void resendOffersToTradingPlayer();

    @Shadow
    protected abstract void stopTrading();

    @Unique
    private boolean isTradingClosed() {
        return !getOffers().isEmpty() && brain.getActiveNonCoreActivity().orElse(null) == Activity.REST;
    }

    @Unique
    private boolean isTradingOutOfStock() {
        return !getOffers().isEmpty() && getOffers().stream().allMatch(MerchantOffer::isOutOfStock);
    }

    @Unique
    private void addOffers(@NonNull ServerLevel serverLevel, int level) {
        getVillagerData().profession().unwrapKey()
                .flatMap(Trade.getDataManager()::get)
                .ifPresent(trade -> {
                    VillagerTrades.ItemListing[] itemListings = trade.getOfferList(level).toItemListings();
                    MerchantOffers offers = getOffers();

                    for (VillagerTrades.ItemListing itemListing : itemListings) {
                        MerchantOffer offer = itemListing.getOffer(serverLevel, getThis(), random);
                        if (offer != null)
                            offers.add(offer);
                    }
                });
    }

    @Overwrite
    protected void updateTrades(ServerLevel serverLevel) {
        addOffers(serverLevel, getVillagerData().level());
    }

    @Inject(method = "shouldRestock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;resetNumberOfRestocks()V",
            shift = At.Shift.AFTER))
    private void rerollOffers(ServerLevel serverLevel, CallbackInfoReturnable<Boolean> cir) {
        getOffers().clear();

        VillagerData villagerData = getVillagerData();
        for (int i = 1; i <= villagerData.level(); i++)
            addOffers(serverLevel, i);

        resendOffersToTradingPlayer();
    }

    @Definition(id = "random", field = "Lnet/minecraft/world/entity/npc/villager/Villager;random:Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("3 + this.random.nextInt(4)")
    @ModifyExpressionValue(method = "rewardTradeXp", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int modifyRewardBaseXP(int xp, @Local(argsOnly = true) MerchantOffer merchantOffer) {
        ItemCost itemCost = merchantOffer.getItemCostA();

        if (itemCost.itemStack().is(Items.EMERALD)) {
            int count = itemCost.count();
            return 3 * count + random.nextInt(4 * count);
        }

        return xp;
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;isEmpty()Z"))
    private boolean preventTradingIfClosed(boolean flag) {
        return flag || isTradingClosed() || isTradingOutOfStock();
    }

    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 2))
    private void sendClosedMessage(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (isTradingClosed())
            player.displayClientMessage(Component.translatable(COMPONENT_CLOSED), true);
        else if (isTradingOutOfStock())
            player.displayClientMessage(Component.translatable(COMPONENT_OUT_OF_STOCK), true);
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void closeTrading(ServerLevel serverLevel, CallbackInfo ci) {
        if (!isTradingClosed())
            return;

        getOffers().forEach(MerchantOffer::setToOutOfStock);
        if (isTrading())
            stopTrading();
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    private void applyTradingCostMultiplier(Player player, CallbackInfo ci) {
        MutableFloat value = new MutableFloat(0);

        EnchantmentHelper.runIterationOnEquipment(player, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyTradingCostMultiplier((ServerLevel) player.level(), level,
                        enchantedItemInUse.itemStack(), player, value));

        for (MerchantOffer merchantOffer : getOffers())
            merchantOffer.addToSpecialPriceDiff((int) (merchantOffer.getBaseCostA().getCount() * value.floatValue()));
    }
}
