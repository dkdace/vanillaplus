package com.dace.vanillaplus.mixin.server.commands;

import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantCommand.class)
public abstract class EnchantCommandMixin implements VPMixin<EnchantCommand> {
    @ModifyExpressionValue(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int modifyMaxLevel(int maxLevel, @Local(argsOnly = true) Holder<Enchantment> enchantmentHolder) {
        ResourceKey<Enchantment> enchantmentResourceKey = enchantmentHolder.unwrapKey().orElse(null);

        if (enchantmentResourceKey != null) {
            EnchantmentExtension enchantmentExtension = EnchantmentExtension.fromEnchantment(enchantmentResourceKey);
            if (enchantmentExtension != null)
                return enchantmentExtension.getMaxLevel();
        }

        return maxLevel;
    }
}
