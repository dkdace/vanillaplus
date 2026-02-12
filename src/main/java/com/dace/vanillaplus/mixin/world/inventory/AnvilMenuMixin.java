package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements VPMixin<AnvilMenu> {
    @ModifyArg(method = "calculateIncreasedRepairCost", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J"), index = 0)
    private static long modifyIncreasedRepairCost(long cost, @Local(argsOnly = true) int oldCost) {
        return oldCost + 1L;
    }
}
