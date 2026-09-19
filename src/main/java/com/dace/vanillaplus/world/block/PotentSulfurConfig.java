package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PotentSulfurBlock;

import java.util.Collections;
import java.util.List;

/**
 * {@link PotentSulfurBlock}의 블록 설정 데이터 요소 클래스.
 *
 * @param canBurn                   연소 가능 여부
 * @param overrideNoxiousGasEffects 유독 가스 상태 효과 덮어쓰기 여부 또는 목록
 * @param burningNoxiousGasEffects  유독 가스 연소 상태 효과 목록
 */
public record PotentSulfurConfig(boolean canBurn, @NonNull Either<Boolean, List<MobEffectInstance>> overrideNoxiousGasEffects,
                                 @NonNull List<MobEffectInstance> burningNoxiousGasEffects) {
    /** 기본값 */
    private static final PotentSulfurConfig DEFAULT = new PotentSulfurConfig(false, Either.left(false), Collections.emptyList());
    /** JSON 코덱 */
    public static final Codec<PotentSulfurConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("can_burn", DEFAULT.canBurn).forGetter(PotentSulfurConfig::canBurn),
                    Codec.either(Codec.BOOL, MobEffectInstance.CODEC.listOf())
                            .optionalFieldOf("override_noxious_gas_effects", DEFAULT.overrideNoxiousGasEffects)
                            .forGetter(PotentSulfurConfig::overrideNoxiousGasEffects),
                    MobEffectInstance.CODEC.listOf().optionalFieldOf("burning_noxious_gas_effects", DEFAULT.burningNoxiousGasEffects)
                            .forGetter(PotentSulfurConfig::burningNoxiousGasEffects))
            .apply(instance, PotentSulfurConfig::new));

    /**
     * @return {@link PotentSulfurConfig}
     */
    @NonNull
    public static PotentSulfurConfig get() {
        return VPBlock.cast(Blocks.POTENT_SULFUR).getConfigComponents().getOrDefault(BlockConfigComponentTypes.POTENT_SULFUR, DEFAULT);
    }
}
