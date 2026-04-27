package com.dace.vanillaplus.mixin.world.item.trading;

import com.dace.vanillaplus.extension.world.item.trading.VPTradeSet;
import com.dace.vanillaplus.world.TradeSetOffer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

@Mixin(TradeSet.class)
public abstract class TradeSetMixin implements VPTradeSet {
    @Shadow
    public static final Codec<TradeSet> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.either(TradeSetOffer.CODEC.listOf(), RegistryCodecs.homogeneousList(Registries.VILLAGER_TRADE)).fieldOf("trades")
                            .forGetter(tradeSet -> {
                                TradeSetMixin tradeSetMixin = (TradeSetMixin) (Object) tradeSet;

                                return tradeSetMixin.tradeSetOffers != null
                                        ? Either.left(tradeSetMixin.tradeSetOffers)
                                        : Either.right(tradeSetMixin.trades);
                            }),
                    NumberProviders.CODEC.fieldOf("amount").forGetter(tradeSet -> ((TradeSetMixin) (Object) tradeSet).amount),
                    Codec.BOOL.optionalFieldOf("allow_duplicates", false).forGetter(TradeSet::allowDuplicates),
                    Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(TradeSet::randomSequence))
            .apply(instance, TradeSetMixin::create));

    @Shadow
    @Final
    private HolderSet<VillagerTrade> trades;
    @Shadow
    @Final
    private NumberProvider amount;
    @Unique
    @Nullable
    @Getter
    private List<TradeSetOffer> tradeSetOffers;

    @Unique
    @NonNull
    private static TradeSet create(Either<List<TradeSetOffer>, HolderSet<VillagerTrade>> either, NumberProvider amount, boolean allowDuplicates,
                                   Optional<Identifier> randomSequence) {
        TradeSet tradeSet = new TradeSet(either.right().orElse(null), amount, allowDuplicates, randomSequence);
        ((TradeSetMixin) (Object) tradeSet).tradeSetOffers = either.left().orElse(null);

        return tradeSet;
    }
}
