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
public record PotionConfig(@NonNull Optional<Integer> color, boolean isGlistering, @NonNull Optional<List<MobEffectInstance>> effects) {
    /** 기본값 */
    public static final PotionConfig DEFAULT = new PotionConfig(Optional.empty(), false, Optional.empty());
    /** JSON 코덱 */
    public static final Codec<PotionConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter(PotionConfig::color),
                    Codec.BOOL.optionalFieldOf("is_glistering", DEFAULT.isGlistering).forGetter(PotionConfig::isGlistering),
                    MobEffectInstance.CODEC.listOf().optionalFieldOf("effects").forGetter(PotionConfig::effects))
            .apply(instance, PotionConfig::new));
}
