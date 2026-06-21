package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.extensions.IForgeItemStack;

/**
 * {@link ItemStack}을 확장하는 인터페이스.
 */
public interface VPItemStack extends VPMixin<ItemStack>, IForgeItemStack {
    @NonNull
    static VPItemStack cast(@NonNull ItemStack object) {
        return (VPItemStack) (Object) object;
    }

    /**
     * @return 수리 한도
     */
    int getRepairLimit();

    /**
     * @param repairLimit 수리 한도
     */
    void setRepairLimit(int repairLimit);

    /**
     * 수리 한도 수치가 표시되는지 확인한다.
     *
     * @return 수리 한도 표시 여부
     */
    default boolean isRepairLimitBarVisible() {
        return EnchantmentHelper.has(getThis(), EnchantmentEffectComponents.REPAIR_WITH_XP) && getThis().has(VPDataComponentTypes.REPAIR_WITH_XP.get());
    }

    /**
     * @return 최대 수리 한도
     */
    int getMaxRepairLimit();
}
