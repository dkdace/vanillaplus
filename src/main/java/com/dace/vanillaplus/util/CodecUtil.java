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

    /**
     * 초 단위의 시간 코덱을 틱 단위로 변환한다.
     *
     * @param codec 초 단위 시간 코덱
     * @return {@link Codec}
     */
    @NonNull
    public static Codec<Integer> secondsToTicks(@NonNull Codec<Float> codec) {
        return codec.xmap(to -> (int) (to * 20.0), from -> from / 20F);
    }
}
