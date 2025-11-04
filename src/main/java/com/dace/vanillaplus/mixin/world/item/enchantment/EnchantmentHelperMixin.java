package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.data.modifier.GeneralModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin implements VPMixin<EnchantmentHelper> {
    @Overwrite
    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int level, ItemStack itemStack,
                                                                           Stream<Holder<Enchantment>> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean flag = itemStack.is(Items.BOOK);

        possibleEnchantments.filter(enchantmentHolder -> itemStack.canApplyAtEnchantingTable(enchantmentHolder) || flag)
                .forEach(enchantmentHolder -> {
                    ResourceKey<Enchantment> enchantmentResourceKey = enchantmentHolder.unwrapKey().orElse(null);
                    Enchantment enchantment = enchantmentHolder.value();
                    int maxLevel = enchantment.getMaxLevel();

                    if (enchantmentResourceKey != null) {
                        EnchantmentExtension enchantmentExtension = EnchantmentExtension.fromEnchantment(enchantmentResourceKey);
                        if (enchantmentExtension != null)
                            maxLevel = enchantmentExtension.getMaxLevel(itemStack);
                    }

                    for (int i = maxLevel; i >= enchantment.getMinLevel(); i--) {
                        if (level >= enchantment.getMinCost(i) && level <= enchantment.getMaxCost(i)) {
                            list.add(new EnchantmentInstance(enchantmentHolder, i));
                            break;
                        }
                    }
                });

        return list;
    }

    @Expression("15")
    @ModifyExpressionValue(method = "getEnchantmentCost", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static int modifyMaxPower(int maxPower, @Local(argsOnly = true) ItemStack itemStack) {
        return itemStack.is(VPTags.Items.EXTENDED_ENCHANTABLE)
                ? (int) (maxPower * GeneralModifier.get().getExtendedEnchantmentCostMultiplier())
                : maxPower;
    }
}
