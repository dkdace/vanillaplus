package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Instrument;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Instrument.class)
public abstract class InstrumentMixin implements VPInstrument {
    @Shadow
    @Final
    public static final Codec<Instrument> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(SoundEvent.CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("use_duration").forGetter(Instrument::useDuration),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(Instrument::description),
                    MobEffectInstance.CODEC.optionalFieldOf("effect")
                            .forGetter(instrument -> ((InstrumentMixin) (Object) instrument).mobEffectInstance))
            .apply(instance, InstrumentMixin::create)
    );
    @Shadow
    @Final
    public static final StreamCodec<RegistryFriendlyByteBuf, Instrument> DIRECT_STREAM_CODEC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC, Instrument::soundEvent,
            ByteBufCodecs.FLOAT, Instrument::useDuration,
            ByteBufCodecs.FLOAT, Instrument::range,
            ComponentSerialization.STREAM_CODEC, Instrument::description,
            ByteBufCodecs.optional(MobEffectInstance.STREAM_CODEC), instrument -> ((InstrumentMixin) (Object) instrument).mobEffectInstance,
            InstrumentMixin::create);

    @Unique
    @Getter
    private Optional<MobEffectInstance> mobEffectInstance = Optional.empty();

    @Unique
    @NonNull
    private static Instrument create(Holder<SoundEvent> soundEvent, float useDuration, float range, Component description,
                                     Optional<MobEffectInstance> mobEffectInstance) {
        Instrument instrument = new Instrument(soundEvent, useDuration, range, description);
        ((InstrumentMixin) (Object) instrument).mobEffectInstance = mobEffectInstance;

        return instrument;
    }
}
