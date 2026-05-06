package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/**
 * {@link BellBlock}의 블록 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BellBlockModifier extends BlockModifier {
    /** 기본값 */
    public static final BellBlockModifier DEFAULT = new BellBlockModifier(BlockModifier.DEFAULT.getComponents(), Optional.empty(),
            Optional.empty(), Optional.empty());
    /** JSON 코덱 */
    public static final MapCodec<BellBlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createBaseCodec(instance)
                    .and(instance.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("raider_detection_range")
                                    .forGetter(BellBlockModifier::getRaiderDetectionRange),
                            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("glow_range").forGetter(BellBlockModifier::getGlowRange),
                            CodecUtil.secondsToTicks(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("glow_duration_seconds")
                                    .forGetter(BellBlockModifier::getGlowDuration)))
                    .apply(instance, BellBlockModifier::new));

    /** 습격자 탐지 범위 */
    @NonNull
    private final Optional<Integer> raiderDetectionRange;
    /** 발광 효과 범위 */
    @NonNull
    private final Optional<Integer> glowRange;
    /** 발광 효과 지속시간 */
    @NonNull
    private final Optional<Integer> glowDuration;

    private BellBlockModifier(@NonNull VPDataComponentMap components, @NonNull Optional<Integer> raiderDetectionRange,
                              @NonNull Optional<Integer> glowRange, @NonNull Optional<Integer> glowDuration) {
        super(components);

        this.raiderDetectionRange = raiderDetectionRange;
        this.glowRange = glowRange;
        this.glowDuration = glowDuration;
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    public static BellBlockModifier get() {
        return VPModifiableData.getDataModifier(Blocks.BELL, DEFAULT);
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
