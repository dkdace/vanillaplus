package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

/**
 * 모드에서 사용하는 엔티티 속성을 관리하는 클래스.
 */
@UtilityClass
public final class VPAttributes {
    private static final DeferredRegister<Attribute> REGISTRY = StaticRegistry.createDeferredRegister(Registries.ATTRIBUTE);

    public static final RegistryObject<Attribute> PROJECTILE_KNOCKBACK_RESISTANCE = createAll("projectile_knockback_resistance",
            new RangedAttribute("attribute.name.projectile_knockback_resistance", 0, 0, 1)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> ENVIRONMENTAL_DAMAGE_RESISTANCE = createAll("environmental_damage_resistance",
            new RangedAttribute("attribute.name.environmental_damage_resistance", 0, 0, 1)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> FOOD_EXHAUSTION_MULTIPLIER = createPlayer("food_exhaustion_multiplier",
            new RangedAttribute("attribute.name.food_exhaustion_multiplier", 1, 0, 1024)
                    .setSyncable(true)
                    .setSentiment(Attribute.Sentiment.NEGATIVE));
    public static final RegistryObject<Attribute> HEARING_RANGE = createPlayer("hearing_range",
            new RangedAttribute("attribute.name.hearing_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> ITEM_PICKUP_RANGE = createPlayer("item_pickup_range",
            new RangedAttribute("attribute.name.item_pickup_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> BEACON_EFFECT_RANGE = createPlayer("beacon_effect_range",
            new RangedAttribute("attribute.name.beacon_effect_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> FOG_DISTANCE = createAll("fog_distance",
            new RangedAttribute("attribute.name.fog_distance", 1, 0, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> SPRINTING_SPEED = createAll("sprinting_speed",
            new RangedAttribute("attribute.name.sprinting_speed", 0.3, 0, 1024)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> HEAL_MULTIPLIER = createAll("heal_multiplier",
            new RangedAttribute("attribute.name.heal_multiplier", 1, 0, 1024)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> VIBRATION_TRANSMIT_RANGE = createAll("vibration_transmit_range",
            new RangedAttribute("attribute.name.vibration_transmit_range", 1, 0, 4)
                    .setSyncable(true)
                    .setSentiment(Attribute.Sentiment.NEUTRAL));
    public static final RegistryObject<Attribute> ELYTRA_FLYING_SPEED_MULTIPLIER = createAll("elytra_flying_speed_multiplier",
            new RangedAttribute("attribute.name.elytra_flying_speed_multiplier", 1, 0, 1024)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> VEHICLE_SPEED_MULTIPLIER = createAll("vehicle_speed_multiplier",
            new RangedAttribute("attribute.name.vehicle_speed_multiplier", 1, 0, 1024)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> EATING_TIME = createAll("eating_time",
            new RangedAttribute("attribute.name.eating_time", 1, 0, 1024)
                    .setSyncable(true)
                    .setSentiment(Attribute.Sentiment.NEGATIVE));
    public static final RegistryObject<Attribute> ATTACK_REACH_MULTIPLIER = createAll("attack_reach_multiplier",
            new RangedAttribute("attribute.name.attack_reach_multiplier", 1, 0, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> SWEEPING_RANGE = createPlayer("sweeping_range",
            new RangedAttribute("attribute.name.sweeping_range", 0, 0, 10)
                    .setSyncable(true));

    @NonNull
    private static RegistryObject<Attribute> createAll(@NonNull String name, @NonNull Attribute attribute) {
        RegistryObject<Attribute> registryObject = REGISTRY.register(name, () -> attribute);

        EntityAttributeModificationEvent.BUS.addListener(event -> {
            for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
                event.add(entityType, registryObject.getHolder().orElseThrow());
            }
        });

        return registryObject;
    }

    @NonNull
    private static RegistryObject<Attribute> createPlayer(@NonNull String name, @NonNull Attribute attribute) {
        RegistryObject<Attribute> registryObject = REGISTRY.register(name, () -> attribute);

        EntityAttributeModificationEvent.BUS.addListener(event ->
                event.add(EntityType.PLAYER, registryObject.getHolder().orElseThrow()));

        return registryObject;
    }

    /**
     * 엔티티의 최종 밀치기 저항 수치를 반환한다.
     *
     * @param livingEntity 대상 엔티티
     * @param damageSource 피해 근원
     * @return 밀치기 저항. 0~1 사이의 값
     */
    public static double getFinalKnockbackResistance(@NonNull LivingEntity livingEntity, @Nullable DamageSource damageSource) {
        double resistance = livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double projectileResistance = damageSource != null && damageSource.is(DamageTypeTags.IS_PROJECTILE)
                ? livingEntity.getAttributeValue(PROJECTILE_KNOCKBACK_RESISTANCE.getHolder().orElseThrow())
                : 0;

        return Math.max(resistance, projectileResistance);
    }
}
