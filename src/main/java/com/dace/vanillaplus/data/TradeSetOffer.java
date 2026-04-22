package com.dace.vanillaplus.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TradeSet}에서 사용하는 주민 거래 품목을 나타내는 클래스.
 */
public abstract class TradeSetOffer {
    /** JSON 코덱 */
    public static final Codec<TradeSetOffer> CODEC = Codec.lazyInitialized(() -> Codec.either(Direct.CODEC, Random.CODEC)
            .xmap(Either::unwrap, tradeSetOffer -> switch (tradeSetOffer) {
                case Direct direect -> Either.left(direect);
                case Random random -> Either.right(random);
                default -> null;
            }));

    @NonNull
    abstract HolderSet<VillagerTrade> getHolderSet(@NonNull RandomSource randomSource);

    /**
     * 전리품 컨텍스트의 조건을 만족하는 무작위 거래 품목을 반환한다.
     *
     * @param lootContext 전리품 컨텍스트
     * @return 무작위 거래 품목. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public final MerchantOffer getRandomMerchantOffer(@NonNull LootContext lootContext) {
        RandomSource randomSource = lootContext.getRandom();
        List<Holder<VillagerTrade>> villagerTradeHolders = getHolderSet(randomSource).stream().collect(Collectors.toList());

        while (!villagerTradeHolders.isEmpty()) {
            Holder<VillagerTrade> villagerTrade = villagerTradeHolders.remove(randomSource.nextInt(villagerTradeHolders.size()));
            MerchantOffer merchantOffer = villagerTrade.value().getOffer(lootContext);

            if (merchantOffer != null)
                return merchantOffer;
        }

        return null;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Direct extends TradeSetOffer {
        private static final Codec<Direct> CODEC = RegistryCodecs.homogeneousList(Registries.VILLAGER_TRADE)
                .xmap(Direct::new, tradeSetOffer -> tradeSetOffer.holderSet);
        private final HolderSet<VillagerTrade> holderSet;

        @Override
        @NonNull
        HolderSet<VillagerTrade> getHolderSet(@NonNull RandomSource randomSource) {
            return holderSet;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Random extends TradeSetOffer {
        private static final Codec<Random> CODEC = TradeSetOffer.CODEC.listOf().xmap(Random::new, random -> random.tradeSetOffers);
        private final List<TradeSetOffer> tradeSetOffers;

        @Override
        @NonNull
        HolderSet<VillagerTrade> getHolderSet(@NonNull RandomSource randomSource) {
            return tradeSetOffers.get(randomSource.nextInt(tradeSetOffers.size())).getHolderSet(randomSource);
        }
    }
}
