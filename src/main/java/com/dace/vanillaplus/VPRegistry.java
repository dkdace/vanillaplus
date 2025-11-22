package com.dace.vanillaplus;

import com.dace.vanillaplus.data.*;
import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.data.modifier.DataModifier;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.registryobject.VPEnchantmentLevelBasedValueTypes;
import com.dace.vanillaplus.registryobject.VPSoundEvents;
import com.dace.vanillaplus.util.ReflectionUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 모드에서 사용하는 레지스트리 정보를 관리하는 클래스.
 *
 * @param <T> 레지스트리 데이터 타입
 */
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPRegistry<T> {
    /** 효과음 이벤트 */
    public static final VPRegistry<SoundEvent> SOUND_EVENT = new VPRegistry<>(BuiltInRegistries.SOUND_EVENT);
    /** 엔티티 속성 */
    public static final VPRegistry<Attribute> ATTRIBUTE = new VPRegistry<>(BuiltInRegistries.ATTRIBUTE);
    /** 마법 부여의 레벨 기반 값 타입 */
    public static final VPRegistry<MapCodec<? extends LevelBasedValue>> ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = new VPRegistry<>(BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE);
    /** 데이터 요소 타입 */
    public static final VPRegistry<DataComponentType<?>> DATA_COMPONENT_TYPE = new VPRegistry<>(BuiltInRegistries.DATA_COMPONENT_TYPE);

    /** 설정 */
    public static final VPRegistry<GeneralConfig> CONFIG = new VPRegistry<>("config");
    /** 주민 거래 정보 */
    public static final VPRegistry<Trade> TRADE = new VPRegistry<>("trade");
    /** 구조물 지도 */
    public static final VPRegistry<StructureMap> STRUCTURE_MAP = new VPRegistry<>("structure_map");
    /** 노획물 테이블 보상 */
    public static final VPRegistry<LootTableReward> LOOT_TABLE_REWARD = new VPRegistry<>("loot_table_reward");
    /** 마법 부여 확장 */
    public static final VPRegistry<EnchantmentExtension> ENCHANTMENT_EXTENSION = new VPRegistry<>("enchantment_extension");
    /** 습격 웨이브 정보 */
    public static final VPRegistry<RaidWave> RAID_WAVE = new VPRegistry<>("raid_wave");
    /** 습격자 효과 */
    public static final VPRegistry<RaiderEffect> RAIDER_EFFECT = new VPRegistry<>("raider_effect");
    /** 아이템 수정자 */
    public static final VPRegistry<ItemModifier> ITEM_MODIFIER = new VPRegistry<>("modifier/item");
    /** 블록 수정자 */
    public static final VPRegistry<BlockModifier> BLOCK_MODIFIER = new VPRegistry<>("modifier/block");
    /** 엔티티 수정자 */
    public static final VPRegistry<EntityModifier> ENTITY_MODIFIER = new VPRegistry<>("modifier/entity");

    @Nullable
    private static HolderLookup.Provider provider;

    static {
        ReflectionUtil.loadClass(VPSoundEvents.class);
        ReflectionUtil.loadClass(VPAttributes.class);
        ReflectionUtil.loadClass(VPEnchantmentLevelBasedValueTypes.class);
        ReflectionUtil.loadClass(VPDataComponentTypes.class);
    }

    /** 레지스트리 리소스 키 */
    @NonNull
    @Getter
    private final ResourceKey<Registry<T>> registryKey;
    /** DeferredRegister 인스턴스 */
    private final DeferredRegister<T> deferredRegister;

    /** 바닐라 레지스트리 인스턴스 */
    @Nullable
    private Registry<T> vanillaRegistry;
    /** Forge 레지스트리 홀더 인스턴스 */
    @Nullable
    private DeferredRegister.RegistryHolder<T> forgeRegistryHolder;

    private VPRegistry(@NonNull ResourceKey<Registry<T>> registryKey) {
        this.registryKey = registryKey;
        this.deferredRegister = DeferredRegister.create(registryKey, VanillaPlus.MODID);

        VanillaPlus.getInstance().registerBusGroup(deferredRegister);
    }

    /**
     * 바닐라 레지스트리를 생성한다.
     *
     * @param registry 레지스트리 인스턴스
     */
    private VPRegistry(@NonNull Registry<T> registry) {
        this(ResourceKey.createRegistryKey(registry.key().location()));
        this.vanillaRegistry = registry;
    }

    /**
     * Forge 레지스트리를 생성한다.
     *
     * @param name 이름
     */
    private VPRegistry(@NonNull String name) {
        this(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name)));
        this.forgeRegistryHolder = deferredRegister.makeRegistry(RegistryBuilder::of);
    }

    @SubscribeEvent
    private static void onAddReloadListener(@NonNull AddReloadListenerEvent event) {
        initData(event::getRegistries);
        VanillaPlus.LOGGER.debug("Server-side Data Loaded");
    }

    @SubscribeEvent
    private static void onClientPlayerNetworkLoggingIn(@NonNull ClientPlayerNetworkEvent.LoggingIn event) {
        if (provider != null)
            return;

        initData(event.getPlayer()::registryAccess);
        VanillaPlus.LOGGER.debug("Client-side Data Loaded");
    }

    private static void initData(@NonNull Supplier<HolderLookup.Provider> providerFunction) {
        provider = providerFunction.get();

        applyDataModifiers(ItemModifier::fromItem, BuiltInRegistries.ITEM);
        applyDataModifiers(BlockModifier::fromBlock, BuiltInRegistries.BLOCK);
        applyDataModifiers(EntityModifier::fromEntityType, BuiltInRegistries.ENTITY_TYPE);
    }

    private static <T, U extends DataModifier<T>> void applyDataModifiers(@NonNull Function<T, U> dataModifierFunction,
                                                                          @NonNull DefaultedRegistry<T> registry) {
        registry.forEach(element -> VPModifiableData.cast(element).setDataModifier(dataModifierFunction.apply(element)));
    }

    /**
     * 지정한 레지스트리에 새로운 개체를 등록한다.
     *
     * @param vpRegistry     레지스트리 정보
     * @param name           이름
     * @param objectFunction 등록할 개체 반환에 실행할 작업
     * @param <T>            레지스트리 데이터 타입
     * @return 등록된 개체 인스턴스
     */
    @NonNull
    public static <T> RegistryObject<T> register(@NonNull VPRegistry<? super T> vpRegistry, @NonNull String name, Supplier<T> objectFunction) {
        return vpRegistry.deferredRegister.register(name, objectFunction);
    }

    /**
     * 레지스트리의 하위 레지스트리를 생성한다.
     *
     * @param name 이름
     * @param <U>  레지스트리 데이터 타입
     * @return 하위 레지스트리
     */
    @NonNull
    public <U> VPRegistry<U> createRegistry(@NonNull String name) {
        return new VPRegistry<>(registryKey.location().getPath() + "/" + name);
    }

    /**
     * 레지스트리의 홀더 코덱을 생성하여 반환한다.
     *
     * @return 홀더 코덱
     */
    @NonNull
    public Codec<Holder<T>> createRegistryCodec() {
        return RegistryFixedCodec.create(registryKey);
    }

    /**
     * 레지스트리의 하위 요소 코덱을 생성하여 반환한다.
     *
     * @return 하위 요소 코덱
     */
    @NonNull
    public Codec<T> createByNameCodec() {
        Validate.validState(vanillaRegistry != null || forgeRegistryHolder != null);

        Supplier<Codec<T>> onCodec = vanillaRegistry != null ? vanillaRegistry::byNameCodec : () -> forgeRegistryHolder.get().getCodec();
        return Codec.lazyInitialized(onCodec);
    }

    /**
     * 레지스트리에 새로운 개체를 등록한다.
     *
     * @param name           이름
     * @param objectFunction 등록할 개체 반환에 실행할 작업
     * @return 등록된 개체 인스턴스
     */
    @NonNull
    public RegistryObject<T> register(@NonNull String name, Supplier<T> objectFunction) {
        return deferredRegister.register(name, objectFunction);
    }

    private <U> U getResource(@NonNull String name, @NonNull BiFunction<HolderLookup.RegistryLookup<T>, ResourceKey<T>, U> resourceFunction) {
        Validate.validState(provider != null, "레지스트리에 접근할 수 없음");

        ResourceKey<T> resourceKey = ResourceKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        return resourceFunction.apply(provider.lookupOrThrow(registryKey), resourceKey);
    }

    /**
     * 지정한 리소스 이름에 해당하는 값을 반환한다.
     *
     * @param name 리소스 이름
     * @return 리소스 이름에 해당하는 값
     * @throws IllegalStateException 레지스트리에 접근할 수 없으면 발생
     */
    @Nullable
    public T getValue(@NonNull String name) {
        return getResource(name, HolderGetter::get).map(Holder.Reference::value).orElse(null);
    }

    /**
     * 지정한 리소스 이름에 해당하는 값을 반환한다.
     *
     * @param name 리소스 이름
     * @return 리소스 이름에 해당하는 값
     * @throws IllegalStateException 레지스트리에 접근할 수 없거나 값이 존재하지 않으면 발생
     */
    @NonNull
    public T getValueOrThrow(@NonNull String name) {
        return getResource(name, HolderGetter::getOrThrow).value();
    }
}
