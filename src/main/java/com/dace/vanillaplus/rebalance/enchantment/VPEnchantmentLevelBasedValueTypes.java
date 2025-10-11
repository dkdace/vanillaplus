package com.dace.vanillaplus.rebalance.enchantment;

import com.dace.vanillaplus.VPRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

/**
 * 모드에서 사용하는 마법 부여의 레벨 기반 값 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPEnchantmentLevelBasedValueTypes {
    static {
        VPRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.register("preset", () -> Preset.TYPED_CODEC);
    }

    private record Preset(@NonNull Holder<EnchantmentValuePreset> enchantmentValuePresetHolder, @NonNull String name) implements LevelBasedValue {
        public static final MapCodec<Preset> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EnchantmentValuePreset.CODEC.fieldOf("value").forGetter(Preset::enchantmentValuePresetHolder),
                        Codec.STRING.fieldOf("name").forGetter(Preset::name))
                .apply(instance, Preset::new));

        @Override
        public float calculate(int level) {
            return enchantmentValuePresetHolder.value().getValue(name).getLevelBasedValue().calculate(level);
        }

        @Override
        @NonNull
        public MapCodec<Preset> codec() {
            return TYPED_CODEC;
        }
    }
}
