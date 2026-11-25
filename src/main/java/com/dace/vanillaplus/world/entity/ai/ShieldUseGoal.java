package com.dace.vanillaplus.world.entity.ai;

import com.dace.vanillaplus.extension.world.entity.VPMob;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;

import java.util.EnumSet;

/**
 * 방패를 사용하는 Goal 클래스.
 *
 * @param <T> {@link Monster}를 상속받는 타입
 */
public final class ShieldUseGoal<T extends Monster> extends Goal {
    /** 속도 수정자 */
    private static final double SPEED_MODIFIER = 0.3;
    /** 방패 사용 거리 */
    private static final int BLOCK_DISTANCE = 5;
    /** 피격 시 {@link ShieldUseGoal#BLOCK_DISTANCE} 조건 무시 시간 */
    private static final int IGNORE_DISTANCE_DURATION = 40;

    /** 대상 몹 */
    private final T mob;
    /** 최소 사용 시간 (tick) */
    private final int minCooldown;
    /** 최대 사용 시간 (tick) */
    private final int maxCooldown;
    /** 최소 사용 시간 (tick) */
    private final int minUseDuration;
    /** 최대 사용 시간 (tick) */
    private final int maxUseDuration;
    /** 다음 사용 시간 */
    private int nextUseTime = 0;
    /** 사용 시간 (tick) */
    private int duration;

    /**
     * 방패 사용 Goal 인스턴스를 생성한다.
     *
     * @param mob            대상 몹
     * @param minCooldown    최소 쿨타임 (tick)
     * @param maxCooldown    최대 쿨타임 (tick)
     * @param minUseDuration 최소 사용 시간 (tick)
     * @param maxUseDuration 최대 사용 시간 (tick)
     */
    public ShieldUseGoal(@NonNull T mob, int minCooldown, int maxCooldown, int minUseDuration, int maxUseDuration) {
        this.mob = mob;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.minUseDuration = minUseDuration;
        this.maxUseDuration = maxUseDuration;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null)
            return false;

        return ableToUse() && !mob.isUsingItem() && nextUseTime <= mob.tickCount && (mob.distanceToSqr(target) <= BLOCK_DISTANCE * BLOCK_DISTANCE
                || mob.getLastHurtByMob() == target && mob.tickCount - mob.getLastHurtByMobTimestamp() <= IGNORE_DISTANCE_DURATION);
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isUsingItem() && ableToUse() && duration > 0;
    }

    @Override
    public void start() {
        mob.setAggressive(true);
        mob.startUsingItem(InteractionHand.OFF_HAND);
        duration = adjustedTickDelay(mob.getRandom().nextInt(minUseDuration, maxUseDuration));
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        mob.setAggressive(false);
        mob.stopUsingItem();

        int disableBlockingTime = VPMob.cast(mob).getDisableBlockingTime();
        nextUseTime = mob.tickCount + (disableBlockingTime > 0 ? disableBlockingTime : mob.getRandom().nextInt(minCooldown, maxCooldown));
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null)
            return;

        mob.getLookControl().setLookAt(target, 30, 30);

        PathNavigation pathNavigation = mob.getNavigation();
        if (mob.distanceToSqr(target) <= 2)
            pathNavigation.stop();
        else
            pathNavigation.moveTo(target, SPEED_MODIFIER);

        duration--;
    }

    /**
     * 사용할 조건을 만족하는지 확인한다.
     *
     * @return 사용할 조건을 만족하면 {@code true} 반환
     */
    private boolean ableToUse() {
        LivingEntity target = mob.getTarget();
        if (target == null)
            return false;

        ItemStack useItem = target.getUseItem();
        return mob.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && VPMob.cast(mob).getDisableBlockingTime() <= 0
                && mob.getSensing().hasLineOfSight(target)
                && mob.isLookingAtMe(target, 0.4, false, true, mob.getEyeY())
                && (!target.isUsingItem() || !useItem.has(DataComponents.BLOCKS_ATTACKS)
                && useItem.getUseDuration(target) >= SpyglassItem.USE_DURATION);
    }
}
