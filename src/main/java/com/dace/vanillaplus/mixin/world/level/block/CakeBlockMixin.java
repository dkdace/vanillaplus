package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends BlockMixin<CakeBlock, BlockModifier.CakeModifier> {
    @Unique
    private static final Consumable CONSUMABLE = Consumable.builder().sound(SoundEvents.GENERIC_EAT).build();

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void redirectEat(FoodData foodData, int foodLevelModifier, float saturationLevelModifier, @Local(argsOnly = true) Player player) {
        BlockModifier.CakeModifier cakeModifier = VPModifiableData.getDataModifier(Blocks.CAKE, BlockModifier.CakeModifier.class).orElse(null);

        if (cakeModifier == null)
            foodData.eat(foodLevelModifier, saturationLevelModifier);
        else
            cakeModifier.getFoodProperties().onConsume(player.level(), player, null, CONSUMABLE);
    }
}
