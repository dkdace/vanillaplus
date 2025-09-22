package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.rebalance.modifier.BlockModifier;
import com.dace.vanillaplus.rebalance.modifier.DataModifiers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin extends BlockMixin {
    @ModifyArgs(method = "tryAwardExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"))
    private void modifyDropXP(Args args, @Local(argsOnly = true) Level level) {
        BlockModifier.DropExperienceModifier dropExperienceData = (BlockModifier.DropExperienceModifier) DataModifiers.get(level.registryAccess(),
                DataModifiers.BLOCK_MODIFIER_MAP, Blocks.CREAKING_HEART);

        args.setAll(dropExperienceData.getXpRange().getMinValue(), dropExperienceData.getXpRange().getMaxValue());
    }
}
