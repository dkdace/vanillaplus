package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin<T extends AbstractFurnaceBlockEntity> extends BlockEntityMixin<T> {
    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), cancellable = true)
    private void damageBurnedItem(RegistryAccess registryAccess, RecipeHolder<? extends AbstractCookingRecipe> recipeHolder,
                                  SingleRecipeInput recipeInput, NonNullList<ItemStack> itemStacks, int maxStackSize,
                                  CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack input) {
        Float smeltingDamageRatio = input.get(VPDataComponentTypes.SMELTING_DAMAGE_RATIO.get());
        if (smeltingDamageRatio == null)
            return;

        int damage = (int) (input.getDamageValue() + input.getMaxDamage() * smeltingDamageRatio);
        if (damage >= input.getMaxDamage())
            return;

        input.setDamageValue(damage);
        cir.setReturnValue(true);
    }
}
