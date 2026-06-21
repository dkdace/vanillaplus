package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ReplaceDisk.class)
public abstract class ReplaceDiskMixin implements VPMixin<ReplaceDisk>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue radius;
    @Shadow
    @Final
    private LevelBasedValue height;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return List.of(radius, height);
    }
}
