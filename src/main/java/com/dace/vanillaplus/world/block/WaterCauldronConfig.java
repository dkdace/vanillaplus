package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Blocks;

/**
 * {@link Blocks#WATER_CAULDRON}의 블록 설정 데이터 요소 클래스.
 *
 * @param maxPotionEffects    담을 수 있는 물약의 최대 상태 효과 종류 수
 * @param maxTippedArrowCount 제작 가능한 물약이 묻은 화살의 최대 개수
 */
public record WaterCauldronConfig(int maxPotionEffects, int maxTippedArrowCount) {
    /** 기본값 */
    private static final WaterCauldronConfig DEFAULT = new WaterCauldronConfig(0, 0);
    /** JSON 코덱 */
    public static final Codec<WaterCauldronConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_potion_effects", DEFAULT.maxPotionEffects)
                            .forGetter(WaterCauldronConfig::maxPotionEffects),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_tipped_arrow_count", DEFAULT.maxTippedArrowCount)
                            .forGetter(WaterCauldronConfig::maxTippedArrowCount))
            .apply(instance, WaterCauldronConfig::new));

    /**
     * @return {@link WaterCauldronConfig}
     */
    @NonNull
    public static WaterCauldronConfig get() {
        return VPBlock.cast(Blocks.WATER_CAULDRON).getConfigComponents().getOrDefault(BlockConfigComponentTypes.WATER_CAULDRON, DEFAULT);
    }
}
