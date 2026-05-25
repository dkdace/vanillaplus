package com.dace.vanillaplus.world.entity.monster;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;

import java.util.Optional;

/**
 * {@link Ravager}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param roarCooldown 포효 쿨타임
 */
public record RavagerConfig(@NonNull Optional<Integer> roarCooldown) {
    /** JSON 코덱 */
    public static final Codec<RavagerConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(CodecUtil.secondsToTicks(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("roar_cooldown_seconds")
                    .forGetter(RavagerConfig::roarCooldown))
            .apply(instance, RavagerConfig::new));
    /** 기본값 */
    private static final RavagerConfig DEFAULT = new RavagerConfig(Optional.empty());

    /**
     * @return {@link RavagerConfig}
     */
    @NonNull
    public static RavagerConfig get() {
        return VPEntityType.cast(EntityType.RAVAGER).getConfigComponents().getOrDefault(EntityConfigComponentTypes.RAVAGER, DEFAULT);
    }
}
