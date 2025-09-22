package com.dace.vanillaplus.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Locale;

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
     * 세부 요소 코덱을 생성하여 반환한다.
     *
     * @param enumClass 세부 요소를 관리하는 열거형 클래스
     * @param <U>       {@link CodecComponentType}을 상속받는 열거형 타입
     * @param <T>       {@link CodecComponent}을 상속받는 타입
     * @return {@link Codec}
     */
    @NonNull
    public static <T extends CodecComponent<T, U>, U extends Enum<U> & CodecComponentType<T, U>> Codec<T> fromCodecComponent(@NonNull Class<U> enumClass) {
        return fromEnum(enumClass).dispatch(CodecComponent::getType, CodecComponentType::getCodec);
    }

    /**
     * 코덱의 세부 요소 유형을 관리하는 인터페이스.
     *
     * @param <T> {@link CodecComponent}을 상속받는 타입
     * @param <U> {@link CodecComponentType}을 상속받는 열거형 타입
     */
    public interface CodecComponentType<T extends CodecComponent<T, U>, U extends Enum<U> & CodecComponentType<T, U>> {
        /**
         * 세부 요소의 JSON 코덱을 반환한다.
         *
         * @return JSON 코덱
         */
        @NonNull
        MapCodec<? extends T> getCodec();
    }

    /**
     * 코덱의 세부 요소를 나타내는 인터페이스.
     *
     * @param <T> {@link CodecComponent}을 상속받는 타입
     * @param <U> {@link CodecComponentType}을 상속받는 열거형 타입
     */
    public interface CodecComponent<T extends CodecComponent<T, U>, U extends Enum<U> & CodecComponentType<T, U>> {
        /**
         * @return 세부 요소 유형
         */
        @NonNull
        U getType();
    }
}
