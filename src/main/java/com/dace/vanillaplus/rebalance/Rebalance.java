package com.dace.vanillaplus.rebalance;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.IntegerRange;

import java.util.function.IntUnaryOperator;

/**
 * 리밸런싱 설정을 관리하는 클래스.
 */
@UtilityClass
public final class Rebalance {
    /** 모루에서의 아이템 수리 비용 (원본: 2n+1) */
    public static final IntUnaryOperator ANVIL_REPAIR_COST = cost -> (int) Math.min(cost + 1L, 2147483647L);
    /** 석영 원석의 드롭 경험치 (원본: 2~5) */
    public static final IntegerRange QUARTZ_ORE_DROP_XP = IntegerRange.of(1, 3);
    /** 수선 인첸트의 수리 한계치 */
    public static final double MENDING_REPAIR_LIMIT = 0.5;
    /** 방패 내구도 (원본: 336) */
    public static final int SHIELD_DURABILITY = 260;
    /** 철 또는 금 도구를 화로에서 녹일 때 내구도 감소 비율 */
    public static final double SMELTING_TOOL_DAMAGE_RATIO = 0.21;

    /**
     * 겉날개 설정.
     */
    @UtilityClass
    public static final class Elytra {
        /** 폭죽의 추가 속도 배수 (원본: 1.5) */
        public static final double FIREWORK_ADD_SPEED_MULTIPLIER = 1.2;
        /** 폭죽의 최종 속도 배수 (원본: 0.5) */
        public static final double FIREWORK_FINAL_SPEED_MULTIPLIER = 0.25;
    }

    /**
     * 쇠뇌 설정.
     */
    @UtilityClass
    public static final class Crossbow {
        /** 폭죽 발사 속력 (원본: 1.6) */
        public static final float SHOOTING_POWER_FIREWORK_ROCKET = 1.8F;
        /** 화살 발사 속력 (원본: 3.15) */
        public static final float SHOOTING_POWER_ARROW = 4.3F;
        /** 몹의 화살 발사 속력 (원본: 1.6) */
        public static final float MOB_SHOOTING_POWER_ARROW = 2.6F;
        /** 몹의 발사 거리 (원본: 8) */
        public static final int MOB_SHOOTING_RANGE = 16;
    }

    /**
     * 크리킹 설정.
     */
    @UtilityClass
    public static final class Creaking {
        /** 크리킹 심장의 드롭 경험치 (원본: 20~24) */
        public static final IntegerRange CREAKING_HEART_DROP_XP = IntegerRange.of(26, 32);
        /** 피해량 (원본: 3) */
        public static final double ATTACK_DAMAGE = 6;
        /** 이동속도 (원본: 0.4) */
        public static final double MOVEMENT_SPEED = 0.5;
    }
}
