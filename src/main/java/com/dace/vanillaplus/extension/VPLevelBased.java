package com.dace.vanillaplus.extension;

import com.dace.vanillaplus.data.LevelBasedValuePreset;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 레벨 기반 값 프리셋({@link LevelBasedValuePreset})을 가진 요소를 나타내는 인터페이스.
 *
 * @param <T> 수정 대상 데이터 타입
 */
public interface VPLevelBased<T> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T> VPLevelBased<T> cast(@NonNull T object) {
        return (VPLevelBased<T>) object;
    }

    /**
     * @return 레벨 기반 값 프리셋
     */
    @NonNull
    Optional<LevelBasedValuePreset> getLevelBasedValuePreset();

    /**
     * @param levelBasedValuePreset 레벨 기반 값 프리셋
     */
    void setLevelBasedValuePreset(@Nullable LevelBasedValuePreset levelBasedValuePreset);
}
