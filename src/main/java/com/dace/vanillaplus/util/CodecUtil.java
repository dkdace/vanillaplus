package com.dace.vanillaplus.util;

import com.mojang.serialization.Codec;
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
}
