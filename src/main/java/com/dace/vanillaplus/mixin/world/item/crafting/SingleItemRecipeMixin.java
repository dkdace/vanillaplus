package com.dace.vanillaplus.mixin.world.item.crafting;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleItemRecipe.class)
public abstract class SingleItemRecipeMixin<T extends SingleItemRecipe> implements VPMixin<T> {
    @Shadow
    @UnknownNullability
    public ItemStack assemble(SingleRecipeInput singleRecipeInput) {
        return null;
    }
}
