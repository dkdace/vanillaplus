package com.dace.vanillaplus.extension.world.item.alchemy;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.item.PotionModifier;
import lombok.NonNull;
import net.minecraft.world.item.alchemy.Potion;

/**
 * {@link Potion}을 확장하는 인터페이스.
 *
 * @see PotionModifier
 */
public interface VPPotion extends VPMixin<Potion>, VPModifiableData<Potion, PotionModifier> {
    @NonNull
    static VPPotion cast(@NonNull Potion object) {
        return (VPPotion) object;
    }
}
