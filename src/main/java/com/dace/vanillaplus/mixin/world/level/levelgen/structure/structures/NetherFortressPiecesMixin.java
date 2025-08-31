package com.dace.vanillaplus.mixin.world.level.levelgen.structure.structures;

import com.dace.vanillaplus.mixin.world.level.levelgen.structure.StructurePieceMixin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherFortressPieces.class)
public abstract class NetherFortressPiecesMixin {
    @Mixin(NetherFortressPieces.CastleSmallCorridorLeftTurnPiece.class)
    public abstract static class CastleSmallCorridorLeftTurnPieceMixin extends StructurePieceMixin {
        @Redirect(method = "postProcess", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressPieces$CastleSmallCorridorLeftTurnPiece;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
        private boolean setLootChest(NetherFortressPieces.CastleSmallCorridorLeftTurnPiece instance, WorldGenLevel worldGenLevel,
                                     BoundingBox boundingBox, RandomSource randomSource, int x, int y, int z,
                                     ResourceKey<LootTable> lootTableResourceKey) {
            return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 25);
        }
    }

    @Mixin(NetherFortressPieces.CastleSmallCorridorRightTurnPiece.class)
    public abstract static class CastleSmallCorridorRightTurnPieceMixin extends StructurePieceMixin {
        @Redirect(method = "postProcess", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressPieces$CastleSmallCorridorRightTurnPiece;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
        private boolean setLootChest(NetherFortressPieces.CastleSmallCorridorRightTurnPiece instance, WorldGenLevel worldGenLevel,
                                     BoundingBox boundingBox, RandomSource randomSource, int x, int y, int z,
                                     ResourceKey<LootTable> lootTableResourceKey) {
            return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 25);
        }
    }
}
