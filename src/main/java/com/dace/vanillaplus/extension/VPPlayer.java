package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.world.entity.player.Player;

/**
 * {@link Player}를 확장하는 인터페이스.
 */
public interface VPPlayer {
    /**
     * 엎드리기 키 입력 여부를 지정한다.
     *
     * @param player         대상 플레이어
     * @param isProneKeyDown 키 입력 여부
     */
    static void setProneKeyDown(@NonNull Player player, boolean isProneKeyDown) {
        ((VPPlayer) player).setProneKeyDown(isProneKeyDown);
    }

    void setProneKeyDown(boolean isProneKeyDown);
}
