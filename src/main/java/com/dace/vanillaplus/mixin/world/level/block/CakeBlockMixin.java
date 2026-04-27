package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends BlockMixin<CakeBlock, BlockModifier.CakeModifier> {
    @Unique
    private static final Consumable CONSUMABLE = Consumable.builder().sound(SoundEvents.GENERIC_EAT).build();

    @WrapWithCondition(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static boolean redirectEat(FoodData instance, int food, float saturationModifier, @Local(argsOnly = true) Player player) {
        return VPModifiableData.getDataModifier(Blocks.CAKE, BlockModifier.CakeModifier.class).map(cakeModifier -> {
            cakeModifier.getFoodProperties().onConsume(player.level(), player, null, CONSUMABLE);
            return false;
        }).orElse(true);
    }
}
