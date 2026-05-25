package com.dace.vanillaplus.world.entity;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;

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
    /** JSON 코덱 */
    public static final Codec<CrossbowMobConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power").forGetter(CrossbowMobConfig::shootingPower),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shooting_range").forGetter(CrossbowMobConfig::shootingRange),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("backup_distance").forGetter(CrossbowMobConfig::backupDistance))
            .apply(instance, CrossbowMobConfig::new));
    /** 기본값 */
    private static final CrossbowMobConfig DEFAULT = new CrossbowMobConfig(Optional.empty(), Optional.empty(),
            Optional.empty());

    /**
     * @param entity 대상 엔티티
     * @return {@link CrossbowMobConfig}
     */
    @NonNull
    public static CrossbowMobConfig get(@NonNull Entity entity) {
        return VPEntity.cast(entity).getConfigComponents().getOrDefault(EntityConfigComponentTypes.CROSSBOW_MOB, DEFAULT);
    }
}
