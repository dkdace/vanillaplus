package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.dace.vanillaplus.world.block.AnvilConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends AbstractContainerScreenMixin<AnvilScreen, AnvilMenu> {
    @Definition(id = "cost", local = @Local(type = int.class, name = "cost"))
    @Expression("cost >= 40")
    @ModifyExpressionValue(method = "extractLabels", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyMaxCostCondition(boolean condition, @Local(name = "cost") int cost) {
        return AnvilConfig.get().maxCost().map(maxCost -> maxCost >= 0 && cost > maxCost).orElse(condition);
    }
}
