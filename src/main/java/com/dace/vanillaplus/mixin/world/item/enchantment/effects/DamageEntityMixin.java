package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(DamageEntity.class)
public abstract class DamageEntityMixin implements VPMixin<DamageEntity>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue minDamage;
    @Shadow
    @Final
    private LevelBasedValue maxDamage;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return List.of(minDamage, maxDamage);
    }
}
