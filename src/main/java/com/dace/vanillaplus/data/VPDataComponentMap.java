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

/**
 * 모드에서 사용하는 데이터 요소 맵 클래스.
 *
 * <p>{@link DataComponentMap}과 유사한 기능이지만 기본값을 제공하며, 모드 데이터 팩에서만 사용한다.</p>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class VPDataComponentMap {
    /** 기본값 */
    public static final VPDataComponentMap EMPTY = new VPDataComponentMap(Collections.emptyMap());
    /** 데이터 요소 목록 */
    private final Map<Key<?>, Object> map;

    /**
     * 데이터 요소 맵의 코덱을 생성하여 반환한다.
     *
     * @param staticRegistry 정적 레지스트리
     * @return JSON 코덱
     */
    @NonNull
    public static Codec<VPDataComponentMap> createCodec(@NonNull StaticRegistry<Key<?>> staticRegistry) {
        Codec<Map<Key<?>, Object>> codec = Codec.dispatchedMap(staticRegistry.createCodec(), Key::codec);
        return codec.xmap(VPDataComponentMap::new, vpDataComponentMap -> vpDataComponentMap.map);
    }

    /**
     * 지정한 타입에 해당하는 데이터 요소를 반환한다.
     *
     * @param registryObject 타입 레지스트리 개체
     * @param <T>            데이터 타입
     * @return 데이터 요소
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull RegistryObject<Key<T>> registryObject) {
        Key<T> key = registryObject.get();
        return (T) map.getOrDefault(key, key.defaultValue);
    }

    /**
     * 데이터 요소의 타입 (키) 클래스.
     *
     * @param id           ID
     * @param codec        JSON 코덱
     * @param defaultValue 기본값
     * @param <T>          데이터 타입
     */
    public record Key<T>(int id, @NonNull Codec<T> codec, @NonNull T defaultValue) {
    }
}
