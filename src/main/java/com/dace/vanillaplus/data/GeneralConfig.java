package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
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
            .group(ExtraCodecs.floatRange(0, 1).optionalFieldOf("smelting_tool_damage_ratio", 1F)
                            .forGetter(GeneralConfig::getSmeltingToolDamageRatio),
                    ExtraCodecs.floatRange(0, 1).optionalFieldOf("max_repair_limit_ratio", 0.5F)
                            .forGetter(GeneralConfig::getMaxRepairLimitRatio),
                    ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("max_bad_omen_level", 5)
                            .forGetter(GeneralConfig::getMaxBadOmenLevel),
                    ExtraCodecs.floatRange(1, 10).optionalFieldOf("extended_enchantment_max_cost_multiplier", 1F)
                            .forGetter(GeneralConfig::getExtendedEnchantmentMaxCostMultiplier))
            .apply(instance, GeneralConfig::new));
    /** 리소스 이름 */
    private static final String RESOURCE_NAME = "general";

    /** 구리, 철 또는 금 도구를 화로에서 녹일 때 내구도 감소 비율 */
    private final float smeltingToolDamageRatio;
    /** 최대 내구도 : 최대 수리 한도 비율 */
    private final float maxRepairLimitRatio;
    /** 최대 흉조 레벨 */
    private final int maxBadOmenLevel;
    /** {@link VPTags.Items#EXTENDED_ENCHANTABLE} 아이템의 최대 마법 부여 비용 배수 */
    private final float extendedEnchantmentMaxCostMultiplier;

    /**
     * 전역 설정을 반환한다.
     *
     * @return 전역 설정
     */
    @NonNull
    public static GeneralConfig get() {
        return VPRegistry.CONFIG.getValueOrThrow(RESOURCE_NAME);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.CONFIG.getRegistryKey(), CODEC, CODEC);
    }
}
