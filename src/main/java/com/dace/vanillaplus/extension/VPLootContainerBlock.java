package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.LootTableReward;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * 모드에서 사용하는 보관함 블록(상자, 통 등)을 확장하는 인터페이스.
 *
 * @param <T> {@link BaseEntityBlock}를 상속받는 타입
 */
public interface VPLootContainerBlock<T extends BaseEntityBlock> extends VPMixin<T> {
    /** 전리품 보관함 여부 */
    BooleanProperty LOOT = BooleanProperty.create("loot");
    /** 항상 열려 있는지 여부 */
    BooleanProperty ALWAYS_OPEN = BooleanProperty.create("always_open");

    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends BaseEntityBlock> VPLootContainerBlock<T> cast(@NonNull T object) {
        return (VPLootContainerBlock<T>) object;
    }

    /**
     * 지정한 위치에 경험치를 생성한다.
     *
     * @param blockState 블록 상태
     * @param level      월드
     * @param blockPos   블록 위치
     */
    default void popOpenXP(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel) || !blockState.getValue(LOOT) || blockState.getValue(ALWAYS_OPEN)
                || !(level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity))
            return;

        LootTableReward lootTableReward = VPRandomizableContainerBlockEntity.cast(randomizableContainerBlockEntity).getLootTableReward();
        if (lootTableReward == null)
            return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(ALWAYS_OPEN, true));
        self().popExperience(serverLevel, blockPos, lootTableReward.getXpRange().sample(level.random));
    }

    /**
     * 블록 채굴 시 드롭되는 경험치 양을 반환한다.
     *
     * @param blockState   블록 상태
     * @param levelReader  월드
     * @param randomSource 랜덤 소스
     * @param blockPos     블록 위치
     * @return 드롭되는 경험치 양
     */
    default int getXp(@NonNull BlockState blockState, @NonNull LevelReader levelReader, @NonNull RandomSource randomSource, @NonNull BlockPos blockPos) {
        if (blockState.getValue(LOOT) && !blockState.getValue(ALWAYS_OPEN)
                && levelReader.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            LootTableReward lootTableReward = VPRandomizableContainerBlockEntity.cast(randomizableContainerBlockEntity).getLootTableReward();
            if (lootTableReward != null)
                return lootTableReward.getXpRange().sample(randomSource);
        }

        return 0;
    }
}
