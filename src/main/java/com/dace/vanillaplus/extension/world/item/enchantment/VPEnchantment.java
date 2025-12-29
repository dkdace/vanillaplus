package com.dace.vanillaplus.extension.world.item.enchantment;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.mutable.MutableFloat;

/**
 * {@link Enchantment}를 확장하는 인터페이스.
 */
public interface VPEnchantment extends VPMixin<Enchantment> {
    @NonNull
    static VPEnchantment cast(@NonNull Enchantment object) {
        return (VPEnchantment) (Object) object;
    }

    /**
     * @see Enchantment#modifyEntityFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity, MutableFloat)
     */
    void modifyXpMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                            @NonNull MutableFloat multiplier);

    /**
     * @see Enchantment#modifyUnfilteredValue(DataComponentType, RandomSource, int, MutableFloat)
     */
    void modifyIronGolemHealMultiplier(int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                       @NonNull MutableFloat healMultiplier);

    /**
     * @see Enchantment#modifyEntityFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity, MutableFloat)
     */
    void modifyBarteringRolls(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                              @NonNull MutableFloat rolls);

    /**
     * @see Enchantment#modifyEntityFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity, MutableFloat)
     */
    void modifyTradingCostMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                     @NonNull MutableFloat multiplier);

    /**
     * @see Enchantment#modifyDamageProtection(ServerLevel, int, ItemStack, Entity, DamageSource, MutableFloat)
     */
    void modifyFinalIncomingDamageMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack,
                                             @NonNull Entity entity, @NonNull DamageSource damageSource, @NonNull MutableFloat multiplier);
}
