package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.dace.vanillaplus.extension.world.inventory.VPBrewingStandMenu;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreenMixin<BrewingStandScreen, BrewingStandMenu> {
    @ModifyExpressionValue(method = "renderBg", at = @At(value = "CONSTANT", args = "floatValue=400"))
    private float modifyTotalBrewTime(float ticks) {
        return VPBrewingStandMenu.cast(menu).getTotalBrewTime();
    }
}
