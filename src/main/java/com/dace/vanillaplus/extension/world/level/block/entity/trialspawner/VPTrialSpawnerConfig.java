package com.dace.vanillaplus.extension.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.extension.VPMixin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link TrialSpawnerConfig}를 확장하는 인터페이스.
 */
public interface VPTrialSpawnerConfig extends VPMixin<TrialSpawnerConfig> {
    @NonNull
    static VPTrialSpawnerConfig cast(@NonNull TrialSpawnerConfig object) {
        return (VPTrialSpawnerConfig) (Object) object;
    }

    /**
     * @return 불길한 시련 생성기 설정
     */
    @NonNull
    Ominous getOminous();

    /**
     * 불길한 시련 생성기 설정 클래스.
     *
     * @param overrideLootTable           노획물 테이블 덮어쓰기 설정
     * @param experiences                 흉조 레벨별 클리어 시 경험치 목록
     * @param ominousItemCooldowns        흉조 레벨별 불길한 아이템 생성 쿨타임 목록
     * @param ominousItemActivationDelays 흉조 레벨별 불길한 아이템 활성화 딜레이 목록
     */
    record Ominous(@NonNull Optional<OverrideLootTable> overrideLootTable, @NonNull List<Integer> experiences,
                   @NonNull List<Integer> ominousItemCooldowns, @NonNull List<Integer> ominousItemActivationDelays) {
        /** 기본값 */
        public static final Ominous DEFAULT = new Ominous(Optional.empty(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList());
        /** JSON 코덱 */
        public static final Codec<Ominous> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(OverrideLootTable.CODEC.optionalFieldOf("override_loot_table").forGetter(Ominous::overrideLootTable),
                        ExtraCodecs.NON_NEGATIVE_INT.listOf().optionalFieldOf("experiences", DEFAULT.experiences)
                                .forGetter(Ominous::experiences),
                        ExtraCodecs.NON_NEGATIVE_INT.listOf().optionalFieldOf("ominous_item_cooldowns", DEFAULT.ominousItemCooldowns)
                                .forGetter(Ominous::ominousItemCooldowns),
                        ExtraCodecs.NON_NEGATIVE_INT.listOf()
                                .optionalFieldOf("ominous_item_activation_delays", DEFAULT.ominousItemActivationDelays)
                                .forGetter(Ominous::ominousItemActivationDelays))
                .apply(instance, Ominous::new));

        /**
         * 노획물 테이블 덮어쓰기 설정 클래스.
         *
         * @param lootTableResourceKey 대상 노획물 테이블 리소스 키
         * @param chances              흉조 레벨별 확률 목록
         */
        public record OverrideLootTable(@NonNull ResourceKey<LootTable> lootTableResourceKey, @NonNull List<Float> chances) {
            /** JSON 코덱 */
            public static final Codec<OverrideLootTable> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(LootTable.KEY_CODEC.fieldOf("data").forGetter(OverrideLootTable::lootTableResourceKey),
                            ExtraCodecs.floatRange(0, 1).listOf().fieldOf("chances").forGetter(OverrideLootTable::chances))
                    .apply(instance, OverrideLootTable::new));
        }
    }
}
