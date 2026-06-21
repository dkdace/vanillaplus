package com.dace.vanillaplus.network.client;

import com.dace.vanillaplus.extension.client.sounds.VPSoundEngine;
import com.dace.vanillaplus.extension.client.sounds.VPSoundManager;
import com.dace.vanillaplus.network.VPPacket;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 효과음 정지 패킷 클래스.
 *
 * @param soundSource 효과음 유형
 * @param seed        효과음 시드
 */
public record StopSoundPacket(@NonNull SoundSource soundSource, long seed) implements VPPacket {
    public StopSoundPacket(@NonNull RegistryFriendlyByteBuf buf) {
        this(buf.readEnum(SoundSource.class), buf.readVarLong());
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeEnum(soundSource);
        buf.writeVarLong(seed);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        VPSoundEngine.cast(VPSoundManager.cast(Minecraft.getInstance().getSoundManager()).getSoundEngine()).stop(soundSource, seed);
    }
}
