package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link ResourceKey}를 통해 모드 데이터 팩의 데이터를 가져오는 클래스.
 *
 * @param <T> 데이터를 가져올 때 사용할 키의 타입
 * @param <U> 모드 데이터의 타입
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataGetter<T, U> {
    /** 입력 키별 리소스 키 목록 (입력 키 : 리소스 키) */
    private final HashMap<T, ResourceKey<U>> resourceKeyMap = new HashMap<>();
    /** 리소스 키 매핑에 사용할 작업 */
    private final Function<T, ResourceKey<U>> resourceKeyMapper;

    /**
     * {@link T}를 통해 데이터를 가져올 수 있는 DataGetter를 생성한다.
     *
     * @param resourceKeyMapper 리소스 키 매핑에 사용할 작업
     * @param <T>               데이터를 가져올 때 사용할 키의 타입
     * @param <U>               모드 데이터의 타입
     * @return {@link DataGetter}
     */
    @NonNull
    public static <T, U> DataGetter<T, U> fromMapper(@NonNull Function<T, ResourceKey<U>> resourceKeyMapper) {
        return new DataGetter<>(resourceKeyMapper);
    }

    /**
     * 리소스 키를 통해 데이터를 가져올 수 있는 DataGetter를 생성한다.
     *
     * @param vpRegistry 레지스트리 정보
     * @param <T>        데이터를 가져올 때 사용할 키의 타입
     * @param <U>        모드 데이터의 타입
     * @return {@link DataGetter}
     */
    @NonNull
    public static <T, U> DataGetter<ResourceKey<T>, U> fromVPRegistry(@NonNull VPRegistry<U> vpRegistry) {
        return new DataGetter<>(resourceKey -> vpRegistry.createResourceKey(resourceKey.identifier().getPath()));
    }

    /**
     * 원본 레지스트리를 통해 데이터를 가져올 수 있는 DataGetter를 생성한다.
     *
     * @param registry   원본 레지스트리
     * @param vpRegistry 레지스트리 정보
     * @param <T>        데이터를 가져올 때 사용할 키의 타입
     * @param <U>        모드 데이터의 타입
     * @return {@link DataGetter}
     */
    @NonNull
    public static <T, U> DataGetter<T, U> fromDirectRegistry(@NonNull Registry<T> registry, @NonNull VPRegistry<U> vpRegistry) {
        return new DataGetter<>(key -> registry.getResourceKey(key)
                .map(resourceKey -> vpRegistry.createResourceKey(resourceKey.identifier().getPath()))
                .orElse(null));
    }

    /**
     * 지정한 키에 해당하는 데이터를 반환한다.
     *
     * @param key 데이터를 가져올 때 사용할 키
     * @return 키에 해당하는 데이터
     */
    @NonNull
    public Optional<U> get(@NonNull T key) {
        return VPRegistry.getRegistries().get(resourceKeyMap.computeIfAbsent(key, resourceKeyMapper)).map(Holder::value);
    }

    /**
     * 지정한 키에 해당하는 데이터를 하위 클래스로 캐스팅하여 반환한다.
     *
     * @param <V>       모드 데이터를 상속받는 하위 클래스의 타입
     * @param castClass 캐스팅 대상 클래스
     * @param key       데이터를 가져올 때 사용할 키
     * @return 키에 해당하는 데이터. {@code castClass}로 캐스팅하여 반환
     */
    @NonNull
    public <V extends U> Optional<V> get(@NonNull T key, @NonNull Class<V> castClass) {
        return get(key).map(value -> castClass.isInstance(value) ? castClass.cast(value) : null);
    }
}
