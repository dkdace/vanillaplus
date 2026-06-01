package com.dace.vanillaplus.extension.world.item.trading;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * {@link MerchantOffer}를 확장하는 인터페이스.
 */
public interface VPMerchantOffer extends VPMixin<MerchantOffer> {
    @NonNull
    static VPMerchantOffer cast(@NonNull MerchantOffer object) {
        return (VPMerchantOffer) object;
    }

    /**
     * @return 거래 시 지급되는 경험치의 가격 비례 여부
     */
    boolean isMultiplyRewardXPByCost();
}
