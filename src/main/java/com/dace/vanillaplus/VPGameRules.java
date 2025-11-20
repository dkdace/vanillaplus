package com.dace.vanillaplus;

import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.ShowHeadOnLocatorBarPacketHandler;
import lombok.*;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 모드에서 사용하는 게임 규칙을 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPGameRules {
    /** 위시 표시 바에 플레이어 머리 표시 */
    public static final GameRules.Key<GameRules.BooleanValue> SHOW_HEAD_ON_LOCATOR_BAR = GameRules.register("showHeadOnLocatorBar",
            GameRules.Category.PLAYER, GameRules.BooleanValue.create(true,
                    (minecraftServer, booleanValue) ->
                            minecraftServer.getPlayerList().getPlayers().forEach(serverPlayer ->
                                    NetworkManager.sendToPlayer(new ShowHeadOnLocatorBarPacketHandler(booleanValue.get()), serverPlayer))));
    /** 엔더 드래곤 전투 중 몹 비활성화 */
    public static final GameRules.Key<GameRules.BooleanValue> DISABLE_MOBS_IN_BOSS_FIGHT = GameRules.register("disableMobsInBossFight",
            GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    @SubscribeEvent
    private static void onMobSpawnAllowDespawn(@NonNull MobSpawnEvent.AllowDespawn event) {
        ServerLevel level = event.getLevel().getLevel();

        if (level.getGameRules().getBoolean(DISABLE_MOBS_IN_BOSS_FIGHT) && level.getBiome(event.getEntity().blockPosition()).is(Biomes.THE_END)
                && !level.getLevel().getEntities(EntityType.ENDER_DRAGON, enderDragon -> true).isEmpty())
            event.setResult(Result.ALLOW);
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
