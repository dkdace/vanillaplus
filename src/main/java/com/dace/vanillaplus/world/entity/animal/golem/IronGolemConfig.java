package com.dace.vanillaplus.world.entity.animal.golem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.animal.golem.IronGolem;

/**
 * {@link IronGolem}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param inflateAttackHitbox 공격 히트박스 확장 여부
 */
public record IronGolemConfig(boolean inflateAttackHitbox) {
    /** 기본값 */
    public static final IronGolemConfig DEFAULT = new IronGolemConfig(false);
    /** JSON 코덱 */
    public static final Codec<IronGolemConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("inflate_attack_hitbox", DEFAULT.inflateAttackHitbox)
                    .forGetter(IronGolemConfig::inflateAttackHitbox))
            .apply(instance, IronGolemConfig::new));
}
