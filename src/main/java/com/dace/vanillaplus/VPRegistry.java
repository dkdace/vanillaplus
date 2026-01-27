package com.dace.vanillaplus;

import com.dace.vanillaplus.data.*;
import com.dace.vanillaplus.data.modifier.*;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.*;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

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
    /** 제작법 타입 */
    public static final VPRegistry<RecipeType<?>> RECIPE_TYPE = new VPRegistry<>(BuiltInRegistries.RECIPE_TYPE);
    /** 제작법 직렬화 처리기 */
    public static final VPRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = new VPRegistry<>(BuiltInRegistries.RECIPE_SERIALIZER);
    /** 제작법 책 분류 */
    public static final VPRegistry<RecipeBookCategory> RECIPE_BOOK_CATEGORY = new VPRegistry<>(BuiltInRegistries.RECIPE_BOOK_CATEGORY);
    /** 제작법 디스플레이 */
    public static final VPRegistry<RecipeDisplay.Type<?>> RECIPE_DISPLAY = new VPRegistry<>(BuiltInRegistries.RECIPE_DISPLAY);
    /** 물약 */
    public static final VPRegistry<Potion> POTION = new VPRegistry<>(BuiltInRegistries.POTION);
    /** 블록 엔티티 타입 */
    public static final VPRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new VPRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    /** 마법 부여의 효과 데이터 요소 타입 */
    public static final VPRegistry<DataComponentType<?>> ENCHANTMENT_EFFECT_COMPONENT_TYPE = new VPRegistry<>(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
    /** 마법 부여의 위치 기반 효과 타입 */
    public static final VPRegistry<MapCodec<? extends EnchantmentLocationBasedEffect>> ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = new VPRegistry<>(BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE);
    /** 마법 부여의 엔티티 효과 타입 */
    public static final VPRegistry<MapCodec<? extends EnchantmentEntityEffect>> ENCHANTMENT_ENTITY_EFFECT_TYPE = new VPRegistry<>(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE);
    /** 게임 규칙 */
    public static final VPRegistry<GameRule<?>> GAME_RULE = new VPRegistry<>(BuiltInRegistries.GAME_RULE);

    /** 주민 거래 정보 */
    public static final VPRegistry<Trade> TRADE = new VPRegistry<>("trade");
    /** 구조물 지도 */
    public static final VPRegistry<StructureMap> STRUCTURE_MAP = new VPRegistry<>("structure_map");
    /** 노획물 테이블 보상 */
    public static final VPRegistry<LootTableReward> LOOT_TABLE_REWARD = new VPRegistry<>("loot_table_reward");
    /** 레벨 기반 값 프리셋 */
    public static final VPRegistry<LevelBasedValuePreset> LEVEL_BASED_VALUE_PRESET = new VPRegistry<>("level_based_value_preset");
    /** 습격 웨이브 정보 */
    public static final VPRegistry<RaidWave> RAID_WAVE = new VPRegistry<>("raid_wave");
    /** 습격자 효과 */
    public static final VPRegistry<RaiderEffect> RAIDER_EFFECT = new VPRegistry<>("raider_effect");
    /** 갑옷 장식 재료 효과 */
    public static final VPRegistry<ArmorTrimEffect.TrimMaterialEffect> TRIM_MATERIAL_EFFECT = new VPRegistry<>("trim_material_effect");
    /** 갑옷 장식 형판 효과 */
    public static final VPRegistry<ArmorTrimEffect.TrimPatternEffect> TRIM_PATTERN_EFFECT = new VPRegistry<>("trim_pattern_effect");
    /** 아이템 수정자 */
    public static final VPRegistry<ItemModifier> ITEM_MODIFIER = new VPRegistry<>("modifier/item");
    /** 블록 수정자 */
    public static final VPRegistry<BlockModifier> BLOCK_MODIFIER = new VPRegistry<>("modifier/block");
    /** 엔티티 수정자 */
    public static final VPRegistry<EntityModifier> ENTITY_MODIFIER = new VPRegistry<>("modifier/entity");
    /** 물약 수정자 */
    public static final VPRegistry<PotionModifier> POTION_MODIFIER = new VPRegistry<>("modifier/potion");

    static {
        VanillaPlus.loadClass(VPSoundEvents.class);
        VanillaPlus.loadClass(VPAttributes.class);
        VanillaPlus.loadClass(VPEnchantmentLevelBasedValueTypes.class);
        VanillaPlus.loadClass(VPDataComponentTypes.class);
        VanillaPlus.loadClass(VPRecipeTypes.class);
        VanillaPlus.loadClass(VPRecipeSerializers.class);
        VanillaPlus.loadClass(VPRecipeBookCategories.class);
        VanillaPlus.loadClass(VPRecipeDisplayTypes.class);
        VanillaPlus.loadClass(VPPotions.class);
        VanillaPlus.loadClass(VPBlockEntityTypes.class);
        VanillaPlus.loadClass(VPEnchantmentEffectComponentTypes.class);
        VanillaPlus.loadClass(VPEnchantmentEntityEffectTypes.class);
        VanillaPlus.loadClass(VPGameRules.class);
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
        this(ResourceKey.createRegistryKey(registry.key().identifier()));
        this.vanillaRegistry = registry;
    }

    /**
     * Forge 레지스트리를 생성한다.
     *
     * @param name 이름
     */
    private VPRegistry(@NonNull String name) {
        this(ResourceKey.createRegistryKey(IdentifierUtil.fromPath(name)));
        this.forgeRegistryHolder = deferredRegister.makeRegistry(RegistryBuilder::of);
    }

    @SubscribeEvent
    private static void onServerAboutToStart(@NonNull ServerAboutToStartEvent event) {
        initData(event.getServer().registryAccess());
        VanillaPlus.LOGGER.debug("Server-side DataModifier Loaded");
    }

    @SubscribeEvent
    private static void onClientPlayerNetworkLoggingIn(@NonNull ClientPlayerNetworkEvent.LoggingIn event) {
        initData(event.getPlayer().registryAccess());
        VanillaPlus.LOGGER.debug("Client-side DataModifier Loaded");
    }

    private static void initData(@NonNull HolderLookup.Provider registries) {
        applyDataModifiers(registries, Registries.ITEM, VPRegistry.ITEM_MODIFIER);
        applyDataModifiers(registries, Registries.BLOCK, VPRegistry.BLOCK_MODIFIER);
        applyDataModifiers(registries, Registries.ENTITY_TYPE, VPRegistry.ENTITY_MODIFIER);
        applyDataModifiers(registries, Registries.POTION, VPRegistry.POTION_MODIFIER);
        applyDataModifiers(registries, Registries.ENCHANTMENT, VPRegistry.LEVEL_BASED_VALUE_PRESET);
        applyDataModifiers(registries, Registries.TRIM_MATERIAL, VPRegistry.TRIM_MATERIAL_EFFECT);
        applyDataModifiers(registries, Registries.TRIM_PATTERN, VPRegistry.TRIM_PATTERN_EFFECT);

        applyArmorTrimEffects(registries, VPRegistry.TRIM_MATERIAL_EFFECT);
        applyArmorTrimEffects(registries, VPRegistry.TRIM_PATTERN_EFFECT);
    }

    private static <T, U extends DataModifier<T>> void applyDataModifiers(@NonNull HolderLookup.Provider registries,
                                                                          @NonNull ResourceKey<Registry<T>> registryKey,
                                                                          @NonNull VPRegistry<U> vpRegistry) {
        registries.lookupOrThrow(registryKey).listElements().forEach(element -> {
            U dataModifier = registries.get(vpRegistry.createResourceKey(IdentifierUtil.fromResourceKey(element.key())))
                    .map(Holder::value)
                    .orElse(null);

            VPModifiableData.cast(element.value()).setDataModifier(dataModifier);
        });
    }

    private static <T extends ArmorTrimEffect<?>> void applyArmorTrimEffects(@NonNull HolderLookup.Provider registries,
                                                                             @NonNull VPRegistry<T> vpRegistry) {
        registries.lookupOrThrow(vpRegistry.registryKey).listElements().forEach(element ->
                registries.get(VPRegistry.LEVEL_BASED_VALUE_PRESET.createResourceKey(element.value().getIdentifier()))
                        .ifPresent(levelBasedValuePresetHolder ->
                                element.value().applyLevelBasedValuePreset(levelBasedValuePresetHolder.value())));
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
     * 레지스트리의 리소스 키를 생성한다.
     *
     * @param identifier 식별자
     * @return 리소스 키
     */
    @NonNull
    public ResourceKey<T> createResourceKey(@NonNull Identifier identifier) {
        return deferredRegister.key(identifier);
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
        return new VPRegistry<>(registryKey.identifier().getPath() + "/" + name);
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
        return Codec.lazyInitialized(vanillaRegistry != null ? vanillaRegistry::byNameCodec : () -> forgeRegistryHolder.get().getCodec());
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
