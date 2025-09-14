package com.dace.vanillaplus.mixin.world.level.levelgen.structure;

import com.dace.vanillaplus.custom.CustomChestBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {
    @Shadow
    @UnknownNullability
    public static BlockState reorient(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Shadow
    protected abstract BlockPos.MutableBlockPos getWorldPos(int x, int y, int z);

    @Unique
    protected boolean createLootChest(@NonNull WorldGenLevel worldGenLevel, @NonNull BoundingBox boundingBox, @NonNull RandomSource randomSource,
                                      int x, int y, int z, @NonNull ResourceKey<LootTable> lootTableResourceKey, int xp) {
        return createLootChest(worldGenLevel, boundingBox, randomSource, getWorldPos(x, y, z), lootTableResourceKey, null, xp);
    }

    @Unique
    protected boolean createLootChest(@NonNull ServerLevelAccessor serverLevelAccessor, @NonNull BoundingBox boundingBox,
                                      @NonNull RandomSource randomSource, @NonNull BlockPos blockPos,
                                      @NonNull ResourceKey<LootTable> lootTableResourceKey, @Nullable BlockState blockState, int xp) {
        if (!boundingBox.isInside(blockPos) || serverLevelAccessor.getBlockState(blockPos).is(Blocks.CHEST))
            return false;

        if (blockState == null)
            blockState = reorient(serverLevelAccessor, blockPos, Blocks.CHEST.defaultBlockState());

        if (blockState == null)
            return false;

        serverLevelAccessor.setBlock(blockPos, blockState.setValue(CustomLootContainerBlock.LOOT, true), 2);

        BlockEntity blockentity = serverLevelAccessor.getBlockEntity(blockPos);
        if (blockentity instanceof ChestBlockEntity chestBlockEntity) {
            chestBlockEntity.setLootTable(lootTableResourceKey, randomSource.nextLong());
            ((CustomChestBlockEntity) chestBlockEntity).setXp(xp);
        }

        return true;
    }
}
