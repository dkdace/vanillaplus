package com.dace.vanillaplus.mixin.world.level.storage.loot.predicates;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPLevelBasedProvider;
import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CompositeLootItemCondition.class)
public abstract class CompositeLootItemConditionMixin implements VPMixin<CompositeLootItemCondition>, VPLevelBasedProvider {
    @Shadow
    @Final
    protected List<LootItemCondition> terms;

    @Override
    @NonNull
    public List<LevelBasedValue> getLevelBasedValues() {
        return VPLevelBasedProvider.getFromList(terms);
    }
}
