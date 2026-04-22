package com.dace.vanillaplus.extension.world.level.block;

import com.dace.vanillaplus.extension.world.level.block.entity.VPRandomizableContainerBlockEntity;
import com.dace.vanillaplus.world.LootTableReward;
import com.dace.vanillaplus.world.block.BlockModifier;
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
public interface VPLootContainerBlock<T extends BaseEntityBlock, U extends BlockModifier> extends VPBlock<T, U> {
    /** 전리품 보관함 여부 */
    BooleanProperty LOOT = BooleanProperty.create("loot");
    /** 항상 열려 있는지 여부 */
    BooleanProperty ALWAYS_OPEN = BooleanProperty.create("always_open");

    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends BaseEntityBlock, U extends BlockModifier> VPLootContainerBlock<T, U> cast(@NonNull T object) {
        return (VPLootContainerBlock<T, U>) object;
    }

    /**
     * 경험치 획득량을 반환한다.
     *
     * @param blockState   블록 사앹
     * @param level        월드
     * @param randomSource 랜덤 소스
     * @param blockPos     블록 위치
     * @return 경험치 획득량
     */
    default int getXP(@NonNull BlockState blockState, @NonNull LevelReader level, @NonNull RandomSource randomSource, @NonNull BlockPos blockPos) {
        if (blockState.getValue(LOOT) && !blockState.getValue(ALWAYS_OPEN)
                && level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            LootTableReward lootTableReward = VPRandomizableContainerBlockEntity.cast(randomizableContainerBlockEntity).getLootTableReward();
            if (lootTableReward != null)
                return lootTableReward.getXpRange().sample(randomSource);
        }

        return 0;
    }

    /**
     * 지정한 위치에 노획물 테이블 보상을 생성한다.
     *
     * @param blockState 블록 상태
     * @param level      월드
     * @param blockPos   블록 위치
     */
    default void awardLootTableReward(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        int xp = getXP(blockState, serverLevel, level.getRandom(), blockPos);
        if (xp <= 0)
            return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(ALWAYS_OPEN, true));
        getThis().popExperience(serverLevel, blockPos, xp);
    }
}
