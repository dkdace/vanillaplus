package com.dace.vanillaplus.extension.world.item.trading;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.TradeSetOffer;
import lombok.NonNull;
import net.minecraft.world.item.trading.TradeSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link TradeSet}을 확장하는 인터페이스.
 */
public interface VPTradeSet extends VPMixin<TradeSet> {
    @NonNull
    static VPTradeSet cast(@NonNull TradeSet object) {
        return (VPTradeSet) object;
    }

    /**
     * @return 주민 거래 품목 목록
     */
    @Nullable
    List<TradeSetOffer> getTradeSetOffers();
}
