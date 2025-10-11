package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * 마법 부여의 수치 프리셋을 관리하는 클래스.
 */
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EnchantmentValuePreset {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<EnchantmentValuePreset>> CODEC = VPRegistries.ENCHANTMENT_VALUE_PRESET.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<EnchantmentValuePreset> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(Codec.STRING, DefinedValue.CODEC).fieldOf("values")
                    .forGetter(enchantmentValuePreset -> enchantmentValuePreset.definedValueMap))
            .apply(instance, EnchantmentValuePreset::new));

    /** 이름별 사전 정의된 값 목록 (이름 : 사전 정의된 값) */
    private final TreeMap<String, DefinedValue> definedValueMap;

    private EnchantmentValuePreset(@NonNull Map<String, DefinedValue> definedValueMap) {
        this.definedValueMap = new TreeMap<>(Comparator.comparing(k -> definedValueMap.get(k).getDescriptionIndex()));
        this.definedValueMap.putAll(definedValueMap);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistries.ENCHANTMENT_VALUE_PRESET.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }

    /**
     * @return 사전 정의된 값 목록
     */
    @NonNull
    @UnmodifiableView
    public Collection<DefinedValue> getValues() {
        return Collections.unmodifiableCollection(definedValueMap.values());
    }

    /**
     * 지정한 이름에 해당하는 사전 정의된 값을 반환한다.
     *
     * @param name 이름
     * @return 사전 정의된 값
     * @throws NullPointerException 해당하는 사전 정의된 값이 존재하지 않으면 발생
     */
    @NonNull
    public DefinedValue getValue(@NonNull String name) {
        return Objects.requireNonNull(definedValueMap.get(name));
    }

    /**
     * 사전 정의된 값 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class DefinedValue {
        /** JSON 코덱 */
        private static final Codec<DefinedValue> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("description_index").forGetter(DefinedValue::getDescriptionIndex),
                        Codec.FLOAT.optionalFieldOf("description_value_multiplier", 1F)
                                .forGetter(DefinedValue::getDescriptionValueMultiplier),
                        LevelBasedValue.CODEC.fieldOf("value").forGetter(DefinedValue::getLevelBasedValue))
                .apply(instance, DefinedValue::new));

        /** 설명 포맷 인덱스 */
        private final int descriptionIndex;
        /** 설명 표시 값에 적용되는 배수 */
        private final float descriptionValueMultiplier;
        /** 레벨 기반 값 */
        @NonNull
        private final LevelBasedValue levelBasedValue;
    }
}
