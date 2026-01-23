package com.dace.vanillaplus.extension.world.item.equipment.trim;

import com.dace.vanillaplus.data.modifier.ArmorTrimEffect;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/**
 * {@link TrimPattern}를 확장하는 인터페이스.
 */
public interface VPTrimPattern extends VPMixin<TrimPattern>, VPModifiableData<TrimPattern, ArmorTrimEffect.TrimPatternEffect> {
    @NonNull
    static VPTrimPattern cast(@NonNull TrimPattern object) {
        return (VPTrimPattern) (Object) object;
    }
}
