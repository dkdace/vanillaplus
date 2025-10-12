package com.dace.vanillaplus;

import com.dace.vanillaplus.data.EnchantmentValuePreset;
import com.dace.vanillaplus.data.LootTableReward;
import com.dace.vanillaplus.data.StructureMap;
import com.dace.vanillaplus.data.Trade;
import com.dace.vanillaplus.data.modifier.*;
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
import lombok.experimental.UtilityClass;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 모드에서 사용하는 레지스트리를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPRegistries {
    /** 효과음 이벤트 */
    public static final VPRegistry<SoundEvent> SOUND_EVENT = new VPRegistry<>(Registries.SOUND_EVENT);
    /** 엔티티 속성 */
    public static final VPRegistry<Attribute> ATTRIBUTE = new VPRegistry<>(Registries.ATTRIBUTE);
    /** 마법 부여의 레벨 기반 값 타입 */
    public static final VPRegistry<MapCodec<? extends LevelBasedValue>> ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = new VPRegistry<>(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE);
    /** 데이터 요소 타입 */
    public static final VPRegistry<DataComponentType<?>> DATA_COMPONENT_TYPE = new VPRegistry<>(Registries.DATA_COMPONENT_TYPE);

    /** 주민 거래 정보 */
    public static final VPRegistry<Trade> TRADE = new VPRegistry<>("trade");
    /** 구조물 지도 */
    public static final VPRegistry<StructureMap> STRUCTURE_MAP = new VPRegistry<>("structure_map");
    /** 노획물 테이블 보상 */
    public static final VPRegistry<LootTableReward> LOOT_TABLE_REWARD = new VPRegistry<>("loot_table_reward");
    /** 마법 부여 수치 프리셋 */
    public static final VPRegistry<EnchantmentValuePreset> ENCHANTMENT_VALUE_PRESET = new VPRegistry<>("enchantment_value_preset");
    /** 전역 수정자 */
    public static final VPRegistry<GeneralModifier> MODIFIER = new VPRegistry<>("modifier");
    /** 아이템 수정자 */
    public static final VPRegistry<ItemModifier> ITEM_MODIFIER = new VPRegistry<>("modifier/item");
    /** 블록 수정자 */
    public static final VPRegistry<BlockModifier> BLOCK_MODIFIER = new VPRegistry<>("modifier/block");
    /** 엔티티 수정자 */
    public static final VPRegistry<EntityModifier> ENTITY_MODIFIER = new VPRegistry<>("modifier/entity");

    @Nullable
    private static HolderLookup.Provider provider;

    static {
        ReflectionUtil.loadClass(VPTags.class);
        ReflectionUtil.loadClass(VPSoundEvents.class);
        ReflectionUtil.loadClass(VPAttributes.class);
        ReflectionUtil.loadClass(VPEnchantmentLevelBasedValueTypes.class);
        ReflectionUtil.loadClass(VPDataComponentTypes.class);
    }

    private static <T, U extends DataModifier<T>> void applyDataModifiers(@NonNull VPRegistries.VPRegistry<U> vpRegistry,
                                                                          @NonNull DefaultedRegistry<T> registry) {
        registry.forEach(element -> {
            ResourceKey<U> dataModifierResourceKey = vpRegistry.createResourceKey(registry.getKey(element).getPath());
            VPModifiableData.setDataModifier(element, VPRegistries.getValue(dataModifierResourceKey));
        });
    }

    @SubscribeEvent
    private static void onAddReloadListener(@NonNull AddReloadListenerEvent event) {
        provider = event.getRegistries();

        applyDataModifiers(VPRegistries.ITEM_MODIFIER, BuiltInRegistries.ITEM);
        applyDataModifiers(VPRegistries.BLOCK_MODIFIER, BuiltInRegistries.BLOCK);
        applyDataModifiers(VPRegistries.ENTITY_MODIFIER, BuiltInRegistries.ENTITY_TYPE);
    }

    /**
     * 지정한 리소스 키의 값을 반환한다.
     *
     * @param resourceKey 리소스 키
     * @param <T>         데이터 타입
     * @return 리소스 키의 값
     * @throws IllegalStateException 레지스트리에 접근할 수 없으면 발생
     */
    @Nullable
    public static <T> T getValue(@NonNull ResourceKey<T> resourceKey) {
        Validate.validState(provider != null, "레지스트리에 접근할 수 없음");

        Holder.Reference<T> reference = provider.get(resourceKey).orElse(null);
        return reference == null ? null : reference.value();
    }

    /**
     * 지정한 리소스 키의 값을 반환한다.
     *
     * @param resourceKey 리소스 키
     * @param <T>         데이터 타입
     * @return 리소스 키의 값
     * @throws IllegalStateException 레지스트리에 접근할 수 없거나 값이 존재하지 않으면 발생
     */
    @NonNull
    public static <T> T getValueOrThrow(@NonNull ResourceKey<T> resourceKey) {
        Validate.validState(provider != null, "레지스트리에 접근할 수 없음");
        return provider.getOrThrow(resourceKey).value();
    }

    /**
     * 레지스트리 정보를 관리하는 클래스.
     *
     * @param <T> 레지스트리 데이터 타입
     */
    public static final class VPRegistry<T> {
        /** 레지스트리 리소스 키 */
        @NonNull
        @Getter
        private final ResourceKey<Registry<T>> registryKey;
        /** DeferredRegister 인스턴스 */
        private final DeferredRegister<T> deferredRegister;

        private VPRegistry(@NonNull ResourceKey<Registry<T>> registryKey) {
            this.registryKey = registryKey;
            this.deferredRegister = DeferredRegister.create(registryKey, VanillaPlus.MODID);

            VanillaPlus.getInstance().registerBusGroup(deferredRegister);
        }

        private VPRegistry(@NonNull String name) {
            this(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name)));
            deferredRegister.makeRegistry(RegistryBuilder::of);
        }

        /**
         * 레지스트리의 하위 요소 리소스 키를 생성한다.
         *
         * @param name 이름
         * @return 리소스 키
         */
        @NonNull
        public ResourceKey<T> createResourceKey(@NonNull String name) {
            return ResourceKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        }

        /**
         * 레지스트리 코덱을 생성하여 반환한다.
         *
         * @return 레지스트리 코덱
         */
        @NonNull
        public Codec<Holder<T>> createRegistryCodec() {
            return RegistryFixedCodec.create(registryKey);
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
    }
}
