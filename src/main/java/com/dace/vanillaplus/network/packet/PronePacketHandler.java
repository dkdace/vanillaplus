package com.dace.vanillaplus.network.packet;

import com.dace.vanillaplus.custom.CustomPlayer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 엎드리기 패킷을 처리하는 클래스.
 */
@AllArgsConstructor
public final class PronePacketHandler implements PacketHandler {
    /** 키 입력 여부 */
    private boolean isPressed;

    public PronePacketHandler(@NonNull FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void encode(@NonNull FriendlyByteBuf buf) {
        buf.writeBoolean(isPressed);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null)
            return;

        ((CustomPlayer) player).vp$setProneKeyDown(isPressed);
    }
}
