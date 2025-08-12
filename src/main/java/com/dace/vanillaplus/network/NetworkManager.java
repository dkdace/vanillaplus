package com.dace.vanillaplus.network;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.network.packet.PacketHandler;
import com.dace.vanillaplus.network.packet.PronePacketHandler;
import lombok.NonNull;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * 네트워크 관련 기능을 제공하는 클래스.
 */
public final class NetworkManager {
    /** 프로토콜 버전 */
    private static final int PROTOCOL_VERSION = 1;
    /** 패킷 전송 채널 */
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .acceptedVersions((status, version) -> version == PROTOCOL_VERSION)
            .simpleChannel();

    /**
     * 패킷 전송 채널을 등록한다.
     */
    public static void register() {
        CHANNEL.play().serverbound(simpleFlow ->
                simpleFlow.add(PronePacketHandler.class, StreamCodec.of((buf, packet) -> packet.encode(buf),
                        PronePacketHandler::new), PronePacketHandler::handle));

        CHANNEL.build();
    }

    /**
     * 지정한 패킷을 서버로 전송한다.
     *
     * @param packetHandler 패킷 처리기
     */
    public static void sendToServer(@NonNull PacketHandler packetHandler) {
        CHANNEL.send(packetHandler, PacketDistributor.SERVER.noArg());
    }

    /**
     * 지정한 패킷을 특정 플레이어에게 전송한다.
     *
     * @param packetHandler 패킷 처리기
     * @param player        대상 플레이어
     */
    public static void sendToPlayer(@NonNull PacketHandler packetHandler, @NonNull ServerPlayer player) {
        CHANNEL.send(packetHandler, PacketDistributor.PLAYER.with(player));
    }
}
