package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(ConditionalEffect.class)
public abstract class ConditionalEffectMixin<T> implements VPMixin<ConditionalEffect<T>>, VPLevelBasedProvider {
    @Shadow
    @Final
    private T effect;
    @Shadow
    @Final
    private Optional<LootItemCondition> requirements;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return VPLevelBasedProvider.getFromConditionalEffect(effect, requirements);
    }
}
