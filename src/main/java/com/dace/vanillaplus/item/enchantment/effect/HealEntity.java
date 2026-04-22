package com.dace.vanillaplus.item.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 엔티티를 치유하는 마법 부여의 엔티티 효과 클래스.
 *
 * @param minAmount 최소 치유량
 * @param maxAmount 최대 치유량
 */
public record HealEntity(@NonNull LevelBasedValue minAmount, @NonNull LevelBasedValue maxAmount) implements EnchantmentEntityEffect {
    /** JSON 코덱 */
    public static final MapCodec<HealEntity> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("min_amount").forGetter(HealEntity::minAmount),
                    LevelBasedValue.CODEC.fieldOf("max_amount").forGetter(HealEntity::maxAmount))
            .apply(instance, HealEntity::new));

    @Override
    public void apply(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull EnchantedItemInUse item, @NonNull Entity entity,
                      @NonNull Vec3 position) {
        if (entity instanceof LivingEntity livingEntity)
            livingEntity.heal(Mth.randomBetween(serverLevel.getRandom(), minAmount.calculate(enchantmentLevel),
                    maxAmount.calculate(enchantmentLevel)));
    }

    @Override
    @NonNull
    public MapCodec<HealEntity> codec() {
        return TYPED_CODEC;
    }
}
