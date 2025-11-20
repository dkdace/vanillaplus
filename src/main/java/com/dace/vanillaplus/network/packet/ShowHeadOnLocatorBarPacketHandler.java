package com.dace.vanillaplus.network.packet;

import com.dace.vanillaplus.VPGameRules;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * showHeadOnLocatorBar 게임 규칙 변경 패킷을 처리하는 클래스.
 */
@AllArgsConstructor
public final class ShowHeadOnLocatorBarPacketHandler implements PacketHandler {
    /** 활성화 여부 */
    private final boolean isEnabled;

    public ShowHeadOnLocatorBarPacketHandler(@NonNull FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void encode(@NonNull FriendlyByteBuf buf) {
        buf.writeBoolean(isEnabled);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        VPGameRules.ClientState.getInstance().setShowHeadOnLocatorBar(isEnabled);
    }
}
