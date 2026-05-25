package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/**
 * {@link BellBlock}의 블록 설정 데이터 요소 클래스.
 *
 * @param raiderDetectionRange 습격자 탐지 범위
 * @param glowRange            발광 효과 범위
 * @param glowDuration         발광 효과 지속시간
 */
public record BellConfig(@NonNull Optional<Integer> raiderDetectionRange, @NonNull Optional<Integer> glowRange,
                         @NonNull Optional<Integer> glowDuration) {
    /** JSON 코덱 */
    public static final Codec<BellConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("raider_detection_range").forGetter(BellConfig::raiderDetectionRange),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("glow_range").forGetter(BellConfig::glowRange),
                    CodecUtil.secondsToTicks(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("glow_duration_seconds")
                            .forGetter(BellConfig::glowDuration))
            .apply(instance, BellConfig::new));
    /** 기본값 */
    private static final BellConfig DEFAULT = new BellConfig(Optional.empty(), Optional.empty(),
            Optional.empty());

    /**
     * @return {@link BellConfig}
     */
    @NonNull
    public static BellConfig get() {
        return VPBlock.cast(Blocks.BELL).getConfigComponents().getOrDefault(BlockConfigComponentTypes.BELL, DEFAULT);
    }
}
