package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.data.GeneralConfig;
import com.dace.vanillaplus.data.TrimMaterialEffect;
import com.dace.vanillaplus.extension.VPMixin;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin implements VPMixin<EnchantmentHelper> {
    @Unique
    @NonNull
    private static ItemEnchantments addArmorTrimEnchantments(@NonNull ItemEnchantments itemEnchantments, @NonNull ItemStack itemStack) {
        ArmorTrim armorTrim = itemStack.get(DataComponents.TRIM);
        if (armorTrim == null)
            return itemEnchantments;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);

        armorTrim.material().unwrapKey()
                .map(TrimMaterialEffect::fromTrimMaterial)
                .ifPresent(trimMaterialEffect -> mutable.set(trimMaterialEffect.getEnchantmentHolder(), 1));

        return mutable.toImmutable();
    }

    @Overwrite
    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int level, ItemStack itemStack,
                                                                           Stream<Holder<Enchantment>> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean flag = itemStack.is(Items.BOOK);

        possibleEnchantments.filter(enchantmentHolder -> itemStack.canApplyAtEnchantingTable(enchantmentHolder) || flag)
                .forEach(enchantmentHolder -> {
                    Enchantment enchantment = enchantmentHolder.value();

                    int maxLevel = enchantmentHolder.unwrapKey()
                            .map(EnchantmentExtension::fromEnchantment)
                            .map(enchantmentExtension -> enchantmentExtension.getMaxLevel(itemStack))
                            .orElse(enchantment.getMaxLevel());

                    for (int i = maxLevel; i >= enchantment.getMinLevel(); i--) {
                        if (level >= enchantment.getMinCost(i) && level <= enchantment.getMaxCost(i)) {
                            list.add(new EnchantmentInstance(enchantmentHolder, i));
                            break;
                        }
                    }
                });

        return list;
    }

    @ModifyExpressionValue(method = "getEnchantmentCost", at = @At(value = "CONSTANT", args = "intValue=15"))
    private static int modifyMaxPower(int maxPower, @Local(argsOnly = true) ItemStack itemStack) {
        return itemStack.is(VPTags.Items.EXTENDED_ENCHANTABLE)
                ? (int) (maxPower * GeneralConfig.get().getExtendedEnchantmentMaxCostMultiplier())
                : maxPower;
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object modifyEnchantments0(Object itemEnchantments, @Local(argsOnly = true) ItemStack itemStack) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, itemStack);
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private static Object modifyEnchantments1(Object itemEnchantments, @Local(argsOnly = true) ItemStack itemStack) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, itemStack);
    }

    @ModifyExpressionValue(method = "hasTag", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object modifyEnchantments2(Object itemEnchantments, @Local(argsOnly = true) ItemStack itemStack) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, itemStack);
    }

    @ModifyExpressionValue(method = "getRandomItemWith", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object modifyEnchantments3(Object itemEnchantments, @Local ItemStack itemStack) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, itemStack);
    }
}
