package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends AbstractContainerScreenMixin<AnvilScreen, AnvilMenu> {
    @Definition(id = "i", local = @Local(type = int.class, ordinal = 2))
    @Expression("i >= 40")
    @ModifyExpressionValue(method = "extractLabels", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyMaxCostCondition(boolean condition, @Local(name = "cost") int cost) {
        return VPModifiableData.getDataModifier(Blocks.ANVIL, BlockModifier.AnvilModifier.class)
                .map(anvilModifier -> anvilModifier.getMaxCost().map(value -> cost > value).orElse(false))
                .orElse(condition);
    }
}
