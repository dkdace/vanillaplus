package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.item.effect.InstrumentEffect;
import lombok.NonNull;
import net.minecraft.world.item.Instrument;

/**
 * {@link Instrument}를 확장하는 인터페이스.
 *
 * @see InstrumentEffect
 */
public interface VPInstrument extends VPMixin<Instrument>, VPModifiableData<Instrument, InstrumentEffect> {
    @NonNull
    static VPInstrument cast(@NonNull Instrument object) {
        return (VPInstrument) (Object) object;
    }
}
