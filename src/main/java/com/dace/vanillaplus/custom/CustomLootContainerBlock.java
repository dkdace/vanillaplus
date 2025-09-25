package com.dace.vanillaplus.custom;

import com.dace.vanillaplus.rebalance.modifier.LootTableModifier;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface CustomLootContainerBlock {
    BooleanProperty LOOT = BooleanProperty.create("loot");
    BooleanProperty ALWAYS_OPEN = BooleanProperty.create("always_open");

    default void popOpenXP(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel) || !blockState.getValue(LOOT) || blockState.getValue(ALWAYS_OPEN)
                || !(level.getBlockEntity(blockPos) instanceof CustomRandomizableContainerBlockEntity customRandomizableContainerBlockEntity))
            return;

        LootTableModifier lootTableModifier = customRandomizableContainerBlockEntity.getLootTableModifier();
        if (lootTableModifier == null)
            return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(ALWAYS_OPEN, true));
        ((Block) this).popExperience(serverLevel, blockPos, lootTableModifier.getXpRange().sample(level.random));
    }

    default int getXp(@NonNull BlockState state, @NonNull LevelReader levelReader, @NonNull RandomSource randomSource, @NonNull BlockPos blockPos) {
        if (state.getValue(LOOT) && !state.getValue(ALWAYS_OPEN)
                && levelReader.getBlockEntity(blockPos) instanceof CustomRandomizableContainerBlockEntity customRandomizableContainerBlockEntity) {
            LootTableModifier lootTableModifier = customRandomizableContainerBlockEntity.getLootTableModifier();
            if (lootTableModifier != null)
                return lootTableModifier.getXpRange().sample(randomSource);
        }

        return 0;
    }
}
