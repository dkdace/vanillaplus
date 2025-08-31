package com.dace.vanillaplus.mixin.world.level.levelgen.structure.structures;

import com.dace.vanillaplus.mixin.world.level.levelgen.structure.StructurePieceMixin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DesertPyramidPiece.class)
public abstract class DesertPyramidPieceMixin extends StructurePieceMixin {
    @Redirect(method = "postProcess", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/DesertPyramidPiece;createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z"))
    private boolean setLootChest(DesertPyramidPiece instance, WorldGenLevel worldGenLevel, BoundingBox boundingBox, RandomSource randomSource, int x,
                                 int y, int z, ResourceKey<LootTable> lootTableResourceKey) {
        return vp$createLootChest(worldGenLevel, boundingBox, randomSource, x, y, z, lootTableResourceKey, 20);
    }
}
