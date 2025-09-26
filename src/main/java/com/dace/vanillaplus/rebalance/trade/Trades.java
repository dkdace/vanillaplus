package com.dace.vanillaplus.rebalance.trade;

import com.dace.vanillaplus.VPRegistries;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * 주민 거래 정보({@link Trade}) 목록을 관리하는 클래스.
 *
 * @see Trade
 */
@UtilityClass
public final class Trades {
    /** 떠돌이 상인 */
    public static final ResourceKey<Trade> WANDERING_TRADER = VPRegistries.TRADE.createResourceKey("wandering_trader");

    /** 주민 직업별 거래 정보 목록 (주민 직업 : 거래 정보) */
    private static final HashMap<ResourceKey<VillagerProfession>, ResourceKey<Trade>> RESOURCE_KEY_MAP = new HashMap<>();
    /** 농부 */
    public static final ResourceKey<Trade> FARMER = create(VillagerProfession.FARMER);
    /** 어부 */
    public static final ResourceKey<Trade> FISHERMAN = create(VillagerProfession.FISHERMAN);
    /** 양치기 */
    public static final ResourceKey<Trade> SHEPHERD = create(VillagerProfession.SHEPHERD);
    /** 화살 제조인 */
    public static final ResourceKey<Trade> FLETCHER = create(VillagerProfession.FLETCHER);
    /** 사서 */
    public static final ResourceKey<Trade> LIBRARAIN = create(VillagerProfession.LIBRARIAN);
    /** 지도 제작자 */
    public static final ResourceKey<Trade> CARTOGRAPHER = create(VillagerProfession.CARTOGRAPHER);
    /** 성직자 */
    public static final ResourceKey<Trade> CLERIC = create(VillagerProfession.CLERIC);
    /** 갑옷 제조인 */
    public static final ResourceKey<Trade> ARMORER = create(VillagerProfession.ARMORER);
    /** 무기 대장장이 */
    public static final ResourceKey<Trade> WEAPONSMITH = create(VillagerProfession.WEAPONSMITH);
    /** 도구 대장장이 */
    public static final ResourceKey<Trade> TOOLSMITH = create(VillagerProfession.TOOLSMITH);
    /** 도살업자 */
    public static final ResourceKey<Trade> BUTCHER = create(VillagerProfession.BUTCHER);
    /** 가죽 세공인 */
    public static final ResourceKey<Trade> LEATHERWORKER = create(VillagerProfession.LEATHERWORKER);
    /** 석공 */
    public static final ResourceKey<Trade> MASON = create(VillagerProfession.MASON);

    /**
     * 지정한 주민 직업에 해당하는 거래 정보 리소스 키를 반환한다.
     *
     * @param villagerProfessionResourceKey 주민 직업 리소스 키
     * @return 거래 정보 리소스 키. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public static ResourceKey<Trade> fromVillagerProfession(@NonNull ResourceKey<VillagerProfession> villagerProfessionResourceKey) {
        return RESOURCE_KEY_MAP.get(villagerProfessionResourceKey);
    }

    @NonNull
    private static ResourceKey<Trade> create(@NonNull ResourceKey<VillagerProfession> villagerProfessionResourceKey) {
        ResourceKey<Trade> tradeResourceKey = VPRegistries.TRADE.createResourceKey(villagerProfessionResourceKey.location().getPath());
        RESOURCE_KEY_MAP.put(villagerProfessionResourceKey, tradeResourceKey);

        return tradeResourceKey;
    }
}
