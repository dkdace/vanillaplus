package com.dace.vanillaplus.mixin.world.food;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements VPMixin<FoodData> {
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float modifyFoodExhaustion(float amount, @Local(argsOnly = true) ServerPlayer player) {
        return (float) (amount * player.getAttributeValue(VPAttributes.FOOD_EXHAUSTION_MULTIPLIER.getHolder().orElseThrow()));
    }
}
