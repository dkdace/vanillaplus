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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements VPMixin<AnvilMenu> {
    @Overwrite
    public static int calculateIncreasedRepairCost(int cost) {
        return (int) Math.min(cost + 1L, 2147483647L);
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
