package com.dace.vanillaplus.data;

import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentMap;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 모드에서 사용하는 데이터 요소 맵 클래스.
 *
 * <p>{@link DataComponentMap}과 유사한 기능이지만 모드 데이터 팩에서만 사용한다.</p>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class VPDataComponentMap {
    /** 기본값 */
    public static final VPDataComponentMap EMPTY = new VPDataComponentMap(Collections.emptyMap());
    /** 데이터 요소 목록 */
    private final Map<Codec<?>, Object> map;

    /**
     * 데이터 요소 맵의 코덱을 생성하여 반환한다.
     *
     * @param staticRegistry 정적 레지스트리
     * @return JSON 코덱
     */
    @NonNull
    public static Codec<VPDataComponentMap> createCodec(@NonNull StaticRegistry<Codec<?>> staticRegistry) {
        Codec<Map<Codec<?>, Object>> codec = Codec.dispatchedMap(staticRegistry.createCodec(), Function.identity());
        return codec.xmap(VPDataComponentMap::new, vpDataComponentMap -> vpDataComponentMap.map);
    }

    /**
     * 지정한 코덱에 해당하는 데이터 요소를 반환한다.
     *
     * @param registryObject 코덱 레지스트리 개체
     * @param <T>            데이터 타입
     * @return 데이터 요소
     * @see VPDataComponentMap#getOrDefault(RegistryObject, Object)
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(@NonNull RegistryObject<Codec<T>> registryObject) {
        return (Optional<T>) Optional.ofNullable(map.get(registryObject.get()));
    }

    /**
     * 지정한 코덱에 해당하는 데이터 요소를 반환한다.
     *
     * @param registryObject 코덱 레지스트리 개체
     * @param defaultValue   기본값
     * @param <T>            데이터 타입
     * @return 데이터 요소. 존재하지 않으면 {@code defaultValue} 반환
     * @see VPDataComponentMap#get(RegistryObject)
     */
    @NonNull
    public <T> T getOrDefault(@NonNull RegistryObject<Codec<T>> registryObject, T defaultValue) {
        return get(registryObject).orElse(defaultValue);
    }

    /**
     * 지정한 코덱에 해당하는 boolean 데이터 요소를 반환한다.
     *
     * @param registryObject 코덱 레지스트리 개체
     * @return boolean 데이터 요소. 존재하지 않으면 {@code false} 반환
     * @see VPDataComponentMap#get(RegistryObject)
     */
    public boolean getBoolean(@NonNull RegistryObject<Codec<Boolean>> registryObject) {
        return getOrDefault(registryObject, false);
    }
}
