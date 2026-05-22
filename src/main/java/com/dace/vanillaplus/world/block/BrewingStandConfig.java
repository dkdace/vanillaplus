package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;

/**
 * {@link BrewingStandBlock}의 블록 설정 데이터 요소 클래스.
 *
 * @param useDataDrivenRecipe 데이터 팩 양조법 사용 여부
 */
public record BrewingStandConfig(boolean useDataDrivenRecipe) {
    /** 기본값 */
    public static final BrewingStandConfig DEFAULT = new BrewingStandConfig(false);
    /** JSON 코덱 */
    public static final Codec<BrewingStandConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("use_data_driven_recipe", DEFAULT.useDataDrivenRecipe)
                    .forGetter(BrewingStandConfig::useDataDrivenRecipe))
            .apply(instance, BrewingStandConfig::new));

    @NonNull
    public static BrewingStandConfig get() {
        return VPBlock.cast(Blocks.BREWING_STAND).getConfigComponents().get(BlockConfigComponentTypes.BREWING_STAND);
    }
}
