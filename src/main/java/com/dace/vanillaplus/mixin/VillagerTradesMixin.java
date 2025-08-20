package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.UnmodifiableView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(VillagerTrades.class)
public final class VillagerTradesMixin {
    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 0))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyFarmerTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.FARMER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 1))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyFishermanTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.FISHERMAN);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 2))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyShepherdTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.SHEPHERD);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 3))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyFletcherTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.FLETCHER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 4))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyLibrarianTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.LIBRARIAN);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 5))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyCartographerTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.CARTOGRAPHER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 6))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyClericTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.CLERIC);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 7))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyArmorerTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.ARMORER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 8))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyWeaponsmithTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.WEAPONSMITH);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 9))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyToolsmithTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.TOOLSMITH);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 10))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyButcherTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.BUTCHER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 11))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyLeatherworkerTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.LEATHERWORKER);
    }

    @ModifyArg(method = "lambda$static$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerTrades;toIntMap(Lcom/google/common/collect/ImmutableMap;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
            ordinal = 12))
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> modifyMasonTrades(ImmutableMap<Integer, VillagerTrades.ItemListing[]> itemListingsMap) {
        return vp$getItemListingsMap(Rebalance.VillagerTrade.MASON);
    }

    @Unique
    @NonNull
    @UnmodifiableView
    private static ImmutableMap<Integer, VillagerTrades.ItemListing[]> vp$getItemListingsMap(@NonNull List<Rebalance.VillagerTrade.OfferInfo> offerLists) {
        ImmutableMap.Builder<Integer, VillagerTrades.ItemListing[]> builder = ImmutableMap.builder();
        for (int i = 0; i < 5; i++)
            builder.put(i + 1, offerLists.get(i).toArray());

        return builder.build();
    }
}
