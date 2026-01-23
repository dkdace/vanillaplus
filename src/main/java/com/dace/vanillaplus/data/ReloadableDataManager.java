package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 모드의 새로 고침 가능한 서버 데이터를 관리하는 데이터 매니저 클래스.
 *
 * @param <T> 데이터를 가져올 때 사용할 키의 타입
 * @param <U> 모드 데이터의 타입
 */
public final class ReloadableDataManager<T, U> extends SimpleJsonResourceReloadListener<U> {
    /** 식별자별 데이터 목록 (식별자 : 데이터) */
    private final Map<Identifier, U> dataMap = new HashMap<>();
    /** 레지스트리 정보 */
    private final VPRegistry<U> vpRegistry;
    /** 식별자 매핑에 사용할 작업 */
    private final Function<T, Identifier> mappingFunction;

    /**
     * 데이터 매니저 인스턴스를 생성한다.
     *
     * @param registries      레지스트리 목록
     * @param vpRegistry      레지스트리 정보
     * @param codec           JSON 코덱
     * @param mappingFunction 식별자 매핑에 사용할 작업
     */
    public ReloadableDataManager(@NonNull HolderLookup.Provider registries, @NonNull VPRegistry<U> vpRegistry, @NonNull Codec<U> codec,
                                 @NonNull Function<T, Identifier> mappingFunction) {
        super(registries, codec, vpRegistry.getRegistryKey());

        this.vpRegistry = vpRegistry;
        this.mappingFunction = mappingFunction;
    }

    @Override
    protected void apply(Map<Identifier, U> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        dataMap.putAll(map);
        VanillaPlus.LOGGER.info("Loaded {} data from {}", map.size(), vpRegistry.getRegistryKey().identifier());
    }

    /**
     * 지정한 키에 해당하는 데이터를 반환한다.
     *
     * @param key 데이터를 가져올 때 사용할 키
     * @return 키에 해당하는 데이터
     */
    @NonNull
    public Optional<U> get(@NonNull T key) {
        return Optional.ofNullable(dataMap.get(mappingFunction.apply(key)));
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
