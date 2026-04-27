package com.dace.vanillaplus.util;

import com.dace.vanillaplus.data.StaticRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.Optional;
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
     * @param staticRegistry 정적 레지스트리
     * @param <T>            {@link CodecComponent}를 상속받는 하위 코덱 요소
     * @return {@link Codec}
     */
    @NonNull
    public static <T extends CodecComponent<T>> Codec<T> fromCodecRegistry(@NonNull StaticRegistry<MapCodec<? extends T>> staticRegistry) {
        return staticRegistry.createCodec().dispatch(CodecComponent::getCodec, Function.identity());
    }

    /**
     * 지정한 코덱에 {@link Optional}을 적용하여 반환한다.
     *
     * @param codec 코덱
     * @param <T>   값의 타입
     * @return {@link Codec}
     */
    @NonNull
    public static <T> Codec<Optional<T>> optional(@NonNull Codec<T> codec) {
        return Codec.xor(codec, Codec.EMPTY.codec())
                .xmap(to -> to.map(Optional::of, _ -> Optional.empty()),
                        from -> from.map(Either::<T, Unit>left).orElse(Either.right(Unit.INSTANCE)));
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
