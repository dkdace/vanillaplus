package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

/**
 * 모드에서 사용하는 엔티티 속성을 관리하는 클래스.
 */
@UtilityClass
public final class VPAttributes {
    public static final RegistryObject<Attribute> PROJECTILE_KNOCKBACK_RESISTANCE = create("projectile_knockback_resistance",
            new RangedAttribute("attribute.name.projectile_knockback_resistance", 0, 0, 1).setSyncable(true));

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
