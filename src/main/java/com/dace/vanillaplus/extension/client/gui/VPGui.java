package com.dace.vanillaplus.extension.client.gui;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.client.gui.Gui;

/**
 * {@link Gui}를 확장하는 인터페이스.
 */
public interface VPGui extends VPMixin<Gui> {
    @NonNull
    static VPGui cast(@NonNull Gui object) {
        return (VPGui) object;
    }

    /**
     * 최근 피해량을 갱신한다.
     *
     * @param damage   피해량
     * @param isKilled 처치 여부
     */
    void updateRecentDamage(float damage, boolean isKilled);
}
