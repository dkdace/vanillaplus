package com.dace.vanillaplus.mixin.server.level;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements VPMixin<ServerLevel> {
    @Shadow
    @Final
    private MinecraftServer server;

    @Unique
    private void broadcastSoundPacket(@Nullable Player excluded, double x, double y, double z, double radius, @NonNull ResourceKey<Level> dimension,
                                      @NonNull Packet<?> packet) {
        server.getPlayerList().getPlayers().forEach(serverPlayer -> {
            if (serverPlayer == excluded || serverPlayer.level().dimension() != dimension)
                return;

            double targetRadius = radius * serverPlayer.getAttributeValue(VPAttributes.HEARING_RANGE.getHolder().orElseThrow());

            if (serverPlayer.distanceToSqr(x, y, z) < targetRadius * targetRadius)
                serverPlayer.connection.send(packet);
        });
    }

    @Redirect(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcast(Lnet/minecraft/world/entity/player/Player;DDDDLnet/minecraft/resources/ResourceKey;Lnet/minecraft/network/protocol/Packet;)V"))
    private void redirectSoundPacketBroadcast0(PlayerList playerList, Player excluded, double x, double y, double z, double radius,
                                               ResourceKey<Level> dimension, Packet<?> packet) {
        broadcastSoundPacket(excluded, x, y, z, radius, dimension, packet);
    }

    @Redirect(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcast(Lnet/minecraft/world/entity/player/Player;DDDDLnet/minecraft/resources/ResourceKey;Lnet/minecraft/network/protocol/Packet;)V"))
    private void redirectSoundPacketBroadcast1(PlayerList playerList, Player excluded, double x, double y, double z, double radius,
                                               ResourceKey<Level> dimension, Packet<?> packet) {
        broadcastSoundPacket(excluded, x, y, z, radius, dimension, packet);
    }
}
