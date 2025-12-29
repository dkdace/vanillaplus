package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 마법 부여의 효과 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPEnchantmentEffectComponentTypes {
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> XP_MULTIPLIER = create("xp_multiplier",
            builder -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY)
                    .listOf()));
    public static final RegistryObject<DataComponentType<EnchantmentValueEffect>> IRON_GOLEM_HEAL_MULTIPLIER = create("iron_golem_heal_multiplier",
            builder -> builder.persistent(EnchantmentValueEffect.CODEC));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BARTERING_ROLLS = create("bartering_rolls",
            builder -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY)
                    .listOf()));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> TRADING_COST_MULTIPLIER = create("trading_cost_multiplier",
            builder -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY)
                    .listOf()));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> FINAL_INCOMING_DAMAGE_MULTIPLIER = create("final_incoming_damage_multiplier",
            builder -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE)
                    .listOf()));

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return VPRegistry.register(VPRegistry.ENCHANTMENT_EFFECT_COMPONENT_TYPE, name, onBuilder.apply(DataComponentType.builder())::build);
    }
}
