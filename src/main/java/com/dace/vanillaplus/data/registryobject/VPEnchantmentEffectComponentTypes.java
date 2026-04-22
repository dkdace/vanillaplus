package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 마법 부여의 효과 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPEnchantmentEffectComponentTypes {
    public static final ContextKeySet CONTEXT_KEY_SET_ENCHANTED_ENTITY_TARGET = new ContextKeySet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.TARGET_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .build();

    private static final DeferredRegister<DataComponentType<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);

    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> XP_MULTIPLIER = create(
            "xp_multiplier", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> HEAL_PER_XP = create(
            "heal_per_xp", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<EnchantmentValueEffect>> IRON_GOLEM_HEAL_MULTIPLIER = create(
            "iron_golem_heal_multiplier", builder -> builder
                    .persistent(EnchantmentValueEffect.CODEC));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BARTERING_ROLLS = create(
            "bartering_rolls", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> TRADING_COST_MULTIPLIER = create(
            "trading_cost_multiplier", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> FINAL_INCOMING_DAMAGE_MULTIPLIER = create(
            "final_incoming_damage_multiplier", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            LootContextParamSets.ENCHANTED_DAMAGE)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MOB_VISIBILITY_MULTIPLIER = create(
            "mob_visibility_multiplier", builder -> builder
                    .persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
                            CONTEXT_KEY_SET_ENCHANTED_ENTITY_TARGET)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>>> POST_DAMAGE = create(
            "post_damage", builder -> builder.persistent(
                    EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC),
                            LootContextParamSets.ENCHANTED_DAMAGE)));

    @NonNull
    private <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return REGISTRY.register(name, onBuilder.apply(DataComponentType.builder())::build);
    }
}
