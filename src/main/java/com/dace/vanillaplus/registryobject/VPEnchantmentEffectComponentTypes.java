package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 마법 부여의 효과 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VPEnchantmentEffectComponentTypes {
    public static final ContextKeySet CONTEXT_KEY_SET_ENCHANTED_ENTITY_TARGET = new ContextKeySet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.TARGET_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .build();

    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> XP_MULTIPLIER = create("xp_multiplier",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> HEAL_PER_XP = create("heal_per_xp",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<EnchantmentValueEffect>> IRON_GOLEM_HEAL_MULTIPLIER = create("iron_golem_heal_multiplier",
            builder -> builder.persistent(EnchantmentValueEffect.CODEC));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> BARTERING_ROLLS = create("bartering_rolls",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> TRADING_COST_MULTIPLIER = create("trading_cost_multiplier",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), LootContextParamSets.ENCHANTED_ENTITY)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> FINAL_INCOMING_DAMAGE_MULTIPLIER = create("final_incoming_damage_multiplier",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), LootContextParamSets.ENCHANTED_DAMAGE)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MOB_VISIBILITY_MULTIPLIER = create("mob_visibility_multiplier",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentValueEffect.CODEC), CONTEXT_KEY_SET_ENCHANTED_ENTITY_TARGET)));
    public static final RegistryObject<DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>>> POST_DAMAGE = create("post_damage",
            builder -> builder.persistent(EnchantmentEffectComponents.validatedListCodec(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC), LootContextParamSets.ENCHANTED_DAMAGE)));

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return VPRegistry.register(VPRegistry.ENCHANTMENT_EFFECT_COMPONENT_TYPE, name, onBuilder.apply(DataComponentType.builder())::build);
    }

    @SubscribeEvent
    private static void onLivingVisibility(@NonNull LivingEvent.LivingVisibilityEvent event) {
        Entity targetEntity = event.getLookingEntity();
        if (targetEntity == null)
            return;

        LivingEntity entity = event.getEntity();
        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(entity, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyMobVisibilityMultiplier((ServerLevel) entity.level(), level,
                        entity, targetEntity, value));

        event.modifyVisibility(value.floatValue());
    }

    @SubscribeEvent
    private static void onLivingDamage(@NonNull LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        EnchantmentHelper.runIterationOnEquipment(entity, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).runPostDamageEffects((ServerLevel) entity.level(), level, enchantedItemInUse, entity,
                        event.getSource()));
    }
}
