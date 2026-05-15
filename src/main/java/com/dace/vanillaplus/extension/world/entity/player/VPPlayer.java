package com.dace.vanillaplus.extension.world.entity.player;

import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import lombok.NonNull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgePlayer;

/**
 * {@link Player}를 확장하는 인터페이스.
 *
 * @param <T> {@link Player}를 상속받는 타입
 */
public interface VPPlayer<T extends Player> extends VPLivingEntity<T>, IForgePlayer {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Player> VPPlayer<T> cast(@NonNull T object) {
        return (VPPlayer<T>) object;
    }

    @Override
    default LivingEntity self() {
        return VPLivingEntity.super.self();
    }

    /**
     * 엎드리기 키 입력 여부를 지정한다.
     *
     * @param isProneKeyDown 키 입력 여부
     */
    void setProneKeyDown(boolean isProneKeyDown);
}
