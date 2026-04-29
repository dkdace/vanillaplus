package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimPattern;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin implements VPMixin<EnchantmentHelper> {
    @Unique
    private static int getFinalEnchantmentLevel(int level, @NonNull ItemInstance itemInstance) {
        return level * itemInstance.getOrDefault(VPDataComponentTypes.ENCHANTMENT_LEVEL_MULTIPLIER.get(), 1);
    }

    @Unique
    @NonNull
    private static ItemEnchantments addArmorTrimEnchantments(@NonNull ItemEnchantments itemEnchantments, @NonNull ItemStack itemStack) {
        ArmorTrim armorTrim = itemStack.get(DataComponents.TRIM);
        if (armorTrim == null)
            return itemEnchantments;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);

        VPTrimPattern.cast(armorTrim.pattern().value()).getEnchantmentHolder().ifPresent(enchantmentHolder ->
                mutable.set(enchantmentHolder, 1));
        VPTrimMaterial.cast(armorTrim.material().value()).getEnchantmentHolder().ifPresent(enchantmentHolder ->
                mutable.set(enchantmentHolder, 1));

        return mutable.toImmutable();
    }

    @ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
    private static int modifyGetEnchantmentLevel(int level, @Local(argsOnly = true) ItemInstance piece) {
        return getFinalEnchantmentLevel(level, piece);
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap$Entry;getIntValue()I"))
    private static int doubleEnchantmentLevel0(int level, @Local(argsOnly = true) ItemStack piece) {
        return getFinalEnchantmentLevel(level, piece);
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap$Entry;getIntValue()I"))
    private static int doubleEnchantmentLevel1(int level, @Local(argsOnly = true) ItemStack piece) {
        return getFinalEnchantmentLevel(level, piece);
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object applyArmorTrimEnchantments0(Object itemEnchantments, @Local(argsOnly = true) ItemStack piece) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, piece);
    }

    @ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private static Object applyArmorTrimEnchantments1(Object itemEnchantments, @Local(argsOnly = true) ItemStack piece) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, piece);
    }

    @ModifyExpressionValue(method = "hasTag", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object applyArmorTrimEnchantments2(Object itemEnchantments, @Local(argsOnly = true) ItemStack item) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, item);
    }

    @ModifyExpressionValue(method = "getRandomItemWith", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object applyArmorTrimEnchantments3(Object itemEnchantments, @Local(name = "item") ItemStack item) {
        return addArmorTrimEnchantments((ItemEnchantments) itemEnchantments, item);
    }
}
