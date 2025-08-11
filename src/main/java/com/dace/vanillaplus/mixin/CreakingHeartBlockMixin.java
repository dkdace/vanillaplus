package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.Rebalance;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreakingHeartBlock.class)
public final class CreakingHeartBlockMixin {
    @ModifyArgs(method = "tryAwardExperience", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"))
    private void modifyDropXP(Args args) {
        args.setAll(Rebalance.Creaking.CREAKING_HEART_DROP_XP.getMinimum(), Rebalance.Creaking.CREAKING_HEART_DROP_XP.getMaximum());
    }
}
