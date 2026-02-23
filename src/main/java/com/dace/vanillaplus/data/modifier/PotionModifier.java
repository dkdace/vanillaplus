package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;

/**
 * 물약의 효과를 수정하는 물약 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PotionModifier {
    /** JSON 코덱 */
    private static final Codec<PotionModifier> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter(PotionModifier::getColor),
                    Codec.BOOL.optionalFieldOf("glistering", false).forGetter(PotionModifier::isGlistering),
                    MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(PotionModifier::getEffects))
            .apply(instance, PotionModifier::new));

    /** 물약 색상 */
    @NonNull
    private final Optional<Integer> color;
    /** 반짝임 여부 */
    private final boolean isGlistering;
    /** 효과 목록 */
    @NonNull
    private final List<MobEffectInstance> effects;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.POTION_MODIFIER.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }
}
