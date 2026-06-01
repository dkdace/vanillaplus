package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.util.ReflectionUtil;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 모드의 정적 레지스트리를 관리하는 클래스.
 *
 * <p>정적 레지스트리는 코드로 직접 데이터를 등록한다.</p>
 *
 * @see com.dace.vanillaplus.data.registryobject
 */
public final class StaticRegistry<T> {
    /** 로거 인스턴스 */
    private static final Logger LOGGER = LogUtils.getLogger();
    /** 생성된 레지스트리 목록 */
    private static final HashSet<DeferredRegister<?>> REGISTRIES = new HashSet<>();

    /** 블록 설정 데이터 요소 타입 */
    public static final StaticRegistry<Codec<?>> BLOCK_CONFIG_COMPONENT_TYPE = new StaticRegistry<>("block_config/component_type");
    /** 엔티티 설정 데이터 요소 타입 */
    public static final StaticRegistry<Codec<?>> ENTITY_CONFIG_COMPONENT_TYPE = new StaticRegistry<>("entity_config/component_type");
    /** 아이템 설정 데이터 요소 타입 */
    public static final StaticRegistry<Codec<?>> ITEM_CONFIG_COMPONENT_TYPE = new StaticRegistry<>("item_config/component_type");

    /** {@link com.dace.vanillaplus.data.registryobject} 패키지 경로 */
    private static final UnaryOperator<Path> PACKAGE_PATH = path -> path.resolve("com", "dace", "vanillaplus", "data", "registryobject");

    /** DeferredRegister 인스턴스 */
    private final DeferredRegister<T> deferredRegister;
    /** 레지스트리 홀더 인스턴스 */
    private final DeferredRegister.RegistryHolder<T> registryHolder;

    /**
     * 정적 레지스트리 인스턴스를 생성한다.
     *
     * @param name 이름
     */
    private StaticRegistry(@NonNull String name) {
        this.deferredRegister = createDeferredRegister(ResourceKey.createRegistryKey(IdentifierUtil.fromPath(name)));
        this.registryHolder = deferredRegister.makeRegistry(RegistryBuilder::of);
    }

    public static void bootstrap(@NonNull Path rootPath, @NonNull BusGroup busGroup) {
        try {
            ReflectionUtil.loadClassesFromPackage(PACKAGE_PATH.apply(rootPath), clazz -> LOGGER.debug("Loaded {}", clazz));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot initialize StaticRegistry", ex);
        }

        REGISTRIES.forEach(deferredRegister -> deferredRegister.register(busGroup));

        LOGGER.info("Initialized");
    }

    /**
     * DeferredRegister 인스턴스를 생성하여 반환한다.
     *
     * @param registryKey 레지스트리 리소스 키
     * @param <T>         레지스트리 데이터 타입
     * @return {@link DeferredRegister}
     */
    @NonNull
    public static <T> DeferredRegister<T> createDeferredRegister(@NonNull ResourceKey<Registry<T>> registryKey) {
        DeferredRegister<T> deferredRegister = DeferredRegister.create(registryKey, VanillaPlus.MODID);
        REGISTRIES.add(deferredRegister);

        return deferredRegister;
    }

    /**
     * 레지스트리 홀더 인스턴스를 반환한다.
     *
     * @return {@link DeferredRegister.RegistryHolder}
     */
    @NonNull
    public DeferredRegister.RegistryHolder<T> get() {
        return registryHolder;
    }

    /**
     * @see DeferredRegister#register(String, Supplier)
     */
    public <U extends T> RegistryObject<U> register(@NonNull String name, @NonNull Supplier<U> factory) {
        return deferredRegister.register(name, factory);
    }

    /**
     * 레지스트리의 이름 코덱을 생성하여 반환한다.
     *
     * @return {@link Codec}
     */
    @NonNull
    public Codec<T> createCodec() {
        return Codec.lazyInitialized(() -> registryHolder.get().getCodec());
    }
}
