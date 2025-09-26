package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.rebalance.modifier.DataModifier;
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
    /**
     * 데이터 수정자를 반환한다.
     *
     * @param element 대상 요소
     * @param <T>     수정 대상 데이터 타입
     * @param <U>     {@link DataModifier}를 상속받는 데이터 수정자
     * @return 데이터 수정자
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <T, U extends DataModifier<T>> U getDataModifier(@NonNull T element) {
        return ((VPModifiableData<T, U>) element).getDataModifier();
    }

    /**
     * 데이터 수정자를 지정한다.
     *
     * @param element      대상 요소
     * @param dataModifier 데이터 수정자
     * @param <T>          수정 대상 데이터 타입
     * @param <U>          {@link DataModifier}를 상속받는 데이터 수정자
     */
    @SuppressWarnings("unchecked")
    static <T, U extends DataModifier<T>> void setDataModifier(@NonNull T element, @NonNull U dataModifier) {
        ((VPModifiableData<T, U>) element).setDataModifier(dataModifier);
    }

    @Nullable
    U getDataModifier();

    void setDataModifier(@NonNull U dataModifier);
}
