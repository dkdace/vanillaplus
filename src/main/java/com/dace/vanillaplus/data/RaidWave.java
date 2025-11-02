package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;

/**
 * 습격 웨이브 정보를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RaidWave {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<RaidWave>> CODEC = VPRegistry.RAID_WAVE.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<RaidWave> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(RaiderType.CODEC, ExtraCodecs.POSITIVE_INT).listOf().fieldOf("waves")
                    .forGetter(raidWave -> raidWave.raiderCountMaps))
            .apply(instance, RaidWave::new));

    /** 습격대원 종류별 대원 수 목록 (습격대원 종류 : 대원 수) */
    private final List<Map<RaiderType, Integer>> raiderCountMaps;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.RAID_WAVE.getRegistryKey(), DIRECT_CODEC);
    }

    /**
     * 지정한 난이도에 해당하는 습격 웨이브 정보를 반환한다.
     *
     * @param difficulty 난이도
     * @return 습격 웨이브 정보. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public static RaidWave fromDifficulty(@NonNull Difficulty difficulty) {
        return VPRegistry.RAID_WAVE.getValue(difficulty.getKey());
    }

    /**
     * 지정한 웨이브 번호에 해당하는 습격대원 종류별 대원 수 목록을 반환한다.
     *
     * @param wave 웨이브 번호. 1 이상의 값
     * @return 습격대원 종류별 대원 수 목록
     * @throws IllegalArgumentException 인자값이 유효하지 않으면 발생
     */
    @NonNull
    @UnmodifiableView
    public Map<RaiderType, Integer> getRaiderCountMap(int wave) {
        Validate.isTrue(wave >= 1, "wave >= 1 (%d)", wave);
        return raiderCountMaps.get(wave - 1);
    }

    /**
     * 전체 웨이브 수를 반환한다.
     *
     * @return 전체 웨이브 수
     */
    public int getTotalWaves() {
        return raiderCountMaps.size();
    }

    /**
     * 습격대원 종류.
     */
    @AllArgsConstructor
    @Getter
    public enum RaiderType {
        PILLAGER(EntityType.PILLAGER),
        VINDICATOR(EntityType.VINDICATOR),
        WITCH(EntityType.WITCH),
        RAVAGER(EntityType.RAVAGER),
        EVOKER(EntityType.EVOKER),
        ILLUSIONER(EntityType.ILLUSIONER),
        RAVAGER_WITH_PILLAGER(EntityType.RAVAGER, EntityType.PILLAGER),
        RAVAGER_WITH_VINDICATOR(EntityType.RAVAGER, EntityType.VINDICATOR),
        RAVAGER_WITH_EVOKER(EntityType.RAVAGER, EntityType.EVOKER);

        /** JSON 코덱 */
        private static final Codec<RaiderType> CODEC = CodecUtil.fromEnum(RaiderType.class);
        /** 엔티티 타입 */
        private final EntityType<? extends Raider> entityType;
        /** 탑승 엔티티 타입 */
        @Nullable
        private final EntityType<? extends Raider> ridingEntityType;

        RaiderType(@NonNull EntityType<? extends Raider> entityType) {
            this(entityType, null);
        }
    }
}
