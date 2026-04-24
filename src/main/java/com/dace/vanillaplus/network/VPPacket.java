package com.dace.vanillaplus.network;

import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 모드에서 사용하는 패킷 인터페이스.
 */
public interface VPPacket {
    /**
     * 패킷을 인코딩한다.
     *
     * @param buf ByteBuf 인스턴스
     */
    void encode(@NonNull RegistryFriendlyByteBuf buf);

    /**
     * 패킷을 처리하여 지정된 작업을 수행한다.
     *
     * @param context 메시지
     */
    void handle(@NonNull CustomPayloadEvent.Context context);
}
