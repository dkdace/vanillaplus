package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.world.entity.player.Player;

/**
 * {@link Player}를 확장하는 인터페이스.
 *
 * @param <T> {@link Player}를 상속받는 타입
 */
public interface VPPlayer<T extends Player> extends VPMixin<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Player> VPPlayer<T> cast(@NonNull T object) {
        return (VPPlayer<T>) object;
    }

    /**
     * 엎드리기 키 입력 여부를 지정한다.
     *
     * @param isProneKeyDown 키 입력 여부
     */
    void setProneKeyDown(boolean isProneKeyDown);
}
