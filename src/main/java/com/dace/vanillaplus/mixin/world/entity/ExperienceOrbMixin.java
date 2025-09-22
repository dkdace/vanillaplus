package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.rebalance.modifier.GeneralModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends EntityMixin {
    @ModifyArg(method = "repairPlayerItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;"),
            index = 2)
    private Predicate<ItemStack> preventRepair(Predicate<ItemStack> predicate) {
        GeneralModifier generalModifier = level().registryAccess().getOrThrow(GeneralModifier.RESOURCE_KEY).value();
        return predicate.and(itemStack ->
                itemStack.getMaxDamage() - itemStack.getDamageValue() < itemStack.getMaxDamage() * generalModifier.getMendingRepairLimit());
    }

    @ModifyArg(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private int modifyRepairAmount(int damageValue, @Local ItemStack itemStack) {
        GeneralModifier generalModifier = level().registryAccess().getOrThrow(GeneralModifier.RESOURCE_KEY).value();
        return (int) Math.max(damageValue, itemStack.getMaxDamage() - itemStack.getMaxDamage() * generalModifier.getMendingRepairLimit());
    }
}
