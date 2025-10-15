package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link Entity}를 확장하는 인터페이스.
 *
 * @param <T> {@link Entity}를 상속받는 타입
 * @param <U> {@link EntityModifier}를 상속받는 엔티티 수정자
 * @see EntityModifier
 */
public interface VPEntity<T extends Entity, U extends EntityModifier> extends VPMixin<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Entity, U extends EntityModifier> VPEntity<T, U> cast(@NonNull T object) {
        return (VPEntity<T, U>) object;
    }

    /**
     * @param dataModifier 엔티티 수정자
     */
    void setDataModifier(@Nullable U dataModifier);
}
