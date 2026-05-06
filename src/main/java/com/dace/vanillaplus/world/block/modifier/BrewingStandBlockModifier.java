package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;

/**
 * {@link BrewingStandBlock}의 블록 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BrewingStandBlockModifier extends BlockModifier {
    /** 기본값 */
    public static final BrewingStandBlockModifier DEFAULT = new BrewingStandBlockModifier(BlockModifier.DEFAULT.getComponents(),
            false);
    /** JSON 코덱 */
    public static final MapCodec<BrewingStandBlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createBaseCodec(instance)
                    .and(Codec.BOOL.optionalFieldOf("use_data_driven_recipe", DEFAULT.useDataDrivenRecipe)
                            .forGetter(BrewingStandBlockModifier::isUseDataDrivenRecipe))
                    .apply(instance, BrewingStandBlockModifier::new));

    /** 데이터 팩 양조법 사용 여부 */
    private final boolean useDataDrivenRecipe;

    private BrewingStandBlockModifier(@NonNull VPDataComponentMap components, boolean useDataDrivenRecipe) {
        super(components);
        this.useDataDrivenRecipe = useDataDrivenRecipe;
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    public static BrewingStandBlockModifier get() {
        return VPModifiableData.getDataModifier(Blocks.BREWING_STAND, DEFAULT);
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
