package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 모드에서 사용하는 마법 부여의 엔티티 효과 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPEnchantmentEntityEffectTypes {
    static {
        create("heal_entity", HealEntity.TYPED_CODEC);
    }

    private static void create(@NonNull String name, @NonNull MapCodec<? extends EnchantmentEntityEffect> codec) {
        VPRegistry.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE.register(name, () -> codec);
        VPRegistry.ENCHANTMENT_ENTITY_EFFECT_TYPE.register(name, () -> codec);
    }

    private record HealEntity(@NonNull LevelBasedValue minAmount, @NonNull LevelBasedValue maxAmount) implements EnchantmentEntityEffect {
        private static final MapCodec<HealEntity> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(LevelBasedValue.CODEC.fieldOf("min_amount").forGetter(HealEntity::minAmount),
                        LevelBasedValue.CODEC.fieldOf("max_amount").forGetter(HealEntity::maxAmount))
                .apply(instance, HealEntity::new));

        @Override
        public void apply(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull EnchantedItemInUse enchantedItemInUse,
                          @NonNull Entity entity, @NonNull Vec3 pos) {
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
}
