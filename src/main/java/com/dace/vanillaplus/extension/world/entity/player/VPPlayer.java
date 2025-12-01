package com.dace.vanillaplus.extension.world.entity.player;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import lombok.NonNull;
import net.minecraft.world.entity.player.Player;

/**
 * {@link Player}를 확장하는 인터페이스.
 *
 * @param <T> {@link Player}를 상속받는 타입
 */
public interface VPPlayer<T extends Player> extends VPEntity<T, EntityModifier.LivingEntityModifier> {
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
