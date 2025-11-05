package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin implements VPMixin<EnchantmentMenuMixin> {
    @ModifyExpressionValue(method = "lambda$slotsChanged$0", at = @At(value = "CONSTANT", args = "floatValue=1.0"))
    private float removeBasePowerIncrement(float original) {
        return 0;
    }
}
