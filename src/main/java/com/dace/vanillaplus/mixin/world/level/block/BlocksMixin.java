package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @ModifyArgs(method = "lambda$static$224", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/valueproviders/UniformInt;of(II)Lnet/minecraft/util/valueproviders/UniformInt;"))
    private static void modifyQuartzOreDropXP(Args args) {
        args.setAll(Rebalance.QUARTZ_ORE_DROP_XP.getMinimum(), Rebalance.QUARTZ_ORE_DROP_XP.getMaximum());
    }
}
