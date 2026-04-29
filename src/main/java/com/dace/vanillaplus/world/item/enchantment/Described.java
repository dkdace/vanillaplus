package com.dace.vanillaplus.world.item.enchantment;

import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.function.Consumer;

/**
 * 아이템 툴팁에 설명을 표시하기 위한 레벨 기반 값 클래스.
 *
 * @param sentiment                  효과 유형
 * @param descriptionIndex           설명 포맷 인덱스
 * @param descriptionValueMultiplier 설명 표시 값에 적용되는 배수
 * @param levelBasedValue            레벨 기반 값
 */
public record Described(@NonNull Sentiment sentiment, int descriptionIndex, float descriptionValueMultiplier,
                        @NonNull LevelBasedValue levelBasedValue) implements LevelBasedValue, Comparable<Described> {
    /** JSON 코덱 */
    public static final MapCodec<Described> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Sentiment.CODEC.optionalFieldOf("sentiment", Sentiment.POSITIVE).forGetter(Described::sentiment),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("description_index").forGetter(Described::descriptionIndex),
                    Codec.FLOAT.optionalFieldOf("description_value_multiplier", 1F).forGetter(Described::descriptionValueMultiplier),
                    LevelBasedValue.CODEC.fieldOf("value").forGetter(Described::levelBasedValue))
            .apply(instance, Described::new));

    @Override
    public float calculate(int level) {
        return levelBasedValue.calculate(level);
    }

    /**
     * 레벨 기반 값에 대한 아이템 툴팁을 추가한다.
     *
     * @param componentConsumer    {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent 설명 텍스트 요소
     * @param level                레벨
     */
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent, int level) {
        if (!(descriptionComponent.getContents() instanceof TranslatableContents translatableContents))
            return;

        String key = translatableContents.getKey() + ".description." + descriptionIndex;
        float value = calculate(level);
        String argument = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value * descriptionValueMultiplier);

        MutableComponent component = Component.translatable(key, argument);
        componentConsumer.accept(CommonComponents.space().append(component).withStyle(sentiment.attributeSentiment.getStyle(value > 0)));
    }

    @Override
    @NonNull
    public MapCodec<Described> codec() {
        return TYPED_CODEC;
    }

    @Override
    public int compareTo(@NonNull Described o) {
        return Integer.compare(descriptionIndex, o.descriptionIndex);
    }

    /**
     * 효과의 유형.
     */
    @AllArgsConstructor
    private enum Sentiment {
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
}
