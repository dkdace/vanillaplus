package com.dace.vanillaplus.custom;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface CustomLootContainerBlock {
    BooleanProperty LOOT = BooleanProperty.create("loot");
    BooleanProperty ALWAYS_OPEN = BooleanProperty.create("always_open");
}
