package com.dace.vanillaplus.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;

import java.util.function.Function;

/**
 * 코덱 레지스트리의 요소를 관리하는 인터페이스.
 *
 * @param <T> {@link CodecComponent}를 상속받는 타입
 */
public interface CodecComponent<T extends CodecComponent<T>> {
    /**
     * 코덱 레지스트리의 타입 코덱을 생성하여 반환한다.
     *
     * @param staticRegistry 정적 레지스트리
     * @param <T>            {@link CodecComponent}를 상속받는 타입
     * @return {@link Codec}
     */
    @NonNull
    static <T extends CodecComponent<T>> Codec<T> createCodec(@NonNull StaticRegistry<MapCodec<? extends T>> staticRegistry) {
        return staticRegistry.createCodec().dispatch(CodecComponent::getCodec, Function.identity());
    }

    /**
     * @return JSON 코덱
     */
    @NonNull
    MapCodec<? extends T> getCodec();
}
