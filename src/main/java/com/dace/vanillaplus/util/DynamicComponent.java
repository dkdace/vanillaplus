package com.dace.vanillaplus.util;

import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * 가변 인수를 통해 생성되는 동적 컴포넌트 인터페이스.
 *
 * @see Component#translatable(String, Object...)
 */
@FunctionalInterface
public interface DynamicComponent {
    /**
     * 컴포넌트를 반환한다.
     *
     * @param args 매개변수 목록
     * @return {@link MutableComponent}
     */
    @NonNull
    MutableComponent get(@NonNull Object @NonNull ... args);
}
