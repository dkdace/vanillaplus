package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.LootTableReward;
import lombok.NonNull;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link RandomizableContainerBlockEntity}를 확장하는 인터페이스.
 *
 * @param <T> {@link RandomizableContainerBlockEntity}를 상속받는 타입
 */
public interface VPRandomizableContainerBlockEntity<T extends RandomizableContainerBlockEntity> extends VPMixin<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends RandomizableContainerBlockEntity> VPRandomizableContainerBlockEntity<T> cast(@NonNull T object) {
        return (VPRandomizableContainerBlockEntity<T>) object;
    }

    /**
     * 노획물 테이블 보상을 반환한다.
     *
     * @return 노획물 테이블 보상. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    LootTableReward getLootTableReward();
}
