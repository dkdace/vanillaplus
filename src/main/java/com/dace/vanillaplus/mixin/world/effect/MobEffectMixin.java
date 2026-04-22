package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.world.LevelBasedValuePreset;
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
    private LevelBasedValuePreset levelBasedValuePreset;

    @Override
    @NonNull
    public Optional<LevelBasedValuePreset> getLevelBasedValuePreset() {
        return Optional.ofNullable(levelBasedValuePreset);
    }
}
