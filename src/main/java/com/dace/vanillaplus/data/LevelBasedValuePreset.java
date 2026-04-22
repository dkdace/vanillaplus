package com.dace.vanillaplus.data;

import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * 레벨 기반 값 프리셋을 관리하는 클래스.
 */
public final class LevelBasedValuePreset {
    /** JSON 코덱 */
    public static final Codec<LevelBasedValuePreset> DIRECT_CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.unboundedMap(Codec.STRING, DefinedValue.CODEC).fieldOf("values")
                            .forGetter(levelBasedValuePreset -> levelBasedValuePreset.definedValueMap))
                    .apply(instance, LevelBasedValuePreset::new)));

    /** 이름별 사전 정의된 값 목록 (이름 : 사전 정의된 값) */
    private final TreeMap<String, DefinedValue> definedValueMap;

    private LevelBasedValuePreset(@NonNull Map<String, DefinedValue> definedValueMap) {
        this.definedValueMap = new TreeMap<>(Comparator.comparing(k -> definedValueMap.get(k).descriptionIndex));
        this.definedValueMap.putAll(definedValueMap);
    }

    /**
     * 레벨 기반 값에 대한 아이템 툴팁을 추가한다.
     *
     * @param componentConsumer    {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent 설명 텍스트 요소
     * @param level                마법 부여 레벨
     */
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent, int level) {
        if (descriptionComponent.getContents() instanceof TranslatableContents translatableContents)
            definedValueMap.values().forEach(definedValue -> {
                String key = translatableContents.getKey() + ".description." + definedValue.descriptionIndex;
                float value = definedValue.levelBasedValue.calculate(level);
                String argument = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value * definedValue.descriptionValueMultiplier);

                MutableComponent component = Component.translatable(key, argument);

                componentConsumer.accept(CommonComponents.space().append(component)
                        .withStyle(definedValue.sentiment.attributeSentiment.getStyle(value > 0)));
            });
    }

    /**
     * 지정한 이름에 해당하는 레벨 기반 값을 계산하여 반환한다.
     *
     * @param name  이름
     * @param level 레벨
     * @return 계산된 값
     * @throws NullPointerException 해당하는 레벨 기반 값이 존재하지 않으면 발생
     */
    public float calculate(@NonNull String name, int level) {
        return Objects.requireNonNull(definedValueMap.get(name)).levelBasedValue.calculate(level);
    }

    /**
     * 레벨 기반 값의 유형.
     */
    @AllArgsConstructor
    public enum Sentiment {
        /** 이로운 효과 */
        POSITIVE(Attribute.Sentiment.POSITIVE),
        /** 중립 효과 */
        NEUTRAL(Attribute.Sentiment.NEUTRAL),
        /** 해로운 효과 */
        NEGATIVE(Attribute.Sentiment.NEGATIVE);

        /** JSON 코덱 */
        private static final Codec<Sentiment> CODEC = CodecUtil.fromEnum(Sentiment.class);

        /** 색상 */
        private final Attribute.Sentiment attributeSentiment;
    }

    /**
     * 사전 정의된 값 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefinedValue {
        /** JSON 코덱 */
        private static final Codec<DefinedValue> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Sentiment.CODEC.optionalFieldOf("sentiment", Sentiment.POSITIVE)
                                .forGetter(definedValue -> definedValue.sentiment),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("description_index")
                                .forGetter(definedValue -> definedValue.descriptionIndex),
                        Codec.FLOAT.optionalFieldOf("description_value_multiplier", 1F)
                                .forGetter(definedValue -> definedValue.descriptionValueMultiplier),
                        LevelBasedValue.CODEC.fieldOf("value").forGetter(definedValue -> definedValue.levelBasedValue))
                .apply(instance, DefinedValue::new));

        /** 효과 유형 */
        private final Sentiment sentiment;
        /** 설명 포맷 인덱스 */
        private final int descriptionIndex;
        /** 설명 표시 값에 적용되는 배수 */
        private final float descriptionValueMultiplier;
        /** 레벨 기반 값 */
        @NonNull
        private final LevelBasedValue levelBasedValue;
    }
}
