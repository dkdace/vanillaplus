package com.dace.vanillaplus.mixin.world.level.levelgen.structure.structures;

import com.dace.vanillaplus.mixin.world.level.levelgen.structure.StructurePieceMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BuriedTreasurePieces.BuriedTreasurePiece.class)
public abstract class BuriedTreasurePieceMixin extends StructurePieceMixin {
    @Redirect(method = "postProcess", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/BuriedTreasurePieces$BuriedTreasurePiece;createChest(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean setLootChest(BuriedTreasurePieces.BuriedTreasurePiece instance, ServerLevelAccessor serverLevelAccessor, BoundingBox boundingBox,
                                 RandomSource randomSource, BlockPos blockPos, ResourceKey<LootTable> lootTableResourceKey, BlockState blockState) {
        return createLootChest(serverLevelAccessor, boundingBox, randomSource, blockPos, lootTableResourceKey, blockState, 60);
    }
}
