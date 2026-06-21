package com.dace.vanillaplus.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * 엔티티의 체력 비례 수치를 나타내는 클래스.
 *
 * @param <T> 숫자 타입
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class HealthBasedValue<T extends Number> {
    /** 최솟값 */
    private final T min;
    /** 최댓값 */
    private final T max;
    /** 난이도 기반 여부 */
    private final boolean isDifficultyBased;

    /**
     * JSON 코덱을 생성하여 반환한다.
     *
     * @param <T>         숫자 타입
     * @param numberCodec 사용할 숫자 형식 코덱
     * @return JSON 코덱
     */
    @NonNull
    public static <T extends Number> Codec<HealthBasedValue<T>> createCodec(@NonNull Codec<T> numberCodec) {
        return RecordCodecBuilder.create(instance -> instance
                .group(numberCodec.fieldOf("min").forGetter(healthBasedValue -> healthBasedValue.min),
                        numberCodec.fieldOf("max").forGetter(healthBasedValue -> healthBasedValue.max),
                        Codec.BOOL.optionalFieldOf("is_difficulty_based", true)
                                .forGetter(healthBasedInt -> healthBasedInt.isDifficultyBased))
                .apply(instance, HealthBasedValue::new));
    }

    /**
     * 체력 비례 값을 반환한다.
     *
     * @param entity 대상 엔티티
     * @return {@link HealthBasedValue#min} + ({@link LivingEntity#getHealth()} / {@link LivingEntity#getMaxHealth()}) ×
     * ({@link HealthBasedValue#max} - {@link HealthBasedValue#min})
     */
    public float get(@NonNull LivingEntity entity) {
        float value = 0;
        if (isDifficultyBased)
            value = switch (entity.level().getDifficulty()) {
                case NORMAL -> 0.5F;
                case HARD -> 0;
                default -> 1;
            };

        return Mth.clampedLerp(Math.max(value, entity.getHealth() / entity.getMaxHealth()), min.floatValue(), max.floatValue());
    }
}
