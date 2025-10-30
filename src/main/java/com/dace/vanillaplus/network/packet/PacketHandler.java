package com.dace.vanillaplus.network.packet;

import lombok.NonNull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 패킷 처리기 인터페이스.
 */
public interface PacketHandler {
    /**
     * 패킷을 인코딩한다.
     *
     * @param buf ByteBuf 인스턴스
     */
    void encode(@NonNull FriendlyByteBuf buf);

    /**
     * 패킷을 처리하여 지정된 작업을 수행한다.
     *
     * @param context 메시지
     */
    void handle(@NonNull CustomPayloadEvent.Context context);
}
