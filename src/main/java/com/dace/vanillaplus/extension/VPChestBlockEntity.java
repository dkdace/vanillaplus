package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * {@link ChestBlockEntity}를 확장하는 인터페이스.
 *
 * @param <T> {@link ChestBlockEntity}를 상속받는 타입
 */
public interface VPChestBlockEntity<T extends ChestBlockEntity> extends VPMixin<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends ChestBlockEntity> VPChestBlockEntity<T> cast(@NonNull T object) {
        return (VPChestBlockEntity<T>) object;
    }

    /**
     * 상자를 열린 상태로 표시한다.
     */
    void openLid();
}
