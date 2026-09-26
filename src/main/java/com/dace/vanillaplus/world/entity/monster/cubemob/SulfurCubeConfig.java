package com.dace.vanillaplus.world.entity.monster.cubemob;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;

import java.util.Collections;
import java.util.List;

/**
 * {@link SulfurCube}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param canBurn                  연소 가능 여부
 * @param burningNoxiousGasEffects 연소 상태의 유독 가스 상태 효과 목록
 */
public record SulfurCubeConfig(boolean canBurn, @NonNull List<MobEffectInstance> burningNoxiousGasEffects) {
    /** 기본값 */
    private static final SulfurCubeConfig DEFAULT = new SulfurCubeConfig(false, Collections.emptyList());
    /** JSON 코덱 */
    public static final Codec<SulfurCubeConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("can_burn", DEFAULT.canBurn).forGetter(SulfurCubeConfig::canBurn),
                    MobEffectInstance.CODEC.listOf().optionalFieldOf("burning_noxious_gas_effects", DEFAULT.burningNoxiousGasEffects)
                            .forGetter(SulfurCubeConfig::burningNoxiousGasEffects))
            .apply(instance, SulfurCubeConfig::new));

    /**
     * @return {@link SulfurCubeConfig}
     */
    @NonNull
    public static SulfurCubeConfig get() {
        return VPEntityType.cast(EntityTypes.SULFUR_CUBE).getConfigComponents().getOrDefault(EntityConfigComponentTypes.SULFUR_CUBE, DEFAULT);
    }
}
