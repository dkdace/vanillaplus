package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.LootTableReward;
import lombok.NonNull;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link RandomizableContainerBlockEntity}를 확장하는 인터페이스.
 */
public interface VPRandomizableContainerBlockEntity {
    /**
     * 노획물 테이블 보상을 반환한다.
     *
     * @param randomizableContainerBlockEntity 대상 보관함 블록
     * @return 노획물 테이블 보상. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    static LootTableReward getLootTableReward(@NonNull RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
        return ((VPRandomizableContainerBlockEntity) randomizableContainerBlockEntity).getLootTableReward();
    }

    @Nullable
    LootTableReward getLootTableReward();
}
