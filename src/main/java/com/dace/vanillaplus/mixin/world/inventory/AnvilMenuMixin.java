package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.block.AnvilConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements VPMixin<AnvilMenu> {
    @Shadow
    @Final
    private DataSlot cost;

    @ModifyArg(method = "calculateIncreasedRepairCost", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J"), index = 0)
    private static long modifyIncreasedRepairCost(long cost, @Local(argsOnly = true) int baseCost) {
        return AnvilConfig.get().increaseRepairCost() ? cost : baseCost;
    }

    @Definition(id = "cost", field = "Lnet/minecraft/world/inventory/AnvilMenu;cost:Lnet/minecraft/world/inventory/DataSlot;")
    @Definition(id = "get", method = "Lnet/minecraft/world/inventory/DataSlot;get()I")
    @Expression("this.cost.get() >= 40")
    @ModifyExpressionValue(method = "createResult", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyMaxCostCondition(boolean condition) {
        return AnvilConfig.get().maxCost().map(maxCost -> maxCost >= 0 && cost.get() > maxCost).orElse(condition);
    }

    @ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 5))
    private int modifyMaxCost(int value) {
        return AnvilConfig.get().maxCost().map(maxCost -> maxCost >= 0 ? maxCost : cost.get()).orElse(value);
    }
}
