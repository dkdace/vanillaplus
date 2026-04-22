package com.dace.vanillaplus.item.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;

/**
 * 두 레벨 기반 값을 곱하는 레벨 기반 값 클래스.
 *
 * @param first  첫번째 레벨 기반 값
 * @param second 두번째 레벨 기반 값
 */
public record Multiply(@NonNull LevelBasedValue first, @NonNull LevelBasedValue second) implements LevelBasedValue {
    /** JSON 코덱 */
    public static final MapCodec<Multiply> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("first").forGetter(Multiply::first),
                    LevelBasedValue.CODEC.fieldOf("second").forGetter(Multiply::second))
            .apply(instance, Multiply::new));

    @Override
    public float calculate(int level) {
        return first.calculate(level) * second.calculate(level);
    }

    @Override
    @NonNull
    public MapCodec<Multiply> codec() {
        return TYPED_CODEC;
    }
}
