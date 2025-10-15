package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements VPMixin<AnvilMenu> {
    @Overwrite
    public static int calculateIncreasedRepairCost(int cost) {
        return (int) Math.min(cost + 1L, 2147483647L);
    }
}
