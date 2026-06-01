package com.dace.vanillaplus.mixin.world.level.storage.loot.predicates;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(LootItemRandomChanceCondition.class)
public abstract class LootItemRandomChanceConditionMixin implements VPMixin<LootItemRandomChanceCondition>, VPLevelBasedProvider {
    @Shadow
    @Final
    private NumberProvider chance;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return chance instanceof VPLevelBasedProvider vpLevelBasedProvider ? vpLevelBasedProvider.getLevelBasedValues() : Collections.emptyList();
    }
}
