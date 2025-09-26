package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.rebalance.modifier.LootTableModifier;
import lombok.NonNull;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link RandomizableContainerBlockEntity}를 확장하는 인터페이스.
 */
public interface VPRandomizableContainerBlockEntity {
    /**
     * 노획물 테이블 수정자를 반환한다.
     *
     * @param randomizableContainerBlockEntity 대상 보관함 블록
     * @return 노획물 테이블 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    static LootTableModifier getLootTableModifier(@NonNull RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
        return ((VPRandomizableContainerBlockEntity) randomizableContainerBlockEntity).getLootTableModifier();
    }

    @Nullable
    LootTableModifier getLootTableModifier();
}
