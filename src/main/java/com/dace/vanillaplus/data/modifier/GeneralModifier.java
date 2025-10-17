package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 분류되지 않은 기타 요소를 수정하는 전역 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneralModifier {
    /** 전역 수정자 리소스 이름 */
    private static final String RESOURCE_NAME = "general";
    /** JSON 코덱 */
    private static final Codec<GeneralModifier> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.floatRange(0, 1).optionalFieldOf("smelting_tool_damage_ratio", 1F)
                            .forGetter(GeneralModifier::getSmeltingToolDamageRatio),
                    Codec.floatRange(0, 1).optionalFieldOf("max_repair_limit_ratio", 0.5F)
                            .forGetter(GeneralModifier::getMaxRepairLimitRatio))
            .apply(instance, GeneralModifier::new));

    /** 철 또는 금 도구를 화로에서 녹일 때 내구도 감소 비율 */
    private final float smeltingToolDamageRatio;
    /** 최대 내구도 : 최대 수리 한도 비율 */
    private final float maxRepairLimitRatio;

    /**
     * 전역 수정자를 반환한다.
     *
     * @return 전역 수정자
     */
    @NonNull
    public static GeneralModifier get() {
        return VPRegistry.MODIFIER.getValueOrThrow(RESOURCE_NAME);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.MODIFIER.getRegistryKey(), CODEC);
    }
}
