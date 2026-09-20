package com.dace.vanillaplus.mixin.world.entity.animal;

import com.dace.vanillaplus.data.registryobject.VPItems;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Animal.class)
public abstract class AnimalMixin<T extends Animal> extends MobMixin<T> {
    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;isFood(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean modifyFoodCondition(boolean isFood, @Local(name = "itemStack") ItemStack itemStack) {
        return isFood || itemStack.is(VPItems.SULFUR_POWDER.get());
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;canFallInLove()Z"))
    private boolean modifyLoveCondition(boolean canFallInLove, @Local(name = "itemStack") ItemStack itemStack) {
        return canFallInLove && !itemStack.is(VPItems.SULFUR_POWDER.get());
    }
}
