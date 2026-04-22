package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.data.registryobject.VPEnchantmentEffectComponentTypes;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.world.LevelBasedValuePreset;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements VPEnchantment {
    @Unique
    @Nullable
    @Setter
    private LevelBasedValuePreset levelBasedValuePreset;

    @Shadow
    @UnknownNullability
    public static LootContext damageContext(ServerLevel serverLevel, int enchantmentLevel, Entity entity, DamageSource damageSource) {
        return null;
    }

    @Shadow
    private static <T> void applyEffects(List<ConditionalEffect<T>> conditionalEffects, LootContext filterData, Enchantment.GenericAction<T> action) {
    }

    @Shadow
    protected abstract void modifyEntityFilteredValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> dataComponentType,
                                                      ServerLevel serverLevel, int enchantmentLevel, ItemStack itemStack, Entity entity,
                                                      MutableFloat value);

    @Shadow
    public abstract void modifyUnfilteredValue(DataComponentType<EnchantmentValueEffect> componentType, RandomSource randomSource,
                                               int enchantmentLevel, MutableFloat value);

    @Shadow
    public abstract <T> List<T> getEffects(DataComponentType<List<T>> dataComponentType);

    @Override
    @NonNull
    public Optional<LevelBasedValuePreset> getLevelBasedValuePreset() {
        return Optional.ofNullable(levelBasedValuePreset);
    }

    @Override
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent, int level) {
        if (levelBasedValuePreset != null)
            levelBasedValuePreset.applyTooltip(componentConsumer, descriptionComponent, level);

        getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(enchantmentAttributeEffect ->
                ItemAttributeModifiers.Display.attributeModifiers().apply(component ->
                                componentConsumer.accept(CommonComponents.space().append(component)), null,
                        enchantmentAttributeEffect.attribute(), enchantmentAttributeEffect.getModifier(level, EquipmentSlotGroup.ANY)));
    }

    @Override
    public void modifyXpMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                   @NonNull MutableFloat multiplier) {
        modifyEntityFilteredValue(VPEnchantmentEffectComponentTypes.XP_MULTIPLIER.get(), serverLevel, enchantmentLevel, itemStack, entity, multiplier);
    }

    @Override
    public void modifyHealPerXp(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                @NonNull MutableFloat amount) {
        modifyEntityFilteredValue(VPEnchantmentEffectComponentTypes.HEAL_PER_XP.get(), serverLevel, enchantmentLevel, itemStack, entity, amount);
    }

    @Override
    public void modifyIronGolemHealMultiplier(int enchantmentLevel, @NonNull Entity entity, @NonNull MutableFloat healMultiplier) {
        modifyUnfilteredValue(VPEnchantmentEffectComponentTypes.IRON_GOLEM_HEAL_MULTIPLIER.get(), entity.getRandom(), enchantmentLevel,
                healMultiplier);
    }

    @Override
    public void modifyBarteringRolls(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                     @NonNull MutableFloat rolls) {
        modifyEntityFilteredValue(VPEnchantmentEffectComponentTypes.BARTERING_ROLLS.get(), serverLevel, enchantmentLevel, itemStack, entity, rolls);
    }

    @Override
    public void modifyTradingCostMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack,
                                            @NonNull Entity entity, @NonNull MutableFloat multiplier) {
        modifyEntityFilteredValue(VPEnchantmentEffectComponentTypes.TRADING_COST_MULTIPLIER.get(), serverLevel, enchantmentLevel, itemStack, entity,
                multiplier);
    }

    @Override
    public void modifyFinalIncomingDamageMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull Entity entity,
                                                    @NonNull DamageSource damageSource, @NonNull MutableFloat multiplier) {
        LootContext lootContext = damageContext(serverLevel, enchantmentLevel, entity, damageSource);

        for (ConditionalEffect<EnchantmentValueEffect> conditionalEffect : getEffects(VPEnchantmentEffectComponentTypes.FINAL_INCOMING_DAMAGE_MULTIPLIER.get()))
            if (conditionalEffect.matches(lootContext))
                multiplier.setValue(conditionalEffect.effect().process(enchantmentLevel, entity.getRandom(), multiplier.floatValue()));
    }

    @Override
    public void modifyMobVisibilityMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull Entity entity,
                                              @NonNull Entity targetEntity, @NonNull MutableFloat multiplier) {
        LootContext lootContext = new LootContext.Builder(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.TARGET_ENTITY, targetEntity)
                .withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchantmentLevel)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .create(VPEnchantmentEffectComponentTypes.CONTEXT_KEY_SET_ENCHANTED_ENTITY_TARGET)).create(Optional.empty());

        applyEffects(getEffects(VPEnchantmentEffectComponentTypes.MOB_VISIBILITY_MULTIPLIER.get()), lootContext, effect ->
                multiplier.setValue(effect.process(enchantmentLevel, entity.getRandom(), multiplier.floatValue())));
    }

    @Override
    public void runPostDamageEffects(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull EnchantedItemInUse enchantedItemInUse,
                                     @NonNull Entity entity, @NonNull DamageSource damageSource) {
        LootContext lootContext = damageContext(serverLevel, enchantmentLevel, entity, damageSource);

        for (ConditionalEffect<EnchantmentEntityEffect> conditionalEffect : getEffects(VPEnchantmentEffectComponentTypes.POST_DAMAGE.get()))
            if (conditionalEffect.matches(lootContext))
                conditionalEffect.effect().apply(serverLevel, enchantmentLevel, enchantedItemInUse, entity, entity.position());
    }
}
