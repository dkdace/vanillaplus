package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 마법 부여의 효과 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPEnchantmentEffectComponentTypes {
    public static final RegistryObject<DataComponentType<EnchantmentValueEffect>> REPAIR_BONUS = create("repair_bonus",
            builder -> builder.persistent(EnchantmentValueEffect.CODEC));

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return VPRegistry.register(VPRegistry.ENCHANTMENT_EFFECT_COMPONENT_TYPE, name, onBuilder.apply(DataComponentType.builder())::build);
    }
}
