package com.dace.vanillaplus.extension.world.entity.npc;

import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import com.dace.vanillaplus.world.entity.monster.NpcConfig;
import lombok.NonNull;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.Merchant;

/**
 * {@link AbstractVillager}를 확장하는 인터페이스.
 */
public interface VPAbstractVillager<T extends AbstractVillager> extends VPLivingEntity<T>, Merchant, InventoryCarrier {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends AbstractVillager> VPAbstractVillager<T> cast(@NonNull T object) {
        return (VPAbstractVillager<T>) object;
    }

    /**
     * @see NpcConfig
     */
    @NonNull
    NpcConfig getNpcConfig();
}
