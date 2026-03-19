package com.dace.vanillaplus.network.packet;

import com.dace.vanillaplus.extension.client.sounds.VPSoundEngine;
import com.dace.vanillaplus.extension.client.sounds.VPSoundManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 효과음 정지 패킷을 처리하는 클래스.
 */
@AllArgsConstructor
public final class StopSoundPacketHandler implements PacketHandler {
    /** 효과음 유형 */
    private final SoundSource soundSource;
    /** 효과음 시드 */
    private final long seed;

    public StopSoundPacketHandler(@NonNull RegistryFriendlyByteBuf buf) {
        this.soundSource = buf.readEnum(SoundSource.class);
        this.seed = buf.readVarLong();
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
