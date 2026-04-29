package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ChangeItemDamage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(ChangeItemDamage.class)
public abstract class ChangeItemDamageMixin implements VPMixin<ChangeItemDamage>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue amount;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return Collections.singletonList(amount);
    }
}
