package com.dace.vanillaplus.world.entity.config;

import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 조건부 수정자 목록을 관리하는 클래스.
 *
 * @param <T> {@link ConditionalModifier}를 상속받는 타입
 * @param <U> 수정자 데이터 타입
 * @see ConditionalModifier
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class ConditionalModifierList<T extends ConditionalModifier<U>, U> {
    /** 기본값 */
    private static final ConditionalModifierList<?, ?> EMPTY = new ConditionalModifierList<>(Collections.emptyList());
    /** 수정자 목록 */
    @NonNull
    private final List<T> modifiers;

    /**
     * @param <T> {@link ConditionalModifier}를 상속받는 타입
     * @param <U> 수정자 데이터 타입
     * @return 기본값
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends ConditionalModifier<U>, U> ConditionalModifierList<T, U> empty() {
        return (ConditionalModifierList<T, U>) EMPTY;
    }

    /**
     * JSON 코덱을 생성하여 반환한다.
     *
     * @param codec 수정자 코덱
     * @param <T>   {@link ConditionalModifier}를 상속받는 타입
     * @param <U>   수정자 데이터 타입
     * @return JSON 코덱
     */
    @NonNull
    public static <T extends ConditionalModifier<U>, U> Codec<ConditionalModifierList<T, U>> createCodec(@NonNull Codec<T> codec) {
        return codec.listOf().xmap(ConditionalModifierList::new, conditionalModifierList -> conditionalModifierList.modifiers);
    }

    /**
     * 장비 전리품 컨텍스트를 생성한다.
     *
     * @param serverLevel 월드
     * @param entity      엔티티
     * @return 전리품 컨텍스트
     */
    @NonNull
    private static LootContext createEquipmentLootContext(@NonNull ServerLevel serverLevel, @NonNull Entity entity) {
        return new LootContext.Builder(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .create(LootContextParamSets.EQUIPMENT))
                .create(Optional.empty());
    }

    /**
     * 아무 수정자도 없는 비어있는 목록인지 확인한다.
     *
     * @return 비어있으면 {@code true} 반환
     */
    public boolean isEmpty() {
        return modifiers.isEmpty();
    }

    /**
     * 모든 수정자를 누적 연산하여 얻은 최종 값을 반환한다.
     *
     * @param entity 조건 검사에 사용할 엔티티
     * @param value  초기 값
     * @return 최종 값
     * @see ConditionalModifier#apply(Object, LootContext)
     */
    @NonNull
    public U apply(@NonNull Entity entity, @NonNull U value) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            LootContext lootContext = createEquipmentLootContext(serverLevel, entity);

            for (T conditionalComponent : modifiers)
                value = conditionalComponent.apply(value, lootContext);
        }

        return value;
    }
}
