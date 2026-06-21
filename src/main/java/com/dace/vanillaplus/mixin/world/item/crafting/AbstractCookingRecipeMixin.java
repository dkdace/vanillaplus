package com.dace.vanillaplus.mixin.world.item.crafting;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractCookingRecipe.class)
public abstract class AbstractCookingRecipeMixin<T extends AbstractCookingRecipe> extends SingleItemRecipeMixin<T> {
    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        ItemStack itemStack = super.assemble(input);
        itemStack.set(VPDataComponentTypes.EXTRA_FOOD.get(), input.item().get(VPDataComponentTypes.EXTRA_FOOD.get()));

        return itemStack;
    }
}
