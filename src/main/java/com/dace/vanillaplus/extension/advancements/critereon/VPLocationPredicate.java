package com.dace.vanillaplus.extension.advancements.critereon;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

/**
 * {@link LocationPredicate}를 확장하는 인터페이스.
 */
public interface VPLocationPredicate extends VPMixin<LocationPredicate> {
    @NonNull
    static VPLocationPredicate cast(@NonNull LocationPredicate object) {
        return (VPLocationPredicate) (Object) object;
    }

    /**
     * @return 강수 상태
     */
    @NonNull
    Optional<Biome.Precipitation> getPrecipitation();

    /**
     * @param precipitation 강수 상태
     */
    void setPrecipitation(@NonNull Optional<Biome.Precipitation> precipitation);
}
