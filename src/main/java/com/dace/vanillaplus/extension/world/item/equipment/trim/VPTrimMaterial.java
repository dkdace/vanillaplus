package com.dace.vanillaplus.extension.world.item.equipment.trim;

import com.dace.vanillaplus.data.modifier.ArmorTrimEffect;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

/**
 * {@link TrimMaterial}를 확장하는 인터페이스.
 */
public interface VPTrimMaterial extends VPMixin<TrimMaterial>, VPModifiableData<TrimMaterial, ArmorTrimEffect.TrimMaterialEffect> {
    @NonNull
    static VPTrimMaterial cast(@NonNull TrimMaterial object) {
        return (VPTrimMaterial) (Object) object;
    }
}
