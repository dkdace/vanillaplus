package com.dace.vanillaplus;

import com.dace.vanillaplus.data.LootTableReward;
import com.dace.vanillaplus.data.RaidWave;
import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.util.CodecUtil;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 모드의 새로 고침 가능한 서버 데이터를 관리하는 클래스.
 *
 * @param <T> 데이터를 가져올 때 사용할 키의 타입
 * @param <U> 모드 데이터의 타입
 */
public final class ReloadableDataManager<T, U> {
    /** @see DataPackRegistries#LOOT_TABLE_REWARD */
    public static final ReloadableDataManager<ResourceKey<LootTable>, LootTableReward> LOOT_TABLE_REWARD = new ReloadableDataManager<>(
            DataPackRegistries.LOOT_TABLE_REWARD, LootTableReward.DIRECT_CODEC, IdentifierUtil::fromResourceKey);
    /** @see DataPackRegistries#RAIDER_EFFECT */
    public static final ReloadableDataManager<EntityType<?>, RaiderEffect> RAIDER_EFFECT = new ReloadableDataManager<>(
            DataPackRegistries.RAIDER_EFFECT, CodecUtil.fromCodecRegistry(StaticRegistry.RAIDER_EFFECT_TYPE),
            entityType -> IdentifierUtil.fromRegistry(BuiltInRegistries.ENTITY_TYPE, entityType));
    /** @see DataPackRegistries#RAID_WAVE */
    public static final ReloadableDataManager<Difficulty, RaidWave> RAID_WAVE = new ReloadableDataManager<>(
            DataPackRegistries.RAID_WAVE, RaidWave.DIRECT_CODEC, difficulty -> IdentifierUtil.fromPath(difficulty.getSerializedName()));

    /** 로거 인스턴스 */
    private static final Logger LOGGER = LogUtils.getLogger();

    /** 식별자별 데이터 목록 (식별자 : 데이터) */
    private final Map<Identifier, U> dataMap = new HashMap<>();
    /** 식별자 매핑에 사용할 작업 */
    private final Function<T, Identifier> mappingFunction;

    /**
     * 데이터 매니저 인스턴스를 생성한다.
     *
     * @param registryKey     레지스트리 키
     * @param codec           JSON 코덱
     * @param mappingFunction 식별자 매핑에 사용할 작업
     */
    private ReloadableDataManager(@NonNull ResourceKey<Registry<U>> registryKey, @NonNull Codec<U> codec,
                                  @NonNull Function<T, Identifier> mappingFunction) {
        this.mappingFunction = mappingFunction;

        AddReloadListenerEvent.BUS.addListener(event ->
                event.addListener(new SimpleJsonResourceReloadListener<>(event.getRegistries(), codec, registryKey) {
                    @Override
                    protected void apply(@NonNull Map<Identifier, U> preparations, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
                        dataMap.putAll(preparations);
                        LOGGER.info("Loaded {} data from {}", preparations.size(), registryKey.identifier());
                    }
                }));
    }

    static void bootstrap() {
        LOGGER.info("Initialized");
    }

    /**
     * 지정한 키에 해당하는 데이터를 반환한다.
     *
     * @param key 데이터를 가져올 때 사용할 키
     * @return 키에 해당하는 데이터
     */
    @NonNull
    public Optional<U> get(@NonNull T key) {
        return Optional.ofNullable(dataMap.get(mappingFunction.apply(key)));
    }

    /**
     * 지정한 키에 해당하는 데이터를 하위 클래스로 캐스팅하여 반환한다.
     *
     * @param <V>       모드 데이터를 상속받는 하위 클래스의 타입
     * @param castClass 캐스팅 대상 클래스
     * @param key       데이터를 가져올 때 사용할 키
     * @return 키에 해당하는 데이터. {@code castClass}로 캐스팅하여 반환
     */
    @NonNull
    public <V extends U> Optional<V> get(@NonNull T key, @NonNull Class<V> castClass) {
        return get(key).map(value -> castClass.isInstance(value) ? castClass.cast(value) : null);
    }
}
