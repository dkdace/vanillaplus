package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 아이템의 요소를 수정하는 아이템 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ItemModifier implements CodecUtil.CodecComponent<ItemModifier> {
    /** JSON 코덱 */
    public static final MapCodec<ItemModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, ItemModifier::new));

    /** 아이템 데이터 요소 */
    @NonNull
    private final DataComponentPatch dataComponentPatch;
    /** 아이템 속성 수정자 목록 */
    @NonNull
    private final ItemAttributeModifiers itemAttributeModifiers;

    @NonNull
    private static <T extends ItemModifier> Products.P2<RecordCodecBuilder.Mu<T>, DataComponentPatch, ItemAttributeModifiers> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                        .forGetter(ItemModifier::getDataComponentPatch),
                ItemAttributeModifiers.CODEC.optionalFieldOf("attribute_modifiers", ItemAttributeModifiers.EMPTY)
                        .forGetter(ItemModifier::getItemAttributeModifiers));
    }

    @Override
    @NonNull
    public MapCodec<? extends ItemModifier> getCodec() {
        return CODEC;
    }

    /**
     * {@link Items#ELYTRA}의 아이템 수정자 클래스.
     */
    @Getter
    public static final class ElytraModifier extends ItemModifier {
        public static final MapCodec<ElytraModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
                .and(instance.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("firework_add_speed_multiplier", 1.5F)
                                .forGetter(ElytraModifier::getFireworkAddSpeedMultiplier),
                        ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("firework_final_speed_multiplier", 0.5F)
                                .forGetter(ElytraModifier::getFireworkFinalSpeedModifier)))
                .apply(instance, ElytraModifier::new));

        /** 폭죽의 추가 속도 배수 */
        private final float fireworkAddSpeedMultiplier;
        /** 폭죽의 최종 속도 배수 */
        private final float fireworkFinalSpeedModifier;

        private ElytraModifier(@NonNull DataComponentPatch dataComponentPatch, @NonNull ItemAttributeModifiers itemAttributeModifiers,
                               float fireworkAddSpeedMultiplier, float fireworkFinalSpeedModifier) {
            super(dataComponentPatch, itemAttributeModifiers);

            this.fireworkAddSpeedMultiplier = fireworkAddSpeedMultiplier;
            this.fireworkFinalSpeedModifier = fireworkFinalSpeedModifier;
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link ProjectileWeaponItem}의 아이템 수정자 클래스.
     */
    @Getter
    public static class ProjectileWeaponModifier extends ItemModifier {
        public static final MapCodec<ProjectileWeaponModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance).apply(instance, ProjectileWeaponModifier::new));

        /** 화살 피해 배수 */
        private final float baseDamage;
        /** 화살 발사 속력 */
        private final float shootingPower;

        private ProjectileWeaponModifier(@NonNull DataComponentPatch dataComponentPatch, @NonNull ItemAttributeModifiers itemAttributeModifiers,
                                         float baseDamage, float shootingPower) {
            super(dataComponentPatch, itemAttributeModifiers);

            this.baseDamage = baseDamage;
            this.shootingPower = shootingPower;
        }

        @NonNull
        private static <T extends ProjectileWeaponModifier> Products.P4<RecordCodecBuilder.Mu<T>, DataComponentPatch, ItemAttributeModifiers, Float, Float> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
            return ItemModifier.createBaseCodec(instance)
                    .and(instance.group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("base_damage", 2.5F)
                                    .forGetter(ProjectileWeaponModifier::getBaseDamage),
                            ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power", 2.5F)
                                    .forGetter(ProjectileWeaponModifier::getShootingPower)));
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link Items#CROSSBOW}의 아이템 수정자 클래스.
     */
    @Getter
    public static final class CrossbowModifier extends ProjectileWeaponModifier {
        public static final MapCodec<CrossbowModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                ProjectileWeaponModifier.createBaseCodec(instance)
                        .and(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power_firework_rocket", 1.6F)
                                .forGetter(CrossbowModifier::getShootingPowerFireworkRocket))
                        .apply(instance, CrossbowModifier::new));

        /** 폭죽 발사 속력 */
        private final float shootingPowerFireworkRocket;

        private CrossbowModifier(@NonNull DataComponentPatch dataComponentPatch, @NonNull ItemAttributeModifiers itemAttributeModifiers,
                                 float baseDamage, float shootingPower, float shootingPowerFireworkRocket) {
            super(dataComponentPatch, itemAttributeModifiers, baseDamage, shootingPower);
            this.shootingPowerFireworkRocket = shootingPowerFireworkRocket;
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link TridentItem}의 아이템 수정자 클래스.
     */
    public static final class TridentModifier extends ItemModifier {
        public static final MapCodec<TridentModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
                .and(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("riptide_cooldown_seconds", 3F)
                        .forGetter(target -> target.riptideCooldownSeconds))
                .apply(instance, TridentModifier::new));

        /** 급류 돌진 시 쿨타임 (초) */
        private final float riptideCooldownSeconds;

        private TridentModifier(@NonNull DataComponentPatch dataComponentPatch, @NonNull ItemAttributeModifiers itemAttributeModifiers,
                                float riptideCooldownSeconds) {
            super(dataComponentPatch, itemAttributeModifiers);
            this.riptideCooldownSeconds = riptideCooldownSeconds;
        }

        /**
         * @return 회전 공격 시간 (tick)
         */
        public int getRiptideCooldown() {
            return (int) (riptideCooldownSeconds * 20.0);
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link InstrumentItem}의 아이템 수정자 클래스.
     */
    public static final class InstrumentModifier extends ItemModifier {
        public static final MapCodec<InstrumentModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("use_duration_seconds", 1.5F)
                                .forGetter(target -> target.useDurationSeconds))
                        .apply(instance, InstrumentModifier::new));

        /** 지속시간 (초) */
        private final float useDurationSeconds;

        private InstrumentModifier(@NonNull DataComponentPatch dataComponentPatch, @NonNull ItemAttributeModifiers itemAttributeModifiers,
                                   float useDurationSeconds) {
            super(dataComponentPatch, itemAttributeModifiers);
            this.useDurationSeconds = useDurationSeconds;
        }

        /**
         * @return 지속시간 (tick)
         */
        public int getUseDuration() {
            return (int) (useDurationSeconds * 20.0);
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }
}
