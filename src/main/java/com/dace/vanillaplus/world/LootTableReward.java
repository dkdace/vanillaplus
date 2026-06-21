package com.dace.vanillaplus.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

/**
 * 노획물 테이블의 보상을 관리하는 클래스.
 *
 * @param xpRange 경험치 획득량 범위
 */
public record LootTableReward(@NonNull IntProvider xpRange) {
    /** JSON 코덱 */
    public static final Codec<LootTableReward> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(IntProviders.NON_NEGATIVE_CODEC.optionalFieldOf("experience", ConstantInt.of(0)).forGetter(LootTableReward::xpRange))
            .apply(instance, LootTableReward::new));
}
