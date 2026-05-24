package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.item.ItemConfig;
import lombok.NonNull;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeItem;

/**
 * {@link Item}을 확장하는 인터페이스.
 *
 * @param <T> {@link Item}을 상속받는 타입
 * @see ItemConfig
 */
public interface VPItem<T extends Item> extends VPMixin<T>, VPModifiableData<Item, ItemConfig>, IForgeItem {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Item> VPItem<T> cast(@NonNull T object) {
        return (VPItem<T>) object;
    }

    /**
     * @return 설정 데이터 요소 목록
     */
    @NonNull
    VPDataComponentMap getConfigComponents();

    /**
     * 아이템 설정의 아이템 데이터 요소를 적용한다.
     */
    void applyConfigItemComponents();
}
