package com.dace.vanillaplus.network.server;

import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import com.dace.vanillaplus.network.VPPacket;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 엎드리기 패킷 클래스.
 *
 * @param isPressed 키 입력 여부
 */
public record PronePacket(boolean isPressed) implements VPPacket {
    public PronePacket(@NonNull RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isPressed);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null)
            VPPlayer.cast(player).setProneKeyDown(isPressed);
    }
}
