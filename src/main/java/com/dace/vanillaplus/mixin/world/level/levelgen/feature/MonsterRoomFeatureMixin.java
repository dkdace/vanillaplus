package com.dace.vanillaplus.mixin.world.level.levelgen.feature;

import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin {
    @ModifyArg(method = "place", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;reorient(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"),
            index = 2)
    private BlockState modifyChestBlockState(BlockState blockState) {
        return blockState.setValue(CustomLootContainerBlock.LOOT, true);
    }
}
