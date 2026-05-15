package com.dace.vanillaplus.world.entity.config;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/**
 * 조건부 수정자 데이터 요소 클래스.
 *
 * @param <T> 데이터 타입
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConditionalModifier<T> {
    @NonNull
    Optional<LootItemCondition> condition;

    @NonNull
    protected static <T extends ConditionalModifier<U>, U> Products.P1<RecordCodecBuilder.Mu<T>, Optional<LootItemCondition>> createCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(LootItemCondition.DIRECT_CODEC.optionalFieldOf("predicate")
                .forGetter(conditionalComponent -> conditionalComponent.condition));
    }

    /**
     * 조건을 만족하면 초기 값에 수정자를 적용한 최종 값을 반환한다.
     *
     * <p>조건을 만족하지 않으면 초기 값을 그대로 반환한다.</p>
     *
     * @param value       초기 값
     * @param lootContext 전리품 컨텍스트
     * @return 최종 값
     */
    public final T apply(@NonNull T value, @NonNull LootContext lootContext) {
        return condition.map(lootItemCondition -> lootItemCondition.test(lootContext)).orElse(true)
                ? run(value, lootContext)
                : value;
    }

    /**
     * 지정한 값에 수정자를 적용한다.
     *
     * @param value       초기 값
     * @param lootContext 전리품 컨텍스트
     * @return 최종 값
     */
    @NonNull
    protected abstract T run(@NonNull T value, @NonNull LootContext lootContext);
}
