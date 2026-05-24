package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.Optional;

/**
 * {@link ProjectileWeaponItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param baseDamage    화살 피해 배수
 * @param shootingPower 화살 발사 속력
 */
public record ProjectileWeaponConfig(@NonNull Optional<Float> baseDamage, @NonNull Optional<Float> shootingPower) {
    /** 기본값 */
    public static final ProjectileWeaponConfig DEFAULT = new ProjectileWeaponConfig(Optional.empty(), Optional.empty());
    /** JSON 코덱 */
    public static final Codec<ProjectileWeaponConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("base_damage").forGetter(ProjectileWeaponConfig::baseDamage),
                    ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power").forGetter(ProjectileWeaponConfig::shootingPower))
            .apply(instance, ProjectileWeaponConfig::new));

    @NonNull
    public static ProjectileWeaponConfig get(@NonNull Item item) {
        return VPItem.cast(item).getConfigComponents().getOrDefault(ItemConfigComponentTypes.PROJECTILE_WEAPON, DEFAULT);
    }
}
