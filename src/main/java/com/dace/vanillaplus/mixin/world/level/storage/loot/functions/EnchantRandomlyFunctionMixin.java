package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin implements VPMixin<EnchantRandomlyFunction> {
    @ModifyExpressionValue(method = "enchantItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int modifyMaxLevel(int maxLevel, @Local(argsOnly = true) Holder<Enchantment> enchantmentHolder,
                                      @Local(argsOnly = true) ItemStack itemStack) {
        return enchantmentHolder.unwrapKey()
                .map(EnchantmentExtension::fromEnchantment)
                .map(enchantmentExtension -> enchantmentExtension.getMaxLevel(itemStack))
                .orElse(maxLevel);
    }
}
