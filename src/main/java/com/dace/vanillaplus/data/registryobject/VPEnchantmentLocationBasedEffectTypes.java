package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.enchantment.effect.HealEntity;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 마법 부여의 위치 기반 효과 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class VPEnchantmentLocationBasedEffectTypes {
    private static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE);

    static {
        REGISTRY.register("heal_entity", () -> HealEntity.TYPED_CODEC);
    }
}
