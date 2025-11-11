package com.dace.vanillaplus;

import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.ShowHeadOnLocatorBarPacketHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 모드에서 사용하는 게임 규칙을 관리하는 클래스.
 */
@UtilityClass
public final class VPGameRules {
    static {
        GameRules.register("showHeadOnLocatorBar", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true,
                (minecraftServer, booleanValue) ->
                        minecraftServer.getPlayerList().getPlayers().forEach(serverPlayer ->
                                NetworkManager.sendToPlayer(new ShowHeadOnLocatorBarPacketHandler(booleanValue.get()), serverPlayer))));
    }

    /**
     * 클라이언트의 게임 규칙 상태를 관리하는 클래스.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static final class ClientState {
        @Getter
        private static ClientState instance;

        static {
            if (FMLEnvironment.dist.isClient())
                instance = new ClientState();
        }

        /** 위치 표시 바에 플레이어 머리 표시 */
        private boolean showHeadOnLocatorBar = true;
    }
}
