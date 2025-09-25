package com.dace.vanillaplus.custom;

import com.dace.vanillaplus.rebalance.modifier.DataModifier;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface CustomModifiableData<T, U extends DataModifier<T>> {
    @Nullable
    U getDataModifier();

    void setDataModifier(@NonNull U dataModifier);
}
