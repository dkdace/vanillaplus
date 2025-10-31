package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPItemStack;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin implements VPMixin<GrindstoneMenu> {
    @Inject(method = "mergeItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private void resetRepairLimit(ItemStack topItemStack, ItemStack bottomItemStack, CallbackInfoReturnable<ItemStack> cir,
                                  @Local(ordinal = 2) ItemStack resultItemStack) {
        VPItemStack.cast(resultItemStack).setRepairLimit(0);
    }
}
