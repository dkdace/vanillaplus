package com.dace.vanillaplus.world.entity.npc.villager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.npc.villager.Villager;

/**
 * {@link Villager}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param closeTradingAtNight  밤이 되었을 때 거래 마감 여부
 * @param rerollOffersEveryday 매일 거래 품목 재설정 여부
 */
public record VillagerConfig(boolean closeTradingAtNight, boolean rerollOffersEveryday) {
    /** 기본값 */
    public static final VillagerConfig DEFAULT = new VillagerConfig(false, false);
    /** JSON 코덱 */
    public static final Codec<VillagerConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("close_trading_at_night", DEFAULT.closeTradingAtNight)
                            .forGetter(VillagerConfig::closeTradingAtNight),
                    Codec.BOOL.optionalFieldOf("reroll_offers_everyday", DEFAULT.rerollOffersEveryday)
                            .forGetter(VillagerConfig::rerollOffersEveryday))
            .apply(instance, VillagerConfig::new));
}
