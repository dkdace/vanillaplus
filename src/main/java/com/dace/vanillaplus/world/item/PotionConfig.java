package com.dace.vanillaplus.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.Optional;

/**
 * 물약의 효과를 수정하는 물약 설정 클래스.
 *
 * @param color        물약 색상
 * @param isGlistering 반짝임 여부
 * @param effects      효과 목록
 */
public record PotionConfig(@NonNull Optional<Integer> color, boolean isGlistering, @NonNull List<MobEffectInstance> effects) {
    /** JSON 코덱 */
    public static final Codec<PotionConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter(PotionConfig::color),
                    Codec.BOOL.optionalFieldOf("is_glistering", false).forGetter(PotionConfig::isGlistering),
                    MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(PotionConfig::effects))
            .apply(instance, PotionConfig::new));
}
