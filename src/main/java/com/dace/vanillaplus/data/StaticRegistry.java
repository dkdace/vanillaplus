package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.google.common.reflect.ClassPath;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Supplier;

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

    /** 블록 수정자 타입 */
    public static final StaticRegistry<MapCodec<? extends BlockModifier>> BLOCK_MODIFIER_TYPE = new StaticRegistry<>("modifier/block/type");
    /** 엔티티 수정자 타입 */
    public static final StaticRegistry<MapCodec<? extends EntityModifier>> ENTITY_MODIFIER_TYPE = new StaticRegistry<>("modifier/entity/type");
    /** 엔티티 수정자 인터페이스 */
    public static final StaticRegistry<DataComponentType<?>> ENTITY_MODIFIER_INTERFACE = new StaticRegistry<>("modifier/entity/interface");
    /** 아이템 수정자 타입 */
    public static final StaticRegistry<MapCodec<? extends ItemModifier>> ITEM_MODIFIER_TYPE = new StaticRegistry<>("modifier/item/type");
    /** 습격자 효과 타입 */
    public static final StaticRegistry<MapCodec<? extends RaiderEffect>> RAIDER_EFFECT_TYPE = new StaticRegistry<>("raider_effect/type");

    /** {@link com.dace.vanillaplus.data.registryobject} 패키지 */
    private static final String PACKAGE = "com.dace.vanillaplus.data.registryobject";

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

    public static void bootstrap(@NonNull BusGroup busGroup) {
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses(PACKAGE)) {
                Class.forName(classInfo.getName());
                LOGGER.debug("Loaded {}", classInfo);
            }
        } catch (ClassNotFoundException | IOException ex) {
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
