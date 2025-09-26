package com.dace.vanillaplus.custom;

import com.dace.vanillaplus.rebalance.modifier.LootTableModifier;
import org.jetbrains.annotations.Nullable;

public interface CustomRandomizableContainerBlockEntity {
    @Nullable
    LootTableModifier getLootTableModifier();
}
