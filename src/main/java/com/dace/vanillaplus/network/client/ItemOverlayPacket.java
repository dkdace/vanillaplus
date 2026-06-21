package com.dace.vanillaplus.network.client;

import com.dace.vanillaplus.network.VPPacket;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 아이템 오버레이 패킷 클래스.
 *
 * @param itemHolder 표시할 아이템
 */
public record ItemOverlayPacket(@NonNull Holder<Item> itemHolder) implements VPPacket {
    public ItemOverlayPacket(@NonNull RegistryFriendlyByteBuf buf) {
        this(Item.STREAM_CODEC.decode(buf));
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        Item.STREAM_CODEC.encode(buf, itemHolder);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(itemHolder.value().getDefaultInstance());
        context.setPacketHandled(true);
    }
}
