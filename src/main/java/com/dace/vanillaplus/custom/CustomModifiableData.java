package com.dace.vanillaplus.custom;

import com.dace.vanillaplus.rebalance.modifier.DataModifier;
import lombok.NonNull;

public interface CustomModifiableData<T, U extends DataModifier<T>> {
    void apply(@NonNull U modifier);
}
