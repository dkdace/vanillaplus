package com.dace.vanillaplus.custom;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface CustomChestBlock {
    BooleanProperty vp$LOOT = BooleanProperty.create("loot");
    BooleanProperty vp$ALWAYS_OPEN = BooleanProperty.create("always_open");
}
