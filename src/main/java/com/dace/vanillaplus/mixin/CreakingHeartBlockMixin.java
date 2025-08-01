package com.dace.vanillaplus.mixin;

import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreakingHeartBlock.class)
public final class CreakingHeartBlockMixin {
    @ModifyArgs(method = "tryAwardExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"))
    private void getXP(Args args) {
        args.set(0, 26);
        args.set(1, 32);
    }
}
