package com.dace.vanillaplus.extension.world.item.enchantment;

import com.dace.vanillaplus.data.modifier.EnchantmentModifier;
import com.dace.vanillaplus.extension.VPLevelBased;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPEnchantmentEffectComponentTypes;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.function.Consumer;

/**
 * {@link Enchantment}를 확장하는 인터페이스.
 *
 * @see EnchantmentModifier
 */
public interface VPEnchantment extends VPMixin<Enchantment>, VPModifiableData<Enchantment, EnchantmentModifier>, VPLevelBased<Enchantment> {
    @NonNull
    static VPEnchantment cast(@NonNull Enchantment object) {
        return (VPEnchantment) (Object) object;
    }

    /**
     * 마법 부여의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer    {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent 설명 텍스트 요소
     * @param level                마법 부여 레벨
     */
    void applyTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent, int level);

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
     * 획득 경험치당 회복량 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param itemStack        대상 아이템
     * @param entity           대상 엔티티
     * @param amount           회복량
     * @see VPEnchantmentEffectComponentTypes#HEAL_PER_XP
     */
    void modifyHealPerXp(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull ItemStack itemStack, @NonNull Entity entity,
                         @NonNull MutableFloat amount);

    /**
     * 철 골렘 회복 배수 수치를 적용한다.
     *
     * @param enchantmentLevel 마법 부여 레벨
     * @param entity           대상 엔티티
     * @param healMultiplier   회복 배수 값
     * @see VPEnchantmentEffectComponentTypes#IRON_GOLEM_HEAL_MULTIPLIER
     */
    void modifyIronGolemHealMultiplier(int enchantmentLevel, @NonNull Entity entity, @NonNull MutableFloat healMultiplier);

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
     * @param entity           대상 엔티티
     * @param damageSource     피해 근원
     * @param multiplier       피해량 배수 값
     * @see VPEnchantmentEffectComponentTypes#FINAL_INCOMING_DAMAGE_MULTIPLIER
     */
    void modifyFinalIncomingDamageMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull Entity entity,
                                             @NonNull DamageSource damageSource, @NonNull MutableFloat multiplier);

    /**
     * 몹 가시성 배수 수치를 적용한다.
     *
     * @param serverLevel      월드
     * @param enchantmentLevel 마법 부여 레벨
     * @param entity           대상 엔티티
     * @param targetEntity     {@code entity}를 감지하려는 몹
     * @param multiplier       가시성 배수 값
     * @see VPEnchantmentEffectComponentTypes#MOB_VISIBILITY_MULTIPLIER
     */
    void modifyMobVisibilityMultiplier(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull Entity entity, @NonNull Entity targetEntity,
                                       @NonNull MutableFloat multiplier);

    /**
     * 엔티티가 피해를 입었을 때 효과를 실행한다.
     *
     * @param serverLevel        월드
     * @param enchantmentLevel   마법 부여 레벨
     * @param enchantedItemInUse 대상 마법 부여 아이템
     * @param entity             대상 엔티티
     * @param damageSource       피해 근원
     * @see VPEnchantmentEffectComponentTypes#POST_DAMAGE
     */
    void runPostDamageEffects(@NonNull ServerLevel serverLevel, int enchantmentLevel, @NonNull EnchantedItemInUse enchantedItemInUse,
                              @NonNull Entity entity, @NonNull DamageSource damageSource);

    /**
     * {@link Enchantment.EnchantmentDefinition}를 확장하는 인터페이스.
     */
    interface VPEnchantmentDefinition extends VPMixin<VPEnchantment> {
        @NonNull
        static VPEnchantmentDefinition cast(@NonNull Enchantment.EnchantmentDefinition object) {
            return (VPEnchantmentDefinition) (Object) object;
        }

        /**
         * 수정자의 마법 부여 설정을 적용한다.
         *
         * @param enchantmentDefinition 마법 부여 설정
         */
        void set(@NonNull EnchantmentModifier.EnchantmentDefinition enchantmentDefinition);
    }
}
