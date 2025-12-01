package com.dace.vanillaplus.extension.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.level.block.entity.VPBrewingStandBlockEntity;
import lombok.NonNull;
import net.minecraft.world.inventory.BrewingStandMenu;

/**
 * {@link BrewingStandMenu}를 확장하는 인터페이스.
 */
public interface VPBrewingStandMenu extends VPMixin<BrewingStandMenu> {
    @NonNull
    static VPBrewingStandMenu cast(@NonNull BrewingStandMenu object) {
        return (VPBrewingStandMenu) object;
    }

    /**
     * @see VPBrewingStandBlockEntity#getTotalBrewTime()
     */
    int getTotalBrewTime();
}
