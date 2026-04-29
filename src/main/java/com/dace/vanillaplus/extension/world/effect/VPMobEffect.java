package com.dace.vanillaplus.extension.world.effect;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.MobEffectValues;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffect;

/**
 * {@link MobEffect}를 확장하는 인터페이스.
 *
 * @param <T> {@link MobEffect}를 상속받는 타입
 */
public interface VPMobEffect<T extends MobEffect> extends VPMixin<T>, VPModifiableData<T, MobEffectValues> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends MobEffect> VPMobEffect<T> cast(@NonNull T object) {
        return (VPMobEffect<T>) object;
    }
}
