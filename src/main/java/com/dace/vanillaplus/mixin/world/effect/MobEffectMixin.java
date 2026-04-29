package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.world.MobEffectValues;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin<T extends MobEffect> implements VPMobEffect<T> {
    @Unique
    @Nullable
    @Setter
    private MobEffectValues dataModifier;

    @Override
    @NonNull
    public Optional<MobEffectValues> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }
}
