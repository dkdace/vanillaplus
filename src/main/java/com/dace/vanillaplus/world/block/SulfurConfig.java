package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.level.block.Blocks;

/**
 * {@link SulfurBlock}의 블록 설정 데이터 요소 클래스.
 *
 * @param canBurn 연소 가능 여부
 */
public record SulfurConfig(boolean canBurn) {
    /** 기본값 */
    private static final SulfurConfig DEFAULT = new SulfurConfig(false);
    /** JSON 코덱 */
    public static final Codec<SulfurConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("can_burn", DEFAULT.canBurn).forGetter(SulfurConfig::canBurn))
            .apply(instance, SulfurConfig::new));

    /**
     * @return {@link SulfurConfig}
     */
    @NonNull
    public static SulfurConfig get() {
        return VPBlock.cast(Blocks.SULFUR).getConfigComponents().getOrDefault(BlockConfigComponentTypes.SULFUR, DEFAULT);
    }
}
