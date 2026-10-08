package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.item.enchantment.RepairWithItem;
import lombok.NonNull;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * {@link ItemStack}을 확장하는 인터페이스.
 */
public interface VPItemStack extends VPMixin<ItemStack>, ItemInstance, IForgeItemStack {
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
     * @return 최대 수리 한도
     */
    int getMaxRepairLimit();

    /**
     * 마법 부여에서 가장 높은 레벨의 아이템 기반 수리 한도를 반환한다.
     *
     * @return {@link RepairWithItem}
     */
    @Nullable
    RepairWithItem getRepairWithItem();
}
