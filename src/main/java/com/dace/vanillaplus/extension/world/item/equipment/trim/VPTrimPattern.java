package com.dace.vanillaplus.extension.world.item.equipment.trim;

import com.dace.vanillaplus.data.ArmorTrimEffect;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.equipment.trim.TrimPattern;

import java.util.function.Consumer;

/**
 * {@link TrimPattern}를 확장하는 인터페이스.
 */
public interface VPTrimPattern extends VPMixin<TrimPattern>, VPModifiableData<TrimPattern, ArmorTrimEffect.TrimPatternEffect> {
    @NonNull
    static VPTrimPattern cast(@NonNull TrimPattern object) {
        return (VPTrimPattern) (Object) object;
    }

    /**
     * 갑옷 장식 형판의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer {@link TooltipProvider}의 텍스트 요소 Consumer
     */
    void applyTooltip(@NonNull Consumer<Component> componentConsumer);
}
