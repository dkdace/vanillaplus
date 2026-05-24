package com.dace.vanillaplus.world.entity.projectile;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

import java.util.Optional;

/**
 * {@link FireworkRocketEntity}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param flightAddSpeedMultiplier 비행 추가 속도 배수
 * @param flightFinalSpeedModifier 비행 최종 속도 배수
 */
public record FireworkRocketConfig(@NonNull Optional<Float> flightAddSpeedMultiplier, @NonNull Optional<Float> flightFinalSpeedModifier) {
    /** 기본값 */
    public static final FireworkRocketConfig DEFAULT = new FireworkRocketConfig(Optional.empty(), Optional.empty());
    /** JSON 코덱 */
    public static final Codec<FireworkRocketConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("flight_add_speed_multiplier")
                            .forGetter(FireworkRocketConfig::flightAddSpeedMultiplier),
                    ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("flight_final_speed_multiplier")
                            .forGetter(FireworkRocketConfig::flightFinalSpeedModifier))
            .apply(instance, FireworkRocketConfig::new));

    @NonNull
    public static FireworkRocketConfig get() {
        return VPEntityType.cast(EntityType.FIREWORK_ROCKET).getConfigComponents().getOrDefault(EntityConfigComponentTypes.FIREWORK_ROCKET, DEFAULT);
    }
}
