package com.dace.vanillaplus.network;

import com.dace.vanillaplus.util.IdentifierUtil;
import com.google.common.reflect.ClassPath;
import com.mojang.logging.LogUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.simple.SimpleFlow;
import net.minecraftforge.network.simple.SimpleProtocol;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 네트워크 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class NetworkManager {
    /** 로거 인스턴스 */
    private static final Logger LOGGER = LogUtils.getLogger();
    /** 프로토콜 버전 */
    private static final int PROTOCOL_VERSION = 5;
    /** {@link com.dace.vanillaplus.network.client} 패키지 */
    private static final String PACKAGE_CLIENT = "com.dace.vanillaplus.network.client";
    /** {@link com.dace.vanillaplus.network.server} 패키지 */
    private static final String PACKAGE_SERVER = "com.dace.vanillaplus.network.server";
    /** 패킷 전송 채널 */
    private static final SimpleChannel CHANNEL = createChannel();

    public static void bootstrap() {
        LOGGER.info("Initialized");
    }

    @NonNull
    private static SimpleChannel createChannel() {
        SimpleProtocol<RegistryFriendlyByteBuf, Object> protocol = ChannelBuilder.named(IdentifierUtil.fromPath("main"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .simpleChannel()
                .play();

        try {
            SimpleFlow<RegistryFriendlyByteBuf, Object> flow;
            flow = addPackets(PACKAGE_CLIENT, protocol.clientbound());
            flow = addPackets(PACKAGE_SERVER, flow.serverbound());

            return flow.build();
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot initialize NetworkManager", ex);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static SimpleFlow<RegistryFriendlyByteBuf, Object> addPackets(@NonNull String packageName,
                                                                          @NonNull SimpleFlow<RegistryFriendlyByteBuf, Object> flow)
            throws IOException, ClassNotFoundException {
        for (ClassPath.ClassInfo classInfo : ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses(packageName)) {
            Class<?> clazz = Class.forName(classInfo.getName());

            if (VPPacket.class.isAssignableFrom(clazz)) {
                StreamCodec<RegistryFriendlyByteBuf, VPPacket> streamCodec = StreamCodec.ofMember(VPPacket::encode, buf -> {
                    try {
                        return (VPPacket) MethodHandles.lookup()
                                .findConstructor(clazz, MethodType.methodType(void.class, RegistryFriendlyByteBuf.class))
                                .invoke(buf);
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Cannot create VPPacket instance", ex);
                    }
                });

                flow = flow.addMain((Class<VPPacket>) clazz, streamCodec, VPPacket::handle);

                LOGGER.debug("Loaded {}", classInfo);
            }
        }

        return flow;
    }

    /**
     * 지정한 패킷을 서버로 전송한다.
     *
     * @param vpPacket 패킷
     */
    public static void sendToServer(@NonNull VPPacket vpPacket) {
        CHANNEL.send(vpPacket, PacketDistributor.SERVER.noArg());
    }

    /**
     * 지정한 패킷을 특정 플레이어에게 전송한다.
     *
     * @param vpPacket 패킷
     * @param player   대상 플레이어
     */
    public static void sendToPlayer(@NonNull VPPacket vpPacket, @NonNull ServerPlayer player) {
        CHANNEL.send(vpPacket, PacketDistributor.PLAYER.with(player));
    }

    /**
     * 지정한 패킷을 모든 플레이어에게 전송한다.
     *
     * @param vpPacket 패킷
     */
    public static void broadcast(@NonNull VPPacket vpPacket) {
        CHANNEL.send(vpPacket, PacketDistributor.ALL.noArg());
    }

    /**
     * 지정한 패킷을 특정 월드의 모든 플레이어에게 전송한다.
     *
     * @param vpPacket 패킷
     * @param level    월드
     */
    public static void sendToLevel(@NonNull VPPacket vpPacket, @NonNull ServerLevel level) {
        CHANNEL.send(vpPacket, PacketDistributor.DIMENSION.with(level.dimension()));
    }
}
