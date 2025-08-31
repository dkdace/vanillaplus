package com.dace.vanillaplus.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StrongholdPieces.class)
public abstract class StrongholdPiecesMixin {
    @Mixin(StrongholdPieces.Library.class)
    public abstract static class LibraryMixin extends StructurePieceMixin {
        @Redirect(method = "postProcess", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$Library;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
        private boolean setLootChest(StrongholdPieces.Library instance, WorldGenLevel worldGenLevel, BoundingBox boundingBox,
                                     RandomSource randomSource, int x, int y, int z, ResourceKey<LootTable> lootTableResourceKey) {
            return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 25);
        }
    }

    @Mixin(StrongholdPieces.RoomCrossing.class)
    public abstract static class RoomCrossingMixin extends StructurePieceMixin {
        @Redirect(method = "postProcess", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$RoomCrossing;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
        private boolean setLootChest(StrongholdPieces.RoomCrossing instance, WorldGenLevel worldGenLevel, BoundingBox boundingBox,
                                     RandomSource randomSource, int x, int y, int z, ResourceKey<LootTable> lootTableResourceKey) {
            return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 25);
        }
    }

    @Mixin(StrongholdPieces.ChestCorridor.class)
    public abstract static class ChestCorridorMixin extends StructurePieceMixin {
        @Redirect(method = "postProcess", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/StrongholdPieces$ChestCorridor;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
        private boolean setLootChest(StrongholdPieces.ChestCorridor instance, WorldGenLevel worldGenLevel, BoundingBox boundingBox,
                                     RandomSource randomSource, int x, int y, int z, ResourceKey<LootTable> lootTableResourceKey) {
            return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 25);
        }
    }
}
