package com.dace.vanillaplus.extension.world.item.equipment.trim;

import com.dace.vanillaplus.data.ArmorTrimEffect;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.function.Consumer;

/**
 * {@link TrimMaterial}를 확장하는 인터페이스.
 *
 * @see ArmorTrimEffect.TrimMaterialEffect
 */
public interface VPTrimMaterial extends VPMixin<TrimMaterial>, VPModifiableData<TrimMaterial, ArmorTrimEffect.TrimMaterialEffect> {
    @NonNull
    static VPTrimMaterial cast(@NonNull TrimMaterial object) {
        return (VPTrimMaterial) (Object) object;
    }

    /**
     * 갑옷 장식 재료의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer {@link TooltipProvider}의 텍스트 요소 Consumer
     */
    void applyTooltip(@NonNull Consumer<Component> componentConsumer);
}
