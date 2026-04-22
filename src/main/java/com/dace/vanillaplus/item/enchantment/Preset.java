package com.dace.vanillaplus.item.enchantment;

import com.dace.vanillaplus.DataPackRegistries;
import com.dace.vanillaplus.data.LevelBasedValuePreset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.enchantment.LevelBasedValue;

/**
 * 레벨 기반 값 프리셋 ({@link LevelBasedValuePreset})을 참조하는 레벨 기반 값 클래스.
 *
 * @param levelBasedValuePresetHolder 레벨 기반 값 프리셋 홀더 인스턴스.
 * @param name                        {@link LevelBasedValuePreset#calculate(String, int)}에 사용할 이름
 */
public record Preset(@NonNull Holder<LevelBasedValuePreset> levelBasedValuePresetHolder, @NonNull String name) implements LevelBasedValue {
    /** JSON 코덱 */
    public static final MapCodec<Preset> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(RegistryFixedCodec.create(DataPackRegistries.LEVEL_BASED_VALUE_PRESET).fieldOf("value")
                            .forGetter(Preset::levelBasedValuePresetHolder),
                    Codec.STRING.fieldOf("name").forGetter(Preset::name))
            .apply(instance, Preset::new));

    @Override
    public float calculate(int level) {
        return levelBasedValuePresetHolder.value().calculate(name, level);
    }

    @Override
    @NonNull
    public MapCodec<Preset> codec() {
        return TYPED_CODEC;
    }
}
