package com.dace.vanillaplus.world.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/**
 * 숫자 값 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
public final class NumberModifier<T extends Number> extends ConditionalModifier<T> {
    /** 값 */
    @NonNull
    private final T value;

    private NumberModifier(@NonNull Optional<LootItemCondition> condition, @NonNull T value) {
        super(condition);
        this.value = value;
    }

    /**
     * JSON 코덱을 생성하여 반환한다.
     *
     * @param <T>         숫자 타입
     * @param numberCodec 사용할 숫자 형식 코덱
     * @return JSON 코덱
     */
    @NonNull
    public static <T extends Number> Codec<NumberModifier<T>> createCodec(@NonNull Codec<T> numberCodec) {
        return RecordCodecBuilder.create(instance -> ConditionalModifier.createCodec(instance)
                .and(numberCodec.fieldOf("value").forGetter(numberComponent -> numberComponent.value))
                .apply(instance, NumberModifier::new));
    }

    @Override
    @NonNull
    protected T run(@NonNull T value, @NonNull LootContext lootContext) {
        return this.value;
    }
}
