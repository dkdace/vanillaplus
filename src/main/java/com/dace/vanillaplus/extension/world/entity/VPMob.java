package com.dace.vanillaplus.extension.world.entity;

import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.*;
import org.jetbrains.annotations.Nullable;

/**
 * {@link Mob}를 확장하는 인터페이스.
 *
 * @param <T> {@link Mob}를 상속받는 타입
 */
public interface VPMob<T extends LivingEntity> extends VPLivingEntity<T>, Targeting, EquipmentUser, Leashable {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Mob> VPMob<T> cast(@NonNull T object) {
        return (VPMob<T>) object;
    }

    /**
     * {@link DataComponents#BLOCKS_ATTACKS} 아이템으로 방어할 수 없는 시간을 반환한다.
     *
     * @return 방어 불가 시간
     */
    int getDisableBlockingTime();

    /**
     * 시련 생성기에 의해 스폰되었을 때의 위치를 반환한다.
     *
     * @return 시련 생성기 위치. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    BlockPos getTrialSpawnerPos();

    /**
     * 시련 생성기에 의해 스폰되었을 때의 위치를 지정한다.
     *
     * @param blockPos 시련 생성기 위치
     */
    void setTrialSpawnerPos(@Nullable BlockPos blockPos);
}
