package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import lombok.*;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Endermite;
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
    /** 최대 흉조 레벨 */
    public static final int MAX_POSSIBLE_BAD_OMEN_LEVEL = 10;

    public static final RegistryObject<GameRule<Boolean>> SHOW_HEAD_ON_LOCATOR_BAR = create("show_head_on_locator_bar",
            GameRuleCategory.PLAYER, true);
    public static final RegistryObject<GameRule<Boolean>> SPAWN_MOBS_IN_ENDER_DRAGON_FIGHT = create("spawn_mobs_in_ender_dragon_fight",
            GameRuleCategory.SPAWNING, false);
    public static final RegistryObject<GameRule<Integer>> MAX_BAD_OMEN_LEVEL = create("max_bad_omen_level",
            GameRuleCategory.MISC, MAX_POSSIBLE_BAD_OMEN_LEVEL, 1, MAX_POSSIBLE_BAD_OMEN_LEVEL);

    @NonNull
    private static RegistryObject<GameRule<Boolean>> create(@NonNull String name, @NonNull GameRuleCategory gameRuleCategory, boolean defaultValue) {
        return VPRegistry.register(VPRegistry.GAME_RULE, name, () -> new GameRule<>(gameRuleCategory, GameRuleType.BOOL, BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean, Codec.BOOL, value -> value ? 1 : 0, defaultValue, FeatureFlagSet.of()));
    }

    @NonNull
    private static RegistryObject<GameRule<Integer>> create(@NonNull String name, @NonNull GameRuleCategory gameRuleCategory, int defaultValue,
                                                            int minValue, int maxValue) {
        return VPRegistry.register(VPRegistry.GAME_RULE, name, () -> new GameRule<>(gameRuleCategory, GameRuleType.INT,
                IntegerArgumentType.integer(minValue, maxValue), GameRuleTypeVisitor::visitInteger, Codec.intRange(minValue, maxValue),
                value -> value, defaultValue, FeatureFlagSet.of()));
    }

    /**
     * 지정한 게임 규칙 레지스트리 개체의 게임 규칙 값을 반환한다.
     *
     * @param registryObject 게임 규칙 레지스트리 개체
     * @param serverLevel    월드
     * @param <T>            게임 규칙 값의 타입
     * @return 게임 규칙 값
     */
    @NonNull
    public static <T> T getValue(@NonNull RegistryObject<GameRule<T>> registryObject, @NonNull ServerLevel serverLevel) {
        return serverLevel.getGameRules().get(registryObject.get());
    }

    @SubscribeEvent
    private static void onMobSpawnAllowDespawn(@NonNull MobSpawnEvent.AllowDespawn event) {
        ServerLevel level = event.getLevel().getLevel();
        Mob entity = event.getEntity();

        if (!(entity instanceof Endermite) && !level.getGameRules().get(SPAWN_MOBS_IN_ENDER_DRAGON_FIGHT.get())
                && level.getBiome(entity.blockPosition()).is(Biomes.THE_END)
                && !level.getEntities(EntityType.ENDER_DRAGON, enderDragon -> true).isEmpty())
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
