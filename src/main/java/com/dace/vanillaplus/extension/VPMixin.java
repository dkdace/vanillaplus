package com.dace.vanillaplus.extension;

import lombok.NonNull;

/**
 * Mixin 확장 인터페이스.
 *
 * @param <T> Mixin 대상 타입
 */
public interface VPMixin<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
