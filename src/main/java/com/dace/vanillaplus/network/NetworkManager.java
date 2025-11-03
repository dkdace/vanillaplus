package com.dace.vanillaplus.network;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.network.packet.PacketHandler;
import com.dace.vanillaplus.network.packet.PronePacketHandler;
import com.dace.vanillaplus.network.packet.RecoveryCompassTeleportPacketHandler;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.Function;

/**
 * 네트워크 관련 기능을 제공하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NetworkManager {
    /** 프로토콜 버전 */
    private static final int PROTOCOL_VERSION = 2;
    /** 패킷 전송 채널 */
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .acceptedVersions((status, version) -> version == PROTOCOL_VERSION)
            .simpleChannel();

    @SubscribeEvent
    private static void onFMLCommonSetup(@NonNull FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerPacket(PacketFlow.SERVERBOUND, PronePacketHandler.class, PronePacketHandler::new);
            registerPacket(PacketFlow.CLIENTBOUND, RecoveryCompassTeleportPacketHandler.class,
                    buf -> new RecoveryCompassTeleportPacketHandler());

            CHANNEL.build();
        });
    }

    /**
     * 지정한 서버 패킷 처리기를 등록한다.
     *
     * @param packetFlow         패킷 유형 (클라이언트 및 서버)
     * @param packetHandlerClass 패킷 처리기 클래스
     * @param packetFunction     패킷 처리기 반환에 실행할 작업
     * @param <T>                {@link PacketHandler}를 상속받는 패킷 처리기
     */
    private static <T extends PacketHandler> void registerPacket(@NonNull PacketFlow packetFlow, @NonNull Class<T> packetHandlerClass,
                                                                 @NonNull Function<RegistryFriendlyByteBuf, T> packetFunction) {
        CHANNEL.play().flow(packetFlow).add(packetHandlerClass, StreamCodec.of((buf, packet) -> packet.encode(buf),
                packetFunction::apply), PacketHandler::handle);
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
