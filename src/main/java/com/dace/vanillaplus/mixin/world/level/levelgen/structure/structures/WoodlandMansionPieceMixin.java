package com.dace.vanillaplus.mixin.world.level.levelgen.structure.structures;

import com.dace.vanillaplus.mixin.world.level.levelgen.structure.StructurePieceMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WoodlandMansionPieces.WoodlandMansionPiece.class)
public abstract class WoodlandMansionPieceMixin extends StructurePieceMixin {
    @Redirect(method = "handleDataMarker", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$WoodlandMansionPiece;createChest(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean setLootChest(WoodlandMansionPieces.WoodlandMansionPiece instance, ServerLevelAccessor serverLevelAccessor, BoundingBox boundingBox,
                                 RandomSource randomSource, BlockPos blockPos, ResourceKey<LootTable> lootTableResourceKey, BlockState blockState) {
        return vp$createLootChest(serverLevelAccessor, boundingBox, randomSource, blockPos, lootTableResourceKey, blockState, 20);
    }
}
