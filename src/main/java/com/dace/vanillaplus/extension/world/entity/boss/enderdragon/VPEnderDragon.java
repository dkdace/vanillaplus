package com.dace.vanillaplus.extension.world.entity.boss.enderdragon;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * {@link EnderDragon}를 확장하는 인터페이스.
 *
 * @see EntityModifier.EnderDragonModifier
 */
public interface VPEnderDragon extends VPEntity<EnderDragon, EntityModifier.EnderDragonModifier> {
    @NonNull
    static VPEnderDragon cast(@NonNull EnderDragon object) {
        return (VPEnderDragon) object;
    }

    @Override
    @NonNull
    EntityModifier.EnderDragonModifier getDataModifier();

    /**
     * 기본 대상 탐지 조건을 반환한다.
     *
     * @return 대상 탐지 조건
     */
    @NonNull
    TargetingConditions getDefaultTargetingConditions();

    /**
     * @return 공격 쿨타임 (tick)
     */
    int getAttackCooldown();

    /**
     * @param cooldown 공격 쿨타임 (tick)
     */
    void setAttackCooldown(int cooldown);

    /**
     * @return 운석 공격 쿨타임 (tick)
     */
    int getMeteorAttackCooldown();

    /**
     * @param cooldown 운석 공격 쿨타임 (tick)
     */
    void setMeteorAttackCooldown(int cooldown);

    /**
     * 지정한 위치에 운석을 떨어뜨린다.
     *
     * @param pos 위치
     * @return 성공 여부
     */
    boolean dropMeteor(@NonNull Vec3 pos);

    /**
     * 현재 운석의 위치를 반환한다.
     *
     * @return 위치
     */
    @Nullable
    BlockPos getMeteorPos();

    /**
     * 브레스 영역의 상태 효과 인스턴스를 반환한다.
     *
     * @return 상태 효과 인스턴스
     */
    @NonNull
    MobEffectInstance getFlameMobEffectInstance();
}
