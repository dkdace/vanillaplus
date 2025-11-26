package com.dace.vanillaplus.extension.world.item.alchemy;

import com.dace.vanillaplus.data.modifier.PotionModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;
import java.util.Optional;

/**
 * {@link Potion}을 확장하는 인터페이스.
 *
 * @see PotionModifier
 */
public interface VPPotion extends VPMixin<Potion>, VPModifiableData<Potion, PotionModifier> {
    @NonNull
    static VPPotion cast(@NonNull Potion object) {
        return (VPPotion) object;
    }

    /**
     * 물약의 색상을 반환한다.
     *
     * @return 물약의 색상
     */
    @NonNull
    Optional<Integer> getColor();

    /**
     * 물약의 색상을 지정한다.
     *
     * @param color 물약의 색상
     */
    void setColor(int color);

    /**
     * 물약이 반짝이는지 확인한다.
     *
     * @return 반짝임 여부
     */
    boolean isGlistering();

    /**
     * 물약이 반짝이는지 여부를 지정한다.
     *
     * @param isGlistering 반짝임 여부
     */
    void setGlistering(boolean isGlistering);

    /**
     * 물약의 상태 효과를 지정한다.
     *
     * @param mobEffectInstances 상태 효과 목록
     */
    void setEffects(@NonNull List<MobEffectInstance> mobEffectInstances);
}
