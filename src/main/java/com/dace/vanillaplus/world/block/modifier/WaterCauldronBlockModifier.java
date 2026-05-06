package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Blocks;

/**
 * {@link Blocks#WATER_CAULDRON}의 블록 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class WaterCauldronBlockModifier extends BlockModifier {
    /** 기본값 */
    public static final WaterCauldronBlockModifier DEFAULT = new WaterCauldronBlockModifier(BlockModifier.DEFAULT.getComponents(), 0,
            0);
    /** JSON 코덱 */
    public static final MapCodec<WaterCauldronBlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createBaseCodec(instance)
                    .and(instance.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_potion_effects", DEFAULT.maxPotionEffects)
                                    .forGetter(WaterCauldronBlockModifier::getMaxPotionEffects),
                            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_tipped_arrow_count", DEFAULT.maxTippedArrowCount)
                                    .forGetter(WaterCauldronBlockModifier::getMaxTippedArrowCount)))
                    .apply(instance, WaterCauldronBlockModifier::new));

    /** 담을 수 있는 물약의 최대 상태 효과 종류 수 */
    private final int maxPotionEffects;
    /** 제작 가능한 물약이 묻은 화살의 최대 개수 */
    private final int maxTippedArrowCount;

    private WaterCauldronBlockModifier(@NonNull VPDataComponentMap components, int maxPotionEffects, int maxTippedArrowCount) {
        super(components);

        this.maxPotionEffects = maxPotionEffects;
        this.maxTippedArrowCount = maxTippedArrowCount;
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    public static WaterCauldronBlockModifier get() {
        return VPModifiableData.getDataModifier(Blocks.WATER_CAULDRON, DEFAULT);
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
