package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.dace.vanillaplus.world.item.effect.InstrumentEffect;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.world.item.Instrument;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Instrument.class)
public abstract class InstrumentMixin implements VPInstrument {
    @Unique
    @Nullable
    @Setter
    private InstrumentEffect dataModifier;

    @Override
    @NonNull
    public Optional<InstrumentEffect> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }
}
