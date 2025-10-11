package com.dace.vanillaplus.data.trade;

import com.dace.vanillaplus.VPRegistries;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceKey;

/**
 * 구조물 지도({@link StructureMap}) 목록을 관리하는 클래스.
 *
 * @see StructureMap
 */
@UtilityClass
public final class StructureMaps {
    /** 고대 도시 */
    public static final ResourceKey<StructureMap> ANCIENT_CITY = create("ancient_city");
    /** 땅에 묻힌 보물 */
    public static final ResourceKey<StructureMap> BURIED_TREASURE = create("buried_treasure");
    /** 사막 탐험 */
    public static final ResourceKey<StructureMap> EXPLORER_DESERT = create("explorer_desert");
    /** 정글 탐험 */
    public static final ResourceKey<StructureMap> EXPLORER_JUNGLE = create("explorer_jungle");
    /** 설원 탐험 */
    public static final ResourceKey<StructureMap> EXPLORER_SNOWY = create("explorer_snowy");
    /** 늪 탐험 */
    public static final ResourceKey<StructureMap> EXPLORER_SWAMP = create("explorer_swamp");
    /** 삼림 대저택 */
    public static final ResourceKey<StructureMap> MANSION = create("mansion");
    /** 폐광 */
    public static final ResourceKey<StructureMap> MINESHAFT = create("mineshaft");
    /** 바다 폐허 */
    public static final ResourceKey<StructureMap> MONUMENT = create("monument");
    /** 약탈자 전초기지 */
    public static final ResourceKey<StructureMap> PILLAGER_OUTPOST = create("pillager_outpost");
    /** 시련의 회당 */
    public static final ResourceKey<StructureMap> TRIAL_CHAMBERS = create("trial_chambers");
    /** 사막 마을 */
    public static final ResourceKey<StructureMap> VILLAGE_DESERT = create("village_desert");
    /** 평원 마을 */
    public static final ResourceKey<StructureMap> VILLAGE_PLAINS = create("village_plains");
    /** 사바나 마을 */
    public static final ResourceKey<StructureMap> VILLAGE_SAVANNA = create("village_savanna");
    /** 설원 마을 */
    public static final ResourceKey<StructureMap> VILLAGE_SNOWY = create("village_snowy");
    /** 타이가 마을 */
    public static final ResourceKey<StructureMap> VILLAGE_TAIGA = create("village_taiga");

    @NonNull
    private static ResourceKey<StructureMap> create(@NonNull String name) {
        return VPRegistries.STRUCTURE_MAP.createResourceKey(name);
    }
}
