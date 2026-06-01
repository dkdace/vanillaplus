package com.dace.vanillaplus.world.entity.decoration;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;

/**
 * {@link ArmorStand}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param enableQuickSwap   빠른 교체 활성화 여부
 * @param hasToggleableArms 팔 활성화 및 비활성화 가능 여부
 */
public record ArmorStandConfig(boolean enableQuickSwap, boolean hasToggleableArms) {
    /** 기본값 */
    private static final ArmorStandConfig DEFAULT = new ArmorStandConfig(false, false);
    /** JSON 코덱 */
    public static final Codec<ArmorStandConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("enable_quick_swap", DEFAULT.enableQuickSwap).forGetter(ArmorStandConfig::enableQuickSwap),
                    Codec.BOOL.optionalFieldOf("has_toggleable_arms", DEFAULT.hasToggleableArms).forGetter(ArmorStandConfig::hasToggleableArms))
            .apply(instance, ArmorStandConfig::new));

    /**
     * @return {@link ArmorStandConfig}
     */
    @NonNull
    public static ArmorStandConfig get() {
        return VPEntityType.cast(EntityType.ARMOR_STAND).getConfigComponents().getOrDefault(EntityConfigComponentTypes.ARMOR_STAND, DEFAULT);
    }
}
