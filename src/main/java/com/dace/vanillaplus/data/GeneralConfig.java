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
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 게임 요소의 전역 설정 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GeneralConfig {
    /** JSON 코덱 */
    private static final Codec<GeneralConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.floatRange(0, 1).optionalFieldOf("max_repair_limit_ratio", 0.5F)
                    .forGetter(GeneralConfig::getMaxRepairLimitRatio))
            .apply(instance, GeneralConfig::new));
    /** 리소스 키 */
    private static final ResourceKey<GeneralConfig> RESOURCE_KEY = VPRegistry.CONFIG.createResourceKey("general");

    /** 최대 내구도 : 최대 수리 한도 비율 */
    private final float maxRepairLimitRatio;

    /**
     * 전역 설정을 반환한다.
     *
     * @return 전역 설정
     */
    @NonNull
    public static GeneralConfig get() {
        return VPRegistry.getRegistries().getOrThrow(RESOURCE_KEY).value();
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.CONFIG.getRegistryKey(), CODEC, CODEC);
    }
}
