package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.world.MobEffectValues;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin<T extends MobEffect> implements VPMobEffect<T> {
    @Unique
    @Nullable
    private MobEffectValues config;

    @Override
    @NonNull
    public final MobEffectValues getConfig() {
        return Objects.requireNonNull(config, "Not initialized yet");
    }

    @Override
    @MustBeInvokedByOverriders
    public void setConfig(@Nullable MobEffectValues config) {
        this.config = config == null ? MobEffectValues.DEFAULT : config;
    }
}
