package com.dace.vanillaplus.extension.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.item.component.Weapon;

/**
 * {@link Weapon}을 확장하는 인터페이스.
 */
public interface VPWeapon extends VPMixin<Weapon> {
    @NonNull
    static VPWeapon cast(@NonNull Weapon object) {
        return (VPWeapon) (Object) object;
    }

    /**
     * @return 최대 충전 시 방패 무력화 여부
     */
    boolean isDisableBlockingOnFullStrengthAttack();
}
