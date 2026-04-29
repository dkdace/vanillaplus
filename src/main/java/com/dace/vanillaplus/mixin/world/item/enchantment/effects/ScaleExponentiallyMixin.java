package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ScaleExponentially;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ScaleExponentially.class)
public abstract class ScaleExponentiallyMixin implements VPMixin<ScaleExponentially>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue base;
    @Shadow
    @Final
    private LevelBasedValue exponent;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return List.of(base, exponent);
    }
}
