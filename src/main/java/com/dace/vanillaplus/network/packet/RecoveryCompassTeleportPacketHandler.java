package com.dace.vanillaplus.network.packet;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 만회 나침반 사용 패킷을 처리하는 클래스.
 */
@NoArgsConstructor
public final class RecoveryCompassTeleportPacketHandler implements PacketHandler {
    @Override
    public void encode(@NonNull FriendlyByteBuf buf) {
        buf.writeBoolean(true);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(Items.RECOVERY_COMPASS.getDefaultInstance());
    }
}
