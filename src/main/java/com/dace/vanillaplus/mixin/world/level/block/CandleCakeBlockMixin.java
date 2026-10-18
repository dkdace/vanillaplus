package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.world.item.FireChargeConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.CandleCakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CandleCakeBlock.class)
public abstract class CandleCakeBlockMixin extends BlockMixin<CandleCakeBlock> {
    @ModifyExpressionValue(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z",
            ordinal = 1))
    private boolean modifyFireChargeUseCondition(boolean isFireCharge) {
        return !FireChargeConfig.get().enableThrowing() && isFireCharge;
    }
}
