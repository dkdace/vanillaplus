package com.dace.vanillaplus.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;

/**
 * 노획물 테이블의 보상을 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class LootTableReward {
    /** JSON 코덱 */
    public static final Codec<LootTableReward> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(IntProviders.NON_NEGATIVE_CODEC.optionalFieldOf("experience", UniformInt.of(20, 30))
                    .forGetter(LootTableReward::getXpRange))
            .apply(instance, LootTableReward::new));
    /** 경험치 획득량 범위 */
    @NonNull
    private final IntProvider xpRange;
}
