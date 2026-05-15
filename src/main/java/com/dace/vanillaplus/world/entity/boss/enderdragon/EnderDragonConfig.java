package com.dace.vanillaplus.world.entity.boss.enderdragon;

import com.dace.vanillaplus.util.CodecUtil;
import com.dace.vanillaplus.world.entity.HealthBasedValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import java.util.Optional;

/**
 * {@link EnderDragon}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param enderPearlDropChance    피격 시 엔더 진주 드롭 확률
 * @param maxEnderPearlDrops      최대 엔더 진주 드롭 횟수
 * @param endermiteCount          엔더 진주 드롭 시 생성되는 엔더마이트 수
 * @param experience              드롭 경험치 정보
 * @param movementSpeedMultiplier 이동속도 배수
 * @param phaseInfo               페이즈 정보
 */
public record EnderDragonConfig(float enderPearlDropChance, int maxEnderPearlDrops, int endermiteCount, @NonNull Optional<Experience> experience,
                                @NonNull Optional<HealthBasedValue<Float>> movementSpeedMultiplier, @NonNull Optional<PhaseInfo> phaseInfo) {
    /** 기본값 */
    public static final EnderDragonConfig DEFAULT = new EnderDragonConfig(0, 0, 0,
            Optional.empty(), Optional.empty(), Optional.empty());
    /** JSON 코덱 */
    public static final Codec<EnderDragonConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.floatRange(0, 1).fieldOf("ender_pearl_drop_chance").forGetter(EnderDragonConfig::enderPearlDropChance),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_ender_pearl_drops").forGetter(EnderDragonConfig::maxEnderPearlDrops),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("endermite_count").forGetter(EnderDragonConfig::endermiteCount),
                    Experience.CODEC.optionalFieldOf("experience").forGetter(EnderDragonConfig::experience),
                    HealthBasedValue.createCodec(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("movement_speed_multiplier")
                            .forGetter(EnderDragonConfig::movementSpeedMultiplier),
                    PhaseInfo.CODEC.optionalFieldOf("phases").forGetter(EnderDragonConfig::phaseInfo))
            .apply(instance, EnderDragonConfig::new));

    /**
     * 드롭 경험치 정보 클래스.
     *
     * @param first  1회차
     * @param second 2회차 이후
     */
    public record Experience(int first, int second) {
        private static final Codec<Experience> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("first").forGetter(Experience::first),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("second").forGetter(Experience::second))
                .apply(instance, Experience::new));
    }

    /**
     * 페이즈 정보 클래스.
     *
     * @param attackCooldown 공격 쿨타임
     * @param landingChance  순환을 마친 후 착지할 확률
     * @param charge         돌진 공격 정보
     * @param fireball       화염구 공격 정보
     * @param sitting        앉은 상태의 페이즈 정보
     * @param meteor         운석 공격 정보
     */
    public record PhaseInfo(@NonNull HealthBasedValue<Integer> attackCooldown, @NonNull HealthBasedValue<Float> landingChance, @NonNull Charge charge,
                            @NonNull Fireball fireball, @NonNull Sitting sitting, @NonNull Meteor meteor) {
        private static final Codec<PhaseInfo> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(HealthBasedValue.createCodec(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT)).fieldOf("attack_cooldown_seconds")
                                .forGetter(PhaseInfo::attackCooldown),
                        HealthBasedValue.createCodec(ExtraCodecs.floatRange(0, 1)).fieldOf("landing_chance").forGetter(PhaseInfo::landingChance),
                        Charge.CODEC.fieldOf("charge").forGetter(PhaseInfo::charge),
                        Fireball.CODEC.fieldOf("fireball").forGetter(PhaseInfo::fireball),
                        Sitting.CODEC.fieldOf("sitting").forGetter(PhaseInfo::sitting),
                        Meteor.CODEC.fieldOf("meteor").forGetter(PhaseInfo::meteor))
                .apply(instance, PhaseInfo::new));

        /**
         * 돌진 공격 정보 클래스.
         *
         * @param flameDuration 브레스 잔류 시간
         * @param flameRadius   브레스 범위
         */
        public record Charge(int flameDuration, float flameRadius) {
            private static final Codec<Charge> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).fieldOf("flame_duration_seconds")
                                    .forGetter(Charge::flameDuration),
                            ExtraCodecs.POSITIVE_FLOAT.fieldOf("flame_radius").forGetter(Charge::flameRadius))
                    .apply(instance, Charge::new));
        }

        /**
         * 화염구 공격 정보 클래스.
         *
         * @param velocityMultiplier 속력 배수
         * @param explosionRadius    폭발 범위
         * @param flameDuration      브레스 잔류 시간
         * @param maxShots           최대 발사 횟수
         */
        public record Fireball(float velocityMultiplier, float explosionRadius, int flameDuration, @NonNull HealthBasedValue<Integer> maxShots) {
            private static final Codec<Fireball> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("velocity_multiplier", 1.5F)
                                    .forGetter(Fireball::velocityMultiplier),
                            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("explosion_radius", 2F).forGetter(Fireball::explosionRadius),
                            CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).fieldOf("flame_duration_seconds")
                                    .forGetter(Fireball::flameDuration),
                            HealthBasedValue.createCodec(ExtraCodecs.POSITIVE_INT).fieldOf("max_shots").forGetter(Fireball::maxShots))
                    .apply(instance, Fireball::new));
        }

        /**
         * 앉은 상태의 페이즈 정보 클래스.
         *
         * @param scanDuration       대상 탐지 시간
         * @param scanIdleDuration   대상 탐지 중 정지 시간
         * @param allowedDamageRatio 받을 수 있는 최대 피해 비율
         * @param spinAttackDuration 회전 공격 시간
         * @param flamingDuration    브레스 사용 시간
         * @param flameRadius        브레스 범위
         */
        public record Sitting(int scanDuration, @NonNull HealthBasedValue<Integer> scanIdleDuration, float allowedDamageRatio,
                              int spinAttackDuration, @NonNull HealthBasedValue<Integer> flamingDuration, float flameRadius) {
            private static final Codec<Sitting> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).fieldOf("scan_duration_seconds")
                                    .forGetter(Sitting::scanDuration),
                            HealthBasedValue.createCodec(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT))
                                    .fieldOf("scan_idle_duration_seconds").forGetter(Sitting::scanIdleDuration),
                            ExtraCodecs.floatRange(0, 1).fieldOf("allowed_damage_ratio").forGetter(Sitting::allowedDamageRatio),
                            CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).fieldOf("spin_attack_duration_seconds")
                                    .forGetter(Sitting::spinAttackDuration),
                            HealthBasedValue.createCodec(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT))
                                    .fieldOf("flaming_duration_seconds").forGetter(Sitting::flamingDuration),
                            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("flame_radius", 6F).forGetter(Sitting::flameRadius))
                    .apply(instance, Sitting::new));
        }

        /**
         * 운석 공격 정보 클래스.
         *
         * @param velocity        추락 속력
         * @param explosionRadius 폭발 범위
         * @param cooldown        쿨타임
         */
        public record Meteor(int velocity, float explosionRadius, @NonNull HealthBasedValue<Integer> cooldown) {
            private static final Codec<Meteor> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("velocity").forGetter(Meteor::velocity),
                            ExtraCodecs.POSITIVE_FLOAT.fieldOf("explosion_radius").forGetter(Meteor::explosionRadius),
                            HealthBasedValue.createCodec(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT)).fieldOf("cooldown_seconds")
                                    .forGetter(Meteor::cooldown))
                    .apply(instance, Meteor::new));
        }
    }
}
