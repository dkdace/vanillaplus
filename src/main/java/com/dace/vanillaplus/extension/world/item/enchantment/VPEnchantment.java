package com.dace.vanillaplus.extension.world.item.enchantment;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPEnchantmentEffectComponentTypes;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
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
     * 획득 경험치 배수 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param multiplier       경험치 배수 값
     * @see VPEnchantmentEffectComponentTypes#XP_MULTIPLIER
     */
    void modifyXpMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                            @NonNull MutableFloat multiplier);

    /**
     * 철 골렘 회복 배수 수치를 적용한다.
     *
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param healMultiplier   회복 배수 값
     * @see VPEnchantmentEffectComponentTypes#IRON_GOLEM_HEAL_MULTIPLIER
     */
    void modifyIronGolemHealMultiplier(int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                       @NonNull MutableFloat healMultiplier);

    /**
     * 피글린 거래 횟수 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param rolls            거래 횟수 값
     * @see VPEnchantmentEffectComponentTypes#BARTERING_ROLLS
     */
    void modifyBarteringRolls(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                              @NonNull MutableFloat rolls);

    /**
     * 주민 거래 가격 배수 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param multiplier       가격 배수 값
     * @see VPEnchantmentEffectComponentTypes#TRADING_COST_MULTIPLIER
     */
    void modifyTradingCostMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                                     @NonNull MutableFloat multiplier);

    /**
     * 최종 피해량 배수 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param damageSource     피해 근원
     * @param multiplier       피해량 배수 값
     * @see VPEnchantmentEffectComponentTypes#FINAL_INCOMING_DAMAGE_MULTIPLIER
     */
    void modifyFinalIncomingDamageMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack,
                                             @NonNull Entity entity, @NonNull DamageSource damageSource, @NonNull MutableFloat multiplier);
}
