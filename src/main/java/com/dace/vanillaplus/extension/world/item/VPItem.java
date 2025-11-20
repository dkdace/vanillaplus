package com.dace.vanillaplus.extension.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.world.item.Item;

/**
 * {@link Item}을 확장하는 인터페이스.
 *
 * @param <T> {@link Item}을 상속받는 타입
 * @param <U> {@link ItemModifier}를 상속받는 아이템 수정자
 * @see ItemModifier
 */
public interface VPItem<T extends Item, U extends ItemModifier> extends VPMixin<T>, VPModifiableData<Item, U> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Item, U extends ItemModifier> VPItem<T, U> cast(@NonNull T object) {
        return (VPItem<T, U>) object;
    }
}
