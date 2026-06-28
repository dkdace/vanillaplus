package com.dace.vanillaplus.network.client;

import com.dace.vanillaplus.extension.client.gui.VPGui;
import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import com.dace.vanillaplus.network.VPPacket;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 플레이어의 엔티티 공격 패킷 클래스.
 *
 * @param entityId 엔티티 ID
 * @param damage   피해량
 */
public record PlayerDamageEntityPacket(int entityId, float damage) implements VPPacket {
    public PlayerDamageEntityPacket(@NonNull RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readFloat());
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(damage);
    }

    @Override
    public void handle(@NonNull CustomPayloadEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel clientLevel = minecraft.level;

        if (clientLevel != null && clientLevel.getEntity(entityId) instanceof LivingEntity livingEntity) {
            VPGui.cast(minecraft.gui).updateRecentDamage(damage, damage >= livingEntity.getHealth());
            VPLivingEntity.cast(livingEntity).updateRenderHealth();
        }

        context.setPacketHandled(true);
    }
}
