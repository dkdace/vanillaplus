package com.dace.vanillaplus.extension.client;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.ToggleKeyMapping;

/**
 * {@link Options}를 확장하는 인터페이스.
 */
public interface VPOptions extends VPMixin<Options> {
    @NonNull
    static VPOptions cast(@NonNull Options object) {
        return (VPOptions) object;
    }

    /**
     * @return 엎드리기 토글 설정
     */
    @NonNull
    OptionInstance<Boolean> getToggleProne();

    /**
     * @return 최대 아이템 설명 폭
     */
    @NonNull
    OptionInstance<Double> getMaxItemTooltipWidth();

    /**
     * @return 아이템 설명 설정
     */
    @NonNull
    OptionInstance<Boolean> getItemDescription();

    /**
     * @return 공격 표식 설정
     */
    @NonNull
    OptionInstance<Boolean> getAttackMarker();

    /**
     * @return 몹 생명력 지표 설정
     */
    @NonNull
    OptionInstance<Boolean> getMobHealthIndicator();

    /**
     * @return 엎드리기 키
     */
    @NonNull
    ToggleKeyMapping getKeyProne();
}
