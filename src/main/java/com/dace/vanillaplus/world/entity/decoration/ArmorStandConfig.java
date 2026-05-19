package com.dace.vanillaplus.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.decoration.ArmorStand;

/**
 * {@link ArmorStand}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param enableQuickSwap   빠른 교체 활성화 여부
 * @param hasToggleableArms 팔 활성화 및 비활성화 가능 여부
 */
public record ArmorStandConfig(boolean enableQuickSwap, boolean hasToggleableArms) {
    /** 기본값 */
    public static final ArmorStandConfig DEFAULT = new ArmorStandConfig(false, false);
    /** JSON 코덱 */
    public static final Codec<ArmorStandConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("enable_quick_swap", DEFAULT.enableQuickSwap).forGetter(ArmorStandConfig::enableQuickSwap),
                    Codec.BOOL.optionalFieldOf("has_toggleable_arms", DEFAULT.hasToggleableArms).forGetter(ArmorStandConfig::hasToggleableArms))
            .apply(instance, ArmorStandConfig::new));
}
