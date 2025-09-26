package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * {@link ChestBlockEntity}를 확장하는 인터페이스.
 */
public interface VPChestBlockEntity {
    /**
     * 상자를 열린 상태로 표시한다.
     *
     * @param chestBlockEntity 대상 상자 엔티티
     */
    static void openLid(@NonNull ChestBlockEntity chestBlockEntity) {
        ((VPChestBlockEntity) chestBlockEntity).openLid();
    }

    void openLid();
}
