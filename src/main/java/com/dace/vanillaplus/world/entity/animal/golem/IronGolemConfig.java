package com.dace.vanillaplus.world.entity.animal.golem;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;

/**
 * {@link IronGolem}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param inflateAttackHitbox 공격 히트박스 확장 여부
 */
public record IronGolemConfig(boolean inflateAttackHitbox) {
    /** 기본값 */
    private static final IronGolemConfig DEFAULT = new IronGolemConfig(false);
    /** JSON 코덱 */
    public static final Codec<IronGolemConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("inflate_attack_hitbox", DEFAULT.inflateAttackHitbox)
                    .forGetter(IronGolemConfig::inflateAttackHitbox))
            .apply(instance, IronGolemConfig::new));

    /**
     * @return {@link IronGolemConfig}
     */
    @NonNull
    public static IronGolemConfig get() {
        return VPEntityType.cast(EntityType.IRON_GOLEM).getConfigComponents().getOrDefault(EntityConfigComponentTypes.IRON_GOLEM, DEFAULT);
    }
}
