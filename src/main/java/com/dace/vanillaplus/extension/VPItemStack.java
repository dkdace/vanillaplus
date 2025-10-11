package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * {@link ItemStack}을 확장하는 인터페이스.
 */
public interface VPItemStack {
    /**
     * 수리 한도를 반환한다.
     *
     * @param itemStack 대상 아이템
     * @return 수리 한도
     */
    static int getRepairLimit(@NonNull ItemStack itemStack) {
        return ((VPItemStack) (Object) itemStack).getRepairLimit();
    }

    /**
     * 수리 한도를 지정한다.
     *
     * @param itemStack   대상 아이템
     * @param repairLimit 수리 한도
     */
    static void setRepairLimit(@NonNull ItemStack itemStack, int repairLimit) {
        ((VPItemStack) (Object) itemStack).setRepairLimit(repairLimit);
    }

    /**
     * 최대 수리 한도를 반환한다.
     *
     * @param itemStack 대상 아이템
     * @return 최대 수리 한도
     */
    static int getMaxRepairLimit(@NonNull ItemStack itemStack) {
        return ((VPItemStack) (Object) itemStack).getMaxRepairLimit();
    }

    /**
     * 수리 한도 수치가 표시되는지 확인한다.
     *
     * @param itemStack 대상 아이템
     * @return 수리 한도 표시 여부
     */
    static boolean isRepairLimitBarVisible(@NonNull ItemStack itemStack) {
        return EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.REPAIR_WITH_XP);
    }

    int getRepairLimit();

    void setRepairLimit(int repairLimit);

    int getMaxRepairLimit();
}
