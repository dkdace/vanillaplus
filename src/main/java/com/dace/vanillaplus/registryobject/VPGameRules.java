package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import lombok.*;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 게임 규칙을 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPGameRules {
    public static final RegistryObject<GameRule<Boolean>> SHOW_HEAD_ON_LOCATOR_BAR = create("show_head_on_locator_bar",
            GameRuleCategory.PLAYER, true);
    public static final RegistryObject<GameRule<Boolean>> DISABLE_MOBS_IN_BOSS_FIGHT = create("disable_mobs_in_boss_fight",
            GameRuleCategory.SPAWNING, true);

    @NonNull
    private static RegistryObject<GameRule<Boolean>> create(@NonNull String name, @NonNull GameRuleCategory gameRuleCategory, boolean defaultValue) {
        return VPRegistry.register(VPRegistry.GAME_RULE, name, () -> new GameRule<>(gameRuleCategory, GameRuleType.BOOL, BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean, Codec.BOOL, value -> value ? 1 : 0, defaultValue, FeatureFlagSet.of()));
    }

    @SubscribeEvent
    private static void onMobSpawnAllowDespawn(@NonNull MobSpawnEvent.AllowDespawn event) {
        ServerLevel level = event.getLevel().getLevel();

        if (level.getGameRules().get(DISABLE_MOBS_IN_BOSS_FIGHT.get()) && level.getBiome(event.getEntity().blockPosition()).is(Biomes.THE_END)
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
