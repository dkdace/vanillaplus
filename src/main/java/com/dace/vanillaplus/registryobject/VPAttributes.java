package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

/**
 * 모드에서 사용하는 엔티티 속성을 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPAttributes {
    public static final RegistryObject<Attribute> PROJECTILE_KNOCKBACK_RESISTANCE = create("projectile_knockback_resistance",
            new RangedAttribute("attribute.name.projectile_knockback_resistance", 0, 0, 1)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> ENVIRONMENTAL_DAMAGE_RESISTANCE = create("environmental_damage_resistance",
            new RangedAttribute("attribute.name.environmental_damage_resistance", 0, 0, 1)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> FOOD_EXHAUSTION_MULTIPLIER = create("food_exhaustion_multiplier",
            new RangedAttribute("attribute.name.food_exhaustion_multiplier", 1, 0, 1024)
                    .setSyncable(true)
                    .setSentiment(Attribute.Sentiment.NEGATIVE));
    public static final RegistryObject<Attribute> HEARING_RANGE = create("hearing_range",
            new RangedAttribute("attribute.name.hearing_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> ITEM_PICKUP_RANGE = create("item_pickup_range",
            new RangedAttribute("attribute.name.item_pickup_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> BEACON_EFFECT_RANGE = create("beacon_effect_range",
            new RangedAttribute("attribute.name.beacon_effect_range", 1, 1, 10)
                    .setSyncable(true));
    public static final RegistryObject<Attribute> FOG_DISTANCE = create("fog_distance",
            new RangedAttribute("attribute.name.fog_distance", 1, 0, 10)
                    .setSyncable(true));

    @SubscribeEvent
    private static void onEntityAttributeModification(@NonNull EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, PROJECTILE_KNOCKBACK_RESISTANCE.getHolder().orElseThrow());
            event.add(entityType, ENVIRONMENTAL_DAMAGE_RESISTANCE.getHolder().orElseThrow());
            event.add(entityType, FOG_DISTANCE.getHolder().orElseThrow());
        }

        event.add(EntityType.PLAYER, FOOD_EXHAUSTION_MULTIPLIER.getHolder().orElseThrow());
        event.add(EntityType.PLAYER, HEARING_RANGE.getHolder().orElseThrow());
        event.add(EntityType.PLAYER, ITEM_PICKUP_RANGE.getHolder().orElseThrow());
        event.add(EntityType.PLAYER, BEACON_EFFECT_RANGE.getHolder().orElseThrow());
    }

    @SubscribeEvent
    private static void onViewportRenderFog(@NonNull ViewportEvent.RenderFog event) {
        if (!(event.getCamera().getEntity() instanceof LivingEntity livingEntity))
            return;

        float fogDistance = (float) livingEntity.getAttributeValue(VPAttributes.FOG_DISTANCE.getHolder().orElseThrow());
        event.getData().environmentalStart *= fogDistance;
        event.getData().environmentalEnd *= fogDistance;
    }

    @NonNull
    private static RegistryObject<Attribute> create(@NonNull String name, @NonNull Attribute attribute) {
        return VPRegistry.ATTRIBUTE.register(name, () -> attribute);
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
                ? livingEntity.getAttributeValue(VPAttributes.PROJECTILE_KNOCKBACK_RESISTANCE.getHolder().orElseThrow())
                : 0;

        return Math.max(resistance, projectileResistance);
    }
}
