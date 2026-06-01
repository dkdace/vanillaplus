package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(AddValue.class)
public abstract class AddValueMixin implements VPMixin<AddValue>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue value;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return Collections.singletonList(value);
    }
}
