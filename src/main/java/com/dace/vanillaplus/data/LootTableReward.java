package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 노획물 테이블의 보상을 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootTableReward {
    /** JSON 코덱 */
    private static final Codec<LootTableReward> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("experience", UniformInt.of(20, 30))
                    .forGetter(LootTableReward::getXpRange))
            .apply(instance, LootTableReward::new));
    /** 데이터 매니저 */
    @Getter
    private static ReloadableDataManager<ResourceKey<LootTable>, LootTableReward> dataManager;

    /** 경험치 획득량 범위 */
    @NonNull
    private final IntProvider xpRange;

    @SubscribeEvent
    private static void onAddReloadListener(@NonNull AddReloadListenerEvent event) {
        dataManager = ReloadableDataManager.createResourceKeyed(event.getRegistries(), VPRegistry.LOOT_TABLE_REWARD, DIRECT_CODEC);
        event.addListener(dataManager);
    }
}
