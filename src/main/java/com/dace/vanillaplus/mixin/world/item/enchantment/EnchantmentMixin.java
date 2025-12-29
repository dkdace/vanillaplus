package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.registryobject.VPEnchantmentEffectComponentTypes;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements VPEnchantment {
    @Shadow
    @UnknownNullability
    public static LootContext damageContext(ServerLevel serverLevel, int enchantmentLevel, Entity entity, DamageSource damageSource) {
        return null;
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
    public void modifyXpMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                   @NonNull MutableFloat multiplier) {
        modifyEntityFilteredValue(VPEnchantmentEffectComponentTypes.XP_MULTIPLIER.get(), serverLevel, enchantmentLevel, itemStack, entity,
                multiplier);
    }

    @Override
    public void modifyIronGolemHealMultiplier(int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                              @NonNull MutableFloat healMultiplier) {
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
    public void modifyFinalIncomingDamageMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack,
                                                    @NonNull Entity entity, @NonNull DamageSource damageSource, @NonNull MutableFloat multiplier) {
        LootContext lootcontext = damageContext(serverLevel, enchantmentLevel, entity, damageSource);

        for (ConditionalEffect<EnchantmentValueEffect> conditionalEffect : getEffects(VPEnchantmentEffectComponentTypes.FINAL_INCOMING_DAMAGE_MULTIPLIER.get()))
            if (conditionalEffect.matches(lootcontext))
                multiplier.setValue(conditionalEffect.effect().process(enchantmentLevel, entity.getRandom(), multiplier.floatValue()));
    }
}
