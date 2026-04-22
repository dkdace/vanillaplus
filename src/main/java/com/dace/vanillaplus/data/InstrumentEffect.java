package com.dace.vanillaplus.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * 염소 뿔의 효과를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class InstrumentEffect {
    /** JSON 코덱 */
    public static final Codec<InstrumentEffect> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(MobEffectInstance.CODEC.fieldOf("effect").forGetter(instrumentEffect -> instrumentEffect.mobEffectInstance))
            .apply(instance, InstrumentEffect::new));

    /** 상태 효과 인스턴스 */
    private final MobEffectInstance mobEffectInstance;
}
