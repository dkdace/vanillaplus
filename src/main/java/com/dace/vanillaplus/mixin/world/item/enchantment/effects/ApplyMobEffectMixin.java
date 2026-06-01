package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ApplyMobEffect.class)
public abstract class ApplyMobEffectMixin implements VPMixin<ApplyMobEffect>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue minDuration;
    @Shadow
    @Final
    private LevelBasedValue maxDuration;
    @Shadow
    @Final
    private LevelBasedValue minAmplifier;
    @Shadow
    @Final
    private LevelBasedValue maxAmplifier;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return List.of(minDuration, maxDuration, minAmplifier, maxAmplifier);
    }
}
