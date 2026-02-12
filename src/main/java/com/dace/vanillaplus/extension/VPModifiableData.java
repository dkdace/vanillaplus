package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.modifier.DataModifier;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
     * 지정한 수정 가능한 요소의 데이터 수정자를 하위 클래스로 캐스팅하여 반환한다.
     *
     * @param object    대상 인스턴스
     * @param castClass 캐스팅 대상 클래스
     * @param <T>       수정 대상 데이터 타입
     * @param <U>       {@link DataModifier}를 상속받는 데이터 수정자
     * @return 데이터 수정자
     */
    @NonNull
    static <T, U extends DataModifier<T>> Optional<U> getDataModifier(@NonNull T object, @NonNull Class<U> castClass) {
        return cast(object).getDataModifier()
                .map(dataModifier -> castClass.isInstance(dataModifier) ? castClass.cast(dataModifier) : null);
    }

    /**
     * @return 데이터 수정자
     */
    @NonNull
    Optional<U> getDataModifier();

    /**
     * @param dataModifier 데이터 수정자
     */
    void setDataModifier(@Nullable U dataModifier);
}
