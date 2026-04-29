package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(ExplodeEffect.class)
public abstract class ExplodeEffectMixin implements VPMixin<ExplodeEffect>, VPLevelBasedProvider {
    @Shadow
    @Final
    private Optional<LevelBasedValue> knockbackMultiplier;
    @Shadow
    @Final
    private LevelBasedValue radius;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return knockbackMultiplier.map(levelBasedValue -> List.of(radius, levelBasedValue)).orElse(Collections.singletonList(radius));
    }
}
