package com.dace.vanillaplus.mixin.world.level.storage.loot.predicates;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public abstract class LootItemRandomChanceWithEnchantedBonusConditionMixin implements VPMixin<LootItemRandomChanceWithEnchantedBonusCondition>, VPLevelBasedProvider {
    @Shadow
    @Final
    private LevelBasedValue enchantedChance;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return Collections.singletonList(enchantedChance);
    }
}
