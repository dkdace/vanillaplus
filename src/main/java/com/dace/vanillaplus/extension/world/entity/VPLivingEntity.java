package com.dace.vanillaplus.extension.world.entity;

import com.dace.vanillaplus.world.entity.modifier.LivingEntityModifier;
import lombok.NonNull;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link LivingEntity}를 확장하는 인터페이스.
 *
 * @param <T> {@link LivingEntity}를 상속받는 타입
 * @param <U> {@link LivingEntityModifier}를 상속받는 엔티티 수정자
 * @see LivingEntityModifier
 */
public interface VPLivingEntity<T extends LivingEntity, U extends LivingEntityModifier> extends VPEntity<T, U>, IForgeLivingEntity {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends LivingEntity, U extends LivingEntityModifier> VPLivingEntity<T, U> cast(@NonNull T object) {
        return (VPLivingEntity<T, U>) object;
    }

    @Override
    @NonNull
    LivingEntityModifier getDefaultDataModifier();

    /**
     * 엔티티의 최종 밀치기 저항 수치를 반환한다.
     *
     * @param knockbackResistance 기존 밀치기 저항
     * @param damageSource        피해 근원
     * @return 밀치기 저항. 0~1 사이의 값
     */
    double getFinalKnockbackResistance(double knockbackResistance, @Nullable DamageSource damageSource);
}
