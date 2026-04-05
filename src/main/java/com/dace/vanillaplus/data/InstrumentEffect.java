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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 염소 뿔의 효과를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InstrumentEffect {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<InstrumentEffect>> CODEC = VPRegistry.INSTRUMENT_EFFECT.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<InstrumentEffect> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(MobEffectInstance.CODEC.fieldOf("effect").forGetter(instrumentEffect -> instrumentEffect.mobEffectInstance))
            .apply(instance, InstrumentEffect::new));

    /** 상태 효과 인스턴스 */
    private final MobEffectInstance mobEffectInstance;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.INSTRUMENT_EFFECT.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }
}
