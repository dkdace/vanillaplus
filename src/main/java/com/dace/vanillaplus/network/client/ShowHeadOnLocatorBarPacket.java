package com.dace.vanillaplus.network.client;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.network.VPPacket;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * showHeadOnLocatorBar 게임 규칙 변경 패킷 클래스.
 *
 * @param isEnabled 활성화 여부
 */
public record ShowHeadOnLocatorBarPacket(boolean isEnabled) implements VPPacket {
    public ShowHeadOnLocatorBarPacket(@NonNull RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isEnabled);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        VPGameRules.ClientState.getInstance().setShowHeadOnLocatorBar(isEnabled);
    }
}
