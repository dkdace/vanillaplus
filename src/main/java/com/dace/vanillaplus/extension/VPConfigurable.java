package com.dace.vanillaplus.extension;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * 수정 가능한 요소를 나타내는 인터페이스.
 *
 * @param <T> 수정 대상 데이터 타입
 * @param <U> 데이터 설정
 */
public interface VPConfigurable<T, U> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T, U> VPConfigurable<T, U> cast(@NonNull T object) {
        return (VPConfigurable<T, U>) object;
    }

    /**
     * @return 데이터 설정
     */
    @NonNull
    U getConfig();

    /**
     * @param config 데이터 설정
     */
    void setConfig(@Nullable U config);
}
