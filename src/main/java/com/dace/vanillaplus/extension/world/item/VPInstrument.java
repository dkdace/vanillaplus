package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Instrument;

import java.util.Optional;

/**
 * {@link Instrument}를 확장하는 인터페이스.
 */
public interface VPInstrument extends VPMixin<Instrument> {
    @NonNull
    static VPInstrument cast(@NonNull Instrument object) {
        return (VPInstrument) (Object) object;
    }

    /**
     * @return 상태 효과 인스턴스
     */
    @NonNull
    Optional<MobEffectInstance> getMobEffectInstance();
}
