package com.dace.vanillaplus.world.entity.npc.villager;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;

/**
 * {@link Villager}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param closeTradingAtNight  밤이 되었을 때 거래 마감 여부
 * @param rerollOffersEveryday 매일 거래 품목 재설정 여부
 */
public record VillagerConfig(boolean closeTradingAtNight, boolean rerollOffersEveryday) {
    /** 기본값 */
    private static final VillagerConfig DEFAULT = new VillagerConfig(false, false);
    /** JSON 코덱 */
    public static final Codec<VillagerConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("close_trading_at_night", DEFAULT.closeTradingAtNight)
                            .forGetter(VillagerConfig::closeTradingAtNight),
                    Codec.BOOL.optionalFieldOf("reroll_offers_everyday", DEFAULT.rerollOffersEveryday)
                            .forGetter(VillagerConfig::rerollOffersEveryday))
            .apply(instance, VillagerConfig::new));

    /**
     * @return {@link VillagerConfig}
     */
    @NonNull
    public static VillagerConfig get() {
        return VPEntityType.cast(EntityType.VILLAGER).getConfigComponents().getOrDefault(EntityConfigComponentTypes.VILLAGER, DEFAULT);
    }
}
