package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.modifier.DataModifier;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * 수정 가능한 요소를 나타내는 인터페이스.
 *
 * @param <T> 수정 대상 데이터 타입
 * @param <U> {@link DataModifier}를 상속받는 데이터 수정자
 * @see DataModifier
 */
public interface VPModifiableData<T, U extends DataModifier<T>> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T, U extends DataModifier<T>> VPModifiableData<T, U> cast(@NonNull T object) {
        return (VPModifiableData<T, U>) object;
    }

    /**
     * @param dataModifier 데이터 수정자
     */
    void setDataModifier(@Nullable U dataModifier);
}
