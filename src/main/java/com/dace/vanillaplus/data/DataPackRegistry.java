package com.dace.vanillaplus.data;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.util.CodecUtil;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.world.LootTableReward;
import com.dace.vanillaplus.world.MobEffectValues;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaidWave;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.dace.vanillaplus.world.item.PotionModifier;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

/**
 * 모드의 데이터 팩 레지스트리를 관리하는 클래스.
 */
@UtilityClass
public final class DataPackRegistry {
    /** 로거 인스턴스 */
    private static final Logger LOGGER = LogUtils.getLogger();

    /** 노획물 테이블 보상 */
    public static final ResourceKey<Registry<LootTableReward>> LOOT_TABLE_REWARD = create("loot_table_reward");
    /** 상태 효과 값 */
    public static final ResourceKey<Registry<MobEffectValues>> MOB_EFFECT_VALUES = create("mob_effect",
            MobEffectValues.DIRECT_CODEC, Registries.MOB_EFFECT);
    /** 습격자 효과 */
    public static final ResourceKey<Registry<RaiderEffect>> RAIDER_EFFECT = create("raider_effect");
    /** 습격 웨이브 정보 */
    public static final ResourceKey<Registry<RaidWave>> RAID_WAVE = create("raid_wave");
    /** 블록 수정자 */
    public static final ResourceKey<Registry<BlockModifier>> BLOCK_MODIFIER = create("modifier/block",
            CodecUtil.fromCodecRegistry(StaticRegistry.BLOCK_MODIFIER_TYPE), Registries.BLOCK);
    /** 엔티티 수정자 */
    public static final ResourceKey<Registry<EntityModifier>> ENTITY_MODIFIER = create("modifier/entity",
            CodecUtil.fromCodecRegistry(StaticRegistry.ENTITY_MODIFIER_TYPE), Registries.ENTITY_TYPE);
    /** 아이템 수정자 */
    public static final ResourceKey<Registry<ItemModifier>> ITEM_MODIFIER = create("modifier/item",
            CodecUtil.fromCodecRegistry(StaticRegistry.ITEM_MODIFIER_TYPE), Registries.ITEM);
    /** 물약 수정자 */
    public static final ResourceKey<Registry<PotionModifier>> POTION_MODIFIER = create("modifier/potion",
            PotionModifier.DIRECT_CODEC, Registries.POTION);

    public static void bootstrap() {
        LOGGER.info("Initialized");
    }

    @NonNull
    private static <T> ResourceKey<Registry<T>> create(@NonNull String name) {
        return ResourceKey.createRegistryKey(IdentifierUtil.fromPath(name));
    }

    @NonNull
    private static <T> ResourceKey<Registry<T>> create(@NonNull String name, @NonNull Codec<T> codec) {
        ResourceKey<Registry<T>> registryKey = create(name);
        DataPackRegistryEvent.NewRegistry.BUS.addListener(event -> event.dataPackRegistry(registryKey, codec, codec));

        return registryKey;
    }

    @NonNull
    private static <T, U> ResourceKey<Registry<T>> create(@NonNull String name, @NonNull Codec<T> codec,
                                                          @NonNull ResourceKey<Registry<U>> targetRegistryKey) {
        ResourceKey<Registry<T>> registryKey = create(name, codec);

        ServerAboutToStartEvent.BUS.addListener(event ->
                applyDataModifiers(registryKey, event.getServer().registryAccess(), targetRegistryKey));
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event ->
                applyDataModifiers(registryKey, event.getPlayer().registryAccess(), targetRegistryKey));

        return registryKey;
    }

    private static <T, U> void applyDataModifiers(@NonNull ResourceKey<Registry<T>> registryKey, @NonNull HolderLookup.Provider registries,
                                                  @NonNull ResourceKey<Registry<U>> targetRegistryKey) {
        registries.lookupOrThrow(targetRegistryKey).listElements().forEach(element -> {
            T dataModifier = registries.get(ResourceKey.create(registryKey, IdentifierUtil.fromResourceKey(element.key())))
                    .map(Holder::value)
                    .orElse(null);

            VPModifiableData.cast(element.value()).setDataModifier(dataModifier);

            if (dataModifier != null)
                LOGGER.debug("Applied DataModifier to {}", element.key());
        });
    }
}
