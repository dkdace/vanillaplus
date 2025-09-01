package com.dace.vanillaplus.mixin.world.level.levelgen.feature;

import com.dace.vanillaplus.custom.CustomChestBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BonusChestFeature.class)
public abstract class BonusChestFeatureMixin {
    @ModifyArg(method = "place", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            ordinal = 0), index = 1)
    private BlockState modifyChestBlockState(BlockState blockState) {
        return blockState.setValue(CustomLootContainerBlock.LOOT, true);
    }

    @Inject(method = "place", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/RandomizableContainer;setBlockEntityLootTable(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceKey;)V"))
    private void setLootChestXP(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext, CallbackInfoReturnable<Boolean> cir,
                                @Local WorldGenLevel worldGenLevel, @Local BlockPos blockPos) {
        if (worldGenLevel.getBlockEntity(blockPos) instanceof CustomChestBlockEntity customChestBlockEntity)
            customChestBlockEntity.setXp(10);
    }
}
