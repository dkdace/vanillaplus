package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin extends BlockMixin<BlockModifier.DropExperienceModifier> {
    @ModifyArgs(method = "tryAwardExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"))
    private void modifyDropXP(Args args) {
        Objects.requireNonNull(dataModifier);
        args.setAll(dataModifier.getXpRange().getMinValue(), dataModifier.getXpRange().getMaxValue());
    }
}
