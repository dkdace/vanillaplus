package com.dace.vanillaplus.util;

import com.dace.vanillaplus.VPRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.function.Function;

/**
 * {@link Codec} 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class CodecUtil {
    /**
     * 열거형 코덱을 생성하여 반환한다.
     *
     * @param enumClass 열거형 클래스
     * @param <E>       열거형 타입
     * @return {@link Codec}
     */
    @NonNull
    public static <E extends Enum<E>> Codec<E> fromEnum(@NonNull Class<E> enumClass) {
        return Codec.stringResolver(to -> to.toString().toLowerCase(Locale.ROOT),
                from -> Enum.valueOf(enumClass, from.toUpperCase(Locale.ROOT)));
    }

    /**
     * 코덱 레지스트리의 타입 코덱을 생성하여 반환한다.
     *
     * @param vpRegistry 레지스트리 정보
     * @param <T>        {@link CodecComponent}를 상속받는 하위 코덱 요소
     * @return 레지스트리 코덱
     */
    @NonNull
    public <T extends CodecComponent<T>> Codec<T> fromCodecRegistry(@NonNull VPRegistry<MapCodec<? extends T>> vpRegistry) {
        return Codec.lazyInitialized(() -> vpRegistry.createByNameCodec().dispatch(CodecComponent::getCodec, Function.identity()));
    }

    /**
     * 하위 코덱 요소를 관리하는 인터페이스.
     *
     * @param <T> {@link CodecComponent}를 상속받는 타입
     */
    public interface CodecComponent<T extends CodecComponent<T>> {
        /**
         * 세부 요소의 JSON 코덱을 반환한다.
         *
         * @return JSON 코덱
         */
        @NonNull
        MapCodec<? extends T> getCodec();
    }
}
