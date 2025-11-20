package com.dace.vanillaplus.mixin.world.item.enchantment.providers;

import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SingleEnchantment.class)
public abstract class SingleEnchantmentMixin implements VPMixin<SingleEnchantment> {
    @Shadow
    @Final
    private Holder<Enchantment> enchantment;

    @ModifyExpressionValue(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int modifyMaxLevel(int maxLevel, @Local(argsOnly = true) ItemStack itemStack) {
        return enchantment.unwrapKey()
                .map(EnchantmentExtension::fromEnchantment)
                .map(enchantmentExtension -> enchantmentExtension.getMaxLevel(itemStack))
                .orElse(maxLevel);
    }
}
