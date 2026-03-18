package com.dace.vanillaplus.network.packet;

import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 엎드리기 패킷을 처리하는 클래스.
 */
@AllArgsConstructor
public final class PronePacketHandler implements PacketHandler {
    /** 키 입력 여부 */
    private boolean isPressed;

    public PronePacketHandler(@NonNull RegistryFriendlyByteBuf buf) {
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
