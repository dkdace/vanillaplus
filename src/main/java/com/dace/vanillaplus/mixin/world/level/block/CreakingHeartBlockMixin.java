package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.world.block.BlockModifier;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin extends BlockMixin<CreakingHeartBlock, BlockModifier> {
    @ModifyArgs(method = "tryAwardExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"))
    private void modifyDropXP(Args args) {
        getDataModifier().ifPresent(blockModifier ->
                args.setAll(blockModifier.getXpRange().minInclusive(), blockModifier.getXpRange().maxInclusive()));
    }
}
