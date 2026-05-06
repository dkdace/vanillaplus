package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/**
 * {@link AnvilBlock}의 블록 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class AnvilBlockModifier extends BlockModifier {
    /** 기본값 */
    public static final AnvilBlockModifier DEFAULT = new AnvilBlockModifier(BlockModifier.DEFAULT.getComponents(), true,
            Optional.empty());
    /** JSON 코덱 */
    public static final MapCodec<AnvilBlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createBaseCodec(instance)
                    .and(instance.group(Codec.BOOL.optionalFieldOf("increase_repair_cost", DEFAULT.increaseRepairCost)
                                    .forGetter(AnvilBlockModifier::isIncreaseRepairCost),
                            CodecUtil.optional(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max_cost")
                                    .forGetter(AnvilBlockModifier::getMaxCost)))
                    .apply(instance, AnvilBlockModifier::new));

    /** 사용 시 작업 가격 증가 여부 */
    private final boolean increaseRepairCost;
    /** 최대 작업 가격 */
    @NonNull
    private final Optional<Optional<Integer>> maxCost;

    private AnvilBlockModifier(@NonNull VPDataComponentMap components, boolean increaseRepairCost, @NonNull Optional<Optional<Integer>> maxCost) {
        super(components);

        this.increaseRepairCost = increaseRepairCost;
        this.maxCost = maxCost;
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    public static AnvilBlockModifier get() {
        return VPModifiableData.getDataModifier(Blocks.ANVIL, DEFAULT);
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
