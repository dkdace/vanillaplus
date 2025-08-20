package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), cancellable = true)
    private void damageBurnedTool(RegistryAccess registryAccess, RecipeHolder<? extends AbstractCookingRecipe> recipeHolder,
                                  SingleRecipeInput recipeInput, NonNullList<ItemStack> itemStacks, int maxStackSize,
                                  CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack input, @Local(ordinal = 1) ItemStack output) {
        if (!input.isDamageableItem() || !output.is(Items.IRON_NUGGET) && !output.is(Items.GOLD_NUGGET))
            return;
        if (input.getEnchantments().keySet().stream()
                .anyMatch(enchantmentHolder -> enchantmentHolder.value().effects().has(EnchantmentEffectComponents.REPAIR_WITH_XP)))
            return;

        int damage = (int) (input.getDamageValue() + input.getMaxDamage() * Rebalance.SMELTING_TOOL_DAMAGE_RATIO);
        if (damage >= input.getMaxDamage())
            return;

        input.setDamageValue(damage);
        cir.setReturnValue(true);
    }
}
