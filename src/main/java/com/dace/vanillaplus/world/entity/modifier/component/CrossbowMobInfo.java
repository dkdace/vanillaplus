package com.dace.vanillaplus.world.entity.modifier.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

/**
 * 쇠뇌를 사용하는 몹의 데이터 요소 클래스.
 *
 * @param shootingPower  화살 발사 속력
 * @param shootingRange  공격 거리
 * @param backupDistance 후퇴 거리
 */
public record CrossbowMobInfo(float shootingPower, int shootingRange, int backupDistance) {
    /** JSON 코덱 */
    public static final Codec<CrossbowMobInfo> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("shooting_power").forGetter(CrossbowMobInfo::shootingPower),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("shooting_range").forGetter(CrossbowMobInfo::shootingRange),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("backup_distance", 0).forGetter(CrossbowMobInfo::backupDistance))
            .apply(instance, CrossbowMobInfo::new));
}
