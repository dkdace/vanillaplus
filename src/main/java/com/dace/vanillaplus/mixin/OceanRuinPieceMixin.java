package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.custom.CustomChestBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public abstract class OceanRuinPieceMixin extends StructurePieceMixin {
    @ModifyArg(method = "handleDataMarker", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            ordinal = 0), index = 1)
    private BlockState modifyChestBlockState(BlockState blockState) {
        return blockState.setValue(CustomLootContainerBlock.vp$LOOT, true);
    }

    @Inject(method = "handleDataMarker", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;setLootTable(Lnet/minecraft/resources/ResourceKey;J)V"))
    private void setLootChestXP(String metadata, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource randomSource,
                                BoundingBox boundingBox, CallbackInfo ci) {
        if (serverLevelAccessor.getBlockEntity(blockPos) instanceof CustomChestBlockEntity customChestBlockEntity)
            customChestBlockEntity.vp$setXp(25);
    }
}
