package com.dace.vanillaplus.mixin.world.level.storage.loot.providers.number;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(EnchantmentLevelProvider.class)
public abstract class EnchantmentLevelProviderMixin implements VPMixin<EnchantmentLevelProvider>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue amount;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return Collections.singletonList(amount);
    }
}
