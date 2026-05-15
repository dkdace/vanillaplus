package com.dace.vanillaplus.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

/**
 * 쇠뇌를 사용하는 몹의 데이터 요소 클래스.
 *
 * @param shootingPower  화살 발사 속력
 * @param shootingRange  공격 거리
 * @param backupDistance 후퇴 거리
 */
public record CrossbowMobConfig(@NonNull Optional<Float> shootingPower, @NonNull Optional<Integer> shootingRange,
                                @NonNull Optional<Integer> backupDistance) {
    /** 기본값 */
    public static final CrossbowMobConfig DEFAULT = new CrossbowMobConfig(Optional.empty(), Optional.empty(),
            Optional.empty());
    /** JSON 코덱 */
    public static final Codec<CrossbowMobConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power").forGetter(CrossbowMobConfig::shootingPower),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shooting_range").forGetter(CrossbowMobConfig::shootingRange),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("backup_distance").forGetter(CrossbowMobConfig::backupDistance))
            .apply(instance, CrossbowMobConfig::new));
}
