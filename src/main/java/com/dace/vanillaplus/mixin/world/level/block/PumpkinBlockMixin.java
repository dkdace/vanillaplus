package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.PumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PumpkinBlock.class)
public abstract class PumpkinBlockMixin extends BlockMixin<PumpkinBlock, BlockModifier> {
    @ModifyExpressionValue(method = "useItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z"))
    private boolean invertCondition(boolean original) {
        return !original;
    }
}
