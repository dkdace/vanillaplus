package com.dace.vanillaplus.extension.world.entity;

import lombok.NonNull;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * {@link LivingEntity}를 확장하는 인터페이스.
 *
 * @param <T> {@link LivingEntity}를 상속받는 타입
 */
public interface VPLivingEntity<T extends LivingEntity> extends VPEntity<T>, Attackable, WaypointTransmitter, IForgeLivingEntity {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends LivingEntity> VPLivingEntity<T> cast(@NonNull T object) {
        return (VPLivingEntity<T>) object;
    }

    /**
     * 엔티티의 최종 밀치기 저항 수치를 반환한다.
     *
     * @param knockbackResistance 기존 밀치기 저항
     * @param damageSource        피해 근원
     * @return 밀치기 저항. 0~1 사이의 값
     */
    double getFinalKnockbackResistance(double knockbackResistance, @Nullable DamageSource damageSource);

    /**
     * 생명력 표시 상태를 갱신한다.
     */
    void updateRenderHealth();

    /**
     * 생명력을 표시할 수 있는지 확인한다.
     *
     * @return 생명력 표시 여부
     */
    boolean canRenderHealth();
}
