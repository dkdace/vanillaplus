package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 엔티티의 요소를 수정하는 엔티티 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityModifier implements DataModifier<EntityType<?>>, CodecUtil.CodecComponent<EntityModifier> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends EntityModifier>> CODEC_REGISTRY = VPRegistry.ENTITY_MODIFIER.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<EntityModifier> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);
    /** JSON 코덱 */
    private static final MapCodec<EntityModifier> CODEC = MapCodec.unit(new EntityModifier());

    static {
        CODEC_REGISTRY.register("entity", () -> CODEC);
        CODEC_REGISTRY.register("living_entity", () -> LivingEntityModifier.CODEC);
        CODEC_REGISTRY.register("crossbow_attack_mob", () -> CrossbowAttackMobModifier.CODEC);
        CODEC_REGISTRY.register("ravager", () -> RavagerModifier.CODEC);
        CODEC_REGISTRY.register("ender_dragon", () -> EnderDragonModifier.CODEC);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ENTITY_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    /**
     * 지정한 엔티티 타입에 해당하는 엔티티 수정자를 반환한다.
     *
     * @param entityType 엔티티 타입
     * @param <T>        {@link EntityModifier}를 상속받는 엔티티 수정자
     * @return 엔티티 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends EntityModifier> T fromEntityType(@NonNull EntityType<?> entityType) {
        return (T) VPRegistry.ENTITY_MODIFIER.getValue(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath());
    }

    /**
     * 지정한 엔티티 타입에 해당하는 엔티티 수정자를 반환한다.
     *
     * @param entityType 엔티티 타입
     * @return 엔티티 수정자
     * @throws IllegalStateException 해당하는 엔티티 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends EntityModifier> T fromEntityTypeOrThrow(@NonNull EntityType<?> entityType) {
        return (T) VPRegistry.ENTITY_MODIFIER.getValueOrThrow(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath());
    }

    @Override
    @NonNull
    public MapCodec<? extends EntityModifier> getCodec() {
        return CODEC;
    }

    /**
     * 엔티티의 체력 비례 수치를 나타내는 클래스.
     *
     * @param <T> 숫자 타입
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HealthBasedValue<T extends Number> {
        /** 최솟값 */
        private final T min;
        /** 최댓값 */
        private final T max;
        /** 난이도 기반 여부 */
        private final boolean isDifficultyBased;

        private HealthBasedValue(T value) {
            this.min = value;
            this.max = value;
            this.isDifficultyBased = true;
        }

        /**
         * JSON 코덱을 생성하여 반환한다.
         *
         * @param floatCodec 사용할 숫자 형식 코덱
         * @param <T>        숫자 타입
         * @return JSON 코덱
         */
        @NonNull
        private static <T extends Number> Codec<HealthBasedValue<T>> codec(@NonNull Codec<T> floatCodec) {
            return RecordCodecBuilder.create(instance -> instance
                    .group(floatCodec.fieldOf("min").forGetter(healthBasedValue -> healthBasedValue.min),
                            floatCodec.fieldOf("max").forGetter(healthBasedValue -> healthBasedValue.max),
                            Codec.BOOL.optionalFieldOf("difficulty_based", true)
                                    .forGetter(healthBasedInt -> healthBasedInt.isDifficultyBased))
                    .apply(instance, HealthBasedValue::new));
        }

        /**
         * 체력 비례 값을 반환한다.
         *
         * @param entity 대상 엔티티
         * @return {@link HealthBasedValue#min} + ({@link LivingEntity#getHealth()} / {@link LivingEntity#getMaxHealth()}) ×
         * ({@link HealthBasedValue#max} - {@link HealthBasedValue#min})
         */
        public T get(@NonNull LivingEntity entity) {
            double value = 0;
            if (isDifficultyBased)
                value = switch (entity.level().getDifficulty()) {
                    case NORMAL -> 0.5F;
                    case HARD -> 0;
                    default -> 1;
                };

            return ObjectUtils.getClass(min)
                    .cast(Mth.clampedLerp(min.doubleValue(), max.doubleValue(), Math.max(value, entity.getHealth() / entity.getMaxHealth())));
        }
    }

    /**
     * {@link LivingEntity}의 엔티티 수정자 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class LivingEntityModifier extends EntityModifier {
        private static final MapCodec<LivingEntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance).apply(instance, LivingEntityModifier::new));

        /** 엔티티 속성 목록 */
        @NonNull
        private final List<AttributeInstance.Packed> packedAttributes;

        @NonNull
        private static <T extends LivingEntityModifier> Products.P1<RecordCodecBuilder.Mu<T>, List<AttributeInstance.Packed>> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
            return instance.group(AttributeInstance.Packed.LIST_CODEC.optionalFieldOf("attributes", Collections.emptyList())
                    .forGetter(LivingEntityModifier::getPackedAttributes));
        }

        @Override
        @NonNull
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link CrossbowAttackMob}의 엔티티 수정자 클래스.
     */
    @Getter
    public static final class CrossbowAttackMobModifier extends LivingEntityModifier {
        private static final MapCodec<CrossbowAttackMobModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                LivingEntityModifier.createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power", 1.6F)
                                        .forGetter(CrossbowAttackMobModifier::getShootingPower),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shooting_range", 8)
                                        .forGetter(CrossbowAttackMobModifier::getShootingRange)))
                        .apply(instance, CrossbowAttackMobModifier::new));

        /** 화살 발사 속력 */
        private final float shootingPower;
        /** 공격 거리 */
        private final int shootingRange;

        private CrossbowAttackMobModifier(@NonNull List<AttributeInstance.Packed> packedAttributes, float shootingPower, int shootingRange) {
            super(packedAttributes);

            this.shootingPower = shootingPower;
            this.shootingRange = shootingRange;
        }

        @Override
        @NonNull
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link Ravager}의 엔티티 수정자 클래스.
     */
    public static final class RavagerModifier extends LivingEntityModifier {
        private static final MapCodec<RavagerModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                LivingEntityModifier.createBaseCodec(instance)
                        .and(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("roar_cooldown_seconds", 10F)
                                .forGetter(ravagerModifier -> ravagerModifier.roarCooldownSeconds))
                        .apply(instance, RavagerModifier::new));

        /** 포효 쿨타임 (초) */
        private final float roarCooldownSeconds;

        private RavagerModifier(@NonNull List<AttributeInstance.Packed> packedAttributes, float roarCooldownSeconds) {
            super(packedAttributes);
            this.roarCooldownSeconds = roarCooldownSeconds;
        }

        /**
         * @return 포효 쿨타임 (tick)
         */
        public int getRoarCooldown() {
            return (int) (roarCooldownSeconds * 20.0);
        }

        @Override
        @NonNull
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link EnderDragon}의 엔티티 수정자 클래스.
     */
    @Getter
    public static final class EnderDragonModifier extends LivingEntityModifier {
        private static final MapCodec<EnderDragonModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                LivingEntityModifier.createBaseCodec(instance)
                        .and(instance.group(Experience.CODEC.fieldOf("experience").forGetter(EnderDragonModifier::getExperience),
                                HealthBasedValue.codec(ExtraCodecs.NON_NEGATIVE_FLOAT)
                                        .optionalFieldOf("movement_speed_multiplier", new HealthBasedValue<>(1F))
                                        .forGetter(EnderDragonModifier::getMovementSpeedMultiplier),
                                PhaseInfo.CODEC.fieldOf("phases").forGetter(EnderDragonModifier::getPhaseInfo),
                                ExtraCodecs.floatRange(0, 1).optionalFieldOf("ender_pearl_drop_chance", 0.1F)
                                        .forGetter(EnderDragonModifier::getEnderPearlDropChance),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_ender_pearl_drops", 20)
                                        .forGetter(EnderDragonModifier::getMaxEnderPearlDrops)))
                        .apply(instance, EnderDragonModifier::new));

        /** 드롭 경험치 정보 */
        @NonNull
        private final Experience experience;
        /** 이동속도 배수 */
        @NonNull
        private final HealthBasedValue<Float> movementSpeedMultiplier;
        /** 페이즈 정보 */
        @NonNull
        private final PhaseInfo phaseInfo;
        /** 피격 시 엔더 진주 드롭 확률 */
        private final float enderPearlDropChance;
        /** 최대 엔더 진주 드롭 횟수 */
        private final int maxEnderPearlDrops;

        private EnderDragonModifier(@NonNull List<AttributeInstance.Packed> packedAttributes, @NonNull Experience experience,
                                    @NonNull HealthBasedValue<Float> movementSpeedMultiplier, @NonNull PhaseInfo phaseInfo, float enderPearlDropChance,
                                    int maxEnderPearlDrops) {
            super(packedAttributes);

            this.experience = experience;
            this.movementSpeedMultiplier = movementSpeedMultiplier;
            this.phaseInfo = phaseInfo;
            this.enderPearlDropChance = enderPearlDropChance;
            this.maxEnderPearlDrops = maxEnderPearlDrops;
        }

        @Override
        @NonNull
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
        }

        /**
         * 드롭 경험치 정보 클래스.
         */
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        public static final class Experience {
            /** JSON 코덱 */
            private static final Codec<Experience> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("first", 12000).forGetter(Experience::getFirst),
                            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("second", 500).forGetter(Experience::getSecond))
                    .apply(instance, Experience::new));

            /** 1회차 */
            private final int first;
            /** 2회차 이후 */
            private final int second;
        }

        /**
         * 페이즈 정보 클래스.
         */
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        public static final class PhaseInfo {
            /** JSON 코덱 */
            private static final Codec<PhaseInfo> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(HealthBasedValue.codec(ExtraCodecs.POSITIVE_FLOAT)
                                    .optionalFieldOf("attack_cooldown_seconds", new HealthBasedValue<>(16F))
                                    .forGetter(PhaseInfo::getAttackCooldownSeconds),
                            HealthBasedValue.codec(ExtraCodecs.floatRange(0, 1))
                                    .optionalFieldOf("landing_chance", new HealthBasedValue<>(0.3F)).forGetter(PhaseInfo::getLandingChance),
                            Charge.CODEC.fieldOf("charge").forGetter(PhaseInfo::getCharge),
                            Fireball.CODEC.fieldOf("fireball").forGetter(PhaseInfo::getFireball),
                            Sitting.CODEC.fieldOf("sitting").forGetter(PhaseInfo::getSitting),
                            Meteor.CODEC.fieldOf("meteor").forGetter(PhaseInfo::getMeteor))
                    .apply(instance, PhaseInfo::new));

            /** 공격 쿨타임 (초) */
            @NonNull
            private final HealthBasedValue<Float> attackCooldownSeconds;
            /** 순환을 마친 후 착지할 확률 */
            @NonNull
            private final HealthBasedValue<Float> landingChance;
            /** 돌진 공격 정보 */
            @NonNull
            private final Charge charge;
            /** 화염구 공격 정보 */
            @NonNull
            private final Fireball fireball;
            /** 앉은 상태의 페이즈 정보 */
            @NonNull
            private final Sitting sitting;
            /** 운석 공격 정보 */
            @NonNull
            private final Meteor meteor;

            /**
             * 돌진 공격 정보 클래스.
             */
            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            public static final class Charge {
                /** JSON 코덱 */
                private static final Codec<Charge> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("flame_duration_seconds", 5F)
                                        .forGetter(target -> target.flameDurationSeconds),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("flame_radius", 4F).forGetter(Charge::getFlameRadius))
                        .apply(instance, Charge::new));

                /** 브레스 잔류 시간 (초) */
                private final float flameDurationSeconds;
                /** 브레스 범위 */
                @Getter
                private final float flameRadius;

                /**
                 * @return 브레스 잔류 시간 (tick)
                 */
                public int getFlameDuration() {
                    return (int) (flameDurationSeconds * 20.0);
                }
            }

            /**
             * 화염구 공격 정보 클래스.
             */
            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            public static final class Fireball {
                /** JSON 코덱 */
                private static final Codec<Fireball> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("velocity_multiplier", 1.5F)
                                        .forGetter(Fireball::getVelocityMultiplier),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("explosion_radius", 2F)
                                        .forGetter(Fireball::getExplosionRadius),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("flame_duration_seconds", 10F)
                                        .forGetter(target -> target.flameDurationSeconds),
                                HealthBasedValue.codec(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max_shots", new HealthBasedValue<>(1))
                                        .forGetter(Fireball::getMaxShots))
                        .apply(instance, Fireball::new));

                /** 속력 배수 */
                @Getter
                private final float velocityMultiplier;
                /** 폭발 범위 */
                @Getter
                private final float explosionRadius;
                /** 브레스 잔류 시간 (초) */
                private final float flameDurationSeconds;
                /** 최대 발사 횟수 */
                @NonNull
                @Getter
                private final HealthBasedValue<Integer> maxShots;

                /**
                 * @return 브레스 잔류 시간 (tick)
                 */
                public int getFlameDuration() {
                    return (int) (flameDurationSeconds * 20.0);
                }
            }

            /**
             * 앉은 상태의 페이즈 정보 클래스.
             */
            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            public static final class Sitting {
                /** JSON 코덱 */
                private static final Codec<Sitting> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("scan_duration", 0.7F)
                                        .forGetter(target -> target.scanDurationSeconds),
                                HealthBasedValue.codec(ExtraCodecs.POSITIVE_FLOAT)
                                        .optionalFieldOf("scan_idle_duration_seconds", new HealthBasedValue<>(3F))
                                        .forGetter(Sitting::getScanIdleDurationSeconds),
                                ExtraCodecs.floatRange(0, 1).optionalFieldOf("allowed_damage_ratio", 0.15F)
                                        .forGetter(Sitting::getAllowedDamageRatio),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("spin_attack_duration_seconds", 0.8F)
                                        .forGetter(target -> target.spinAttackDurationSeconds),
                                HealthBasedValue.codec(ExtraCodecs.POSITIVE_FLOAT)
                                        .optionalFieldOf("flaming_duration_seconds", new HealthBasedValue<>(3F))
                                        .forGetter(Sitting::getFlamingDurationSeconds),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("flame_radius", 6F).forGetter(Sitting::getFlameRadius))
                        .apply(instance, Sitting::new));

                /** 대상 탐지 시간 (초) */
                private final float scanDurationSeconds;
                /** 대상 탐지 중 정지 시간 (초) */
                @NonNull
                @Getter
                private final HealthBasedValue<Float> scanIdleDurationSeconds;
                /** 받을 수 있는 최대 피해 비율 */
                @Getter
                private final float allowedDamageRatio;
                /** 회전 공격 시간 (초) */
                private final float spinAttackDurationSeconds;
                /** 브레스 사용 시간 (초) */
                @NonNull
                @Getter
                private final HealthBasedValue<Float> flamingDurationSeconds;
                @Getter
                private final float flameRadius;

                /**
                 * @return 대상 탐지 시간 (tick)
                 */
                public int getScanDuration() {
                    return (int) (spinAttackDurationSeconds * 20.0);
                }

                /**
                 * @return 회전 공격 시간 (tick)
                 */
                public int getSpinAttackDuration() {
                    return (int) (spinAttackDurationSeconds * 20.0);
                }
            }

            /**
             * 운석 공격 정보 클래스.
             */
            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            @Getter
            public static final class Meteor {
                /** JSON 코덱 */
                private static final Codec<Meteor> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("velocity", 3).forGetter(Meteor::getVelocity),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("explosion_radius", 3.5F)
                                        .forGetter(Meteor::getExplosionRadius),
                                HealthBasedValue.codec(ExtraCodecs.POSITIVE_FLOAT)
                                        .optionalFieldOf("cooldown_seconds", new HealthBasedValue<>(30F))
                                        .forGetter(Meteor::getCooldownSeconds))
                        .apply(instance, Meteor::new));

                /** 추락 속력 */
                private final int velocity;
                /** 폭발 범위 */
                private final float explosionRadius;
                /** 쿨타임 (초) */
                @NonNull
                private final HealthBasedValue<Float> cooldownSeconds;
            }
        }
    }
}
