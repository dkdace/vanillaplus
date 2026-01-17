package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 노획물 테이블의 보상을 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootTableReward {
    /** DataGetter */
    public static final DataGetter<ResourceKey<LootTable>, LootTableReward> DATA_GETTER = DataGetter.fromVPRegistry(VPRegistry.LOOT_TABLE_REWARD);

    /** 레지스트리 코덱 */
    public static final Codec<Holder<LootTableReward>> CODEC = VPRegistry.LOOT_TABLE_REWARD.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<LootTableReward> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("experience", UniformInt.of(20, 30))
                    .forGetter(LootTableReward::getXpRange))
            .apply(instance, LootTableReward::new));
    /** 경험치 획득량 범위 */
    @NonNull
    private final IntProvider xpRange;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.LOOT_TABLE_REWARD.getRegistryKey(), DIRECT_CODEC);
    }
}
