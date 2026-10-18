package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.world.item.FireChargeConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends BlockMixin<TntBlock> {
    @Definition(id = "itemStack", local = @Local(type = ItemStack.class, argsOnly = true))
    @Definition(id = "is", method = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
    @Definition(id = "FIRE_CHARGE", field = "Lnet/minecraft/world/item/Items;FIRE_CHARGE:Lnet/minecraft/world/item/Item;")
    @Expression("itemStack.is(FIRE_CHARGE) == false")
    @ModifyExpressionValue(method = "useItemOn", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyFireChargeUseCondition(boolean condition) {
        return FireChargeConfig.get().enableThrowing() || condition;
    }
}
