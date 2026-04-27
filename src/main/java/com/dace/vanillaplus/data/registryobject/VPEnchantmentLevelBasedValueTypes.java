package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.enchantment.Multiply;
import com.dace.vanillaplus.world.item.enchantment.Preset;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 마법 부여의 레벨 기반 값 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class VPEnchantmentLevelBasedValueTypes {
    private static final DeferredRegister<MapCodec<? extends LevelBasedValue>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE);

    static {
        REGISTRY.register("preset", () -> Preset.TYPED_CODEC);
        REGISTRY.register("multiply", () -> Multiply.TYPED_CODEC);
    }
}
