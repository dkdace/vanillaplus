package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements VPMixin<AnvilMenu> {
    @ModifyArg(method = "calculateIncreasedRepairCost", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J"), index = 0)
    private static long modifyIncreasedRepairCost(long cost, @Local(argsOnly = true) int oldCost) {
        return oldCost + 1L;
    }

    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int modifyMaxLevel(int maxLevel, @Local Holder<Enchantment> enchantmentHolder, @Local(ordinal = 0) ItemStack itemStack) {
        return enchantmentHolder.unwrapKey()
                .map(EnchantmentExtension::fromEnchantment)
                .map(enchantmentExtension -> enchantmentExtension.getMaxLevel(itemStack))
                .orElse(maxLevel);
    }
}
