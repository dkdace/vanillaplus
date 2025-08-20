package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Overwrite
    public static int calculateIncreasedRepairCost(int cost) {
        return Rebalance.ANVIL_REPAIR_COST.applyAsInt(cost);
    }
}
