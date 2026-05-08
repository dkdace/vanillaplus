package com.dace.vanillaplus.extension.world.entity;

import com.dace.vanillaplus.world.entity.modifier.MobModifier;
import lombok.NonNull;
import net.minecraft.world.entity.Mob;

/**
 * {@link Mob}를 확장하는 인터페이스.
 *
 * @param <T> {@link Mob}를 상속받는 타입
 * @param <U> {@link MobModifier}를 상속받는 엔티티 수정자
 * @see MobModifier
 */
public interface VPMob<T extends Mob, U extends MobModifier> extends VPLivingEntity<T, U> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Mob, U extends MobModifier> VPMob<T, U> cast(@NonNull T object) {
        return (VPMob<T, U>) object;
    }

    @Override
    @NonNull
    MobModifier getDefaultDataModifier();
}
