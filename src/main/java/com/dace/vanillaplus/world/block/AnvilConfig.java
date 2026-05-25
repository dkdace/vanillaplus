package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/**
 * {@link AnvilBlock}의 블록 설정 데이터 요소 클래스.
 *
 * @param increaseRepairCost 사용 시 작업 가격 증가 여부
 * @param maxCost            최대 작업 가격
 */
public record AnvilConfig(boolean increaseRepairCost, @NonNull Optional<Integer> maxCost) {
    /** 기본값 */
    private static final AnvilConfig DEFAULT = new AnvilConfig(true, Optional.empty());
    /** JSON 코덱 */
    public static final Codec<AnvilConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("increase_repair_cost", DEFAULT.increaseRepairCost).forGetter(AnvilConfig::increaseRepairCost),
                    ExtraCodecs.intRange(-1, Integer.MAX_VALUE).optionalFieldOf("max_cost").forGetter(AnvilConfig::maxCost))
            .apply(instance, AnvilConfig::new));

    /**
     * @return {@link AnvilConfig}
     */
    @NonNull
    public static AnvilConfig get() {
        return VPBlock.cast(Blocks.ANVIL).getConfigComponents().getOrDefault(BlockConfigComponentTypes.ANVIL, DEFAULT);
    }
}
