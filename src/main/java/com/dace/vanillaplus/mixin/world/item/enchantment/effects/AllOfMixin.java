package com.dace.vanillaplus.mixin.world.item.enchantment.effects;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

public interface AllOfMixin {
    @Mixin(AllOf.EntityEffects.class)
    abstract class EntityEffectsMixin implements VPMixin<AllOf.EntityEffects>, VPLevelBasedProvider {
        @Shadow
        @Final
        private List<EnchantmentEntityEffect> effects;

        @Override
        @NonNull
        public List<LevelBasedValue> getLevelBasedValues() {
            return VPLevelBasedProvider.getFromList(effects);
        }
    }

    @Mixin(AllOf.LocationBasedEffects.class)
    abstract class LocationBasedEffectsMixin implements VPMixin<AllOf.LocationBasedEffects>, VPLevelBasedProvider {
        @Shadow
        @Final
        private List<EnchantmentLocationBasedEffect> effects;

        @Override
        @NonNull
        public List<LevelBasedValue> getLevelBasedValues() {
            return VPLevelBasedProvider.getFromList(effects);
        }
    }

    @Mixin(AllOf.ValueEffects.class)
    abstract class ValueEffectsMixin implements VPMixin<AllOf.ValueEffects>, VPLevelBasedProvider {
        @Shadow
        @Final
        private List<EnchantmentValueEffect> effects;

        @Override
        @NonNull
        public List<LevelBasedValue> getLevelBasedValues() {
            return VPLevelBasedProvider.getFromList(effects);
        }
    }
}
