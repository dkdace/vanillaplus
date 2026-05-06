package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;

import java.util.Optional;

/**
 * {@link CakeBlock}의 블록 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class CakeBlockModifier extends BlockModifier {
    /** 기본값 */
    public static final CakeBlockModifier DEFAULT = new CakeBlockModifier(BlockModifier.DEFAULT.getComponents(), Optional.empty());
    /** JSON 코덱 */
    public static final MapCodec<CakeBlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createBaseCodec(instance)
                    .and(FoodProperties.DIRECT_CODEC.optionalFieldOf("food").forGetter(CakeBlockModifier::getFoodProperties))
                    .apply(instance, CakeBlockModifier::new));

    /** 음식 속성 */
    @NonNull
    private final Optional<FoodProperties> foodProperties;

    private CakeBlockModifier(@NonNull VPDataComponentMap components, @NonNull Optional<FoodProperties> foodProperties) {
        super(components);
        this.foodProperties = foodProperties;
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    public static CakeBlockModifier get() {
        return VPModifiableData.getDataModifier(Blocks.CAKE, DEFAULT);
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
