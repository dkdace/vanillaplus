package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
    /** 식별자 매핑에 사용할 작업 */
    private final Function<T, Identifier> mappingFunction;

    private ReloadableDataManager(@NonNull HolderLookup.Provider registries, @NonNull VPRegistry<U> vpRegistry, @NonNull Codec<U> codec,
                                  @NonNull Function<T, Identifier> mappingFunction) {
        super(registries, codec, vpRegistry.getRegistryKey());
        this.mappingFunction = mappingFunction;
    }

    /**
     * 리소스 키를 통해 데이터를 가져올 수 있는 데이터 매니저를 생성한다.
     *
     * @param registries 레지스트리 목록
     * @param vpRegistry 레지스트리 정보
     * @param codec      JSON 코덱
     * @param <T>        데이터를 가져올 때 사용할 키의 타입
     * @param <U>        모드 데이터의 타입
     * @return {@link ReloadableDataManager}
     */
    @NonNull
    public static <T, U> ReloadableDataManager<ResourceKey<T>, U> fromVPRegistry(@NonNull HolderLookup.Provider registries,
                                                                                 @NonNull VPRegistry<U> vpRegistry, @NonNull Codec<U> codec) {
        return new ReloadableDataManager<>(registries, vpRegistry, codec,
                resourceKey -> VanillaPlus.createIdentifier(resourceKey.identifier().getPath()));
    }

    @Override
    protected void apply(Map<Identifier, U> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        dataMap.putAll(map);
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
}
