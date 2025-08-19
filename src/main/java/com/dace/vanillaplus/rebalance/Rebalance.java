package com.dace.vanillaplus.rebalance;

import com.dace.vanillaplus.Tag;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.apache.commons.lang3.IntegerRange;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

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

    /**
     * 주민 거래 설정.
     */
    @UtilityClass
    public static final class VillagerTrade {
        /** 농부 거래 품목 */
        public static final List<OfferInfo> FARMER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.WHEAT, 18))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKIE, 13))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BEETROOT, 14))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.BREAD, 8))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.POTATO, 23))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.MUSHROOM_STEW))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.CARROT, 20))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BEETROOT_SOUP))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Blocks.PUMPKIN, 5))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.BAKED_POTATO, 8))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Blocks.MELON, 4))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.APPLE, 5))
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.CAKE))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.PUMPKIN_PIE, 4))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, (entity, randomSource) -> {
                                    ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW);

                                    SuspiciousStewEffects.Entry[] effects = {
                                            new SuspiciousStewEffects.Entry(MobEffects.SATURATION, 7),
                                            new SuspiciousStewEffects.Entry(MobEffects.NIGHT_VISION, 600),
                                            new SuspiciousStewEffects.Entry(MobEffects.JUMP_BOOST, 600),
                                            new SuspiciousStewEffects.Entry(MobEffects.REGENERATION, 300),
                                            new SuspiciousStewEffects.Entry(MobEffects.FIRE_RESISTANCE, 600)
                                    };

                                    itemStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS,
                                            new SuspiciousStewEffects(Collections.singletonList(effects[randomSource.nextInt(effects.length)])));

                                    return itemStack;
                                })
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.GLISTERING_MELON_SLICE, 5))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.GOLDEN_CARROT, 5))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 7, ItemStackFunction.item(Items.GOLDEN_APPLE))
                                .sellItem(Supply.MODERATE, 12, ItemStackFunction.item(Items.TORCHFLOWER_SEEDS))
                                .build()));
        /** 어부 거래 품목 */
        public static final List<OfferInfo> FISHERMAN = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.COD, 14))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKED_COD, 7))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.STRING, 18))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKED_SALMON, 7))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.SALMON, 12))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.COD_BUCKET))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.SALMON_BUCKET))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.TROPICAL_FISH_BUCKET))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.PUFFERFISH_BUCKET))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.TROPICAL_FISH, 9))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.CAMPFIRE))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .typeSpecific(Map.of(
                                        VillagerType.PLAINS, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.CHERRY_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.BIRCH_BOAT))
                                                .build(),
                                        VillagerType.TAIGA, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.SPRUCE_BOAT))
                                                .build(),
                                        VillagerType.SNOW, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.SPRUCE_BOAT))
                                                .build(),
                                        VillagerType.DESERT, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.JUNGLE_BOAT))
                                                .build(),
                                        VillagerType.JUNGLE, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.BAMBOO_RAFT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.JUNGLE_BOAT))
                                                .build(),
                                        VillagerType.SAVANNA, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.ACACIA_BOAT))
                                                .build(),
                                        VillagerType.SWAMP, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.MANGROVE_BOAT))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DARK_OAK_BOAT))
                                                .build()))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.INK_SAC, 8))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.PUFFERFISH, 4))
                                .sellEnchantedItem(Supply.SCARCE, 0.7, ItemStackFunction.item(Items.FISHING_ROD), EnchantmentLevel.LOW)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.DRIED_KELP, 9))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.DRIED_KELP_BLOCK, 1))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.7, ItemStackFunction.item(Items.FISHING_ROD), EnchantmentLevel.MEDIUM)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 7, (entity, randomSource) -> {
                                    ItemStack itemStack = new ItemStack(Items.AXOLOTL_BUCKET);
                                    itemStack.set(DataComponents.AXOLOTL_VARIANT, Axolotl.Variant.getCommonSpawnVariant(randomSource));

                                    return itemStack;
                                })
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.7, ItemStackFunction.item(Items.FISHING_ROD), EnchantmentLevel.HIGHEST)
                                .build()));
        /** 양치기 거래 품목 */
        public static final List<OfferInfo> SHEPHERD = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.WHITE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIGHT_GRAY_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GRAY_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BLACK_DYE, 16))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.RED_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.ORANGE_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.YELLOW_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.LIME_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.GREEN_WOOL, 5))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.WHITE_WOOL, 14))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.LIGHT_GRAY_WOOL, 14))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.GRAY_WOOL, 14))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.BLACK_WOOL, 14))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.BROWN_WOOL, 14))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.CYAN_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.BLUE_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.PURPLE_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.MAGENTA_WOOL, 5))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.PINK_WOOL, 5))
                                        .build())
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BROWN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.RED_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.ORANGE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.YELLOW_DYE, 16))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.WHITE_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.GRAY_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.BLACK_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.BROWN_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.RED_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.ORANGE_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.YELLOW_CARPET, 10))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIME_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GREEN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.CYAN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIGHT_BLUE_DYE, 16))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.LIME_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.GREEN_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.CYAN_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.BLUE_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.PURPLE_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.MAGENTA_CARPET, 10))
                                        .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Blocks.PINK_CARPET, 10))
                                        .build())
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BLUE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PURPLE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.MAGENTA_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PINK_DYE, 16))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_BED))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_BED))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.LEAD, 4))
                                .sellEnchantedItem(Supply.MODERATE, 0.4, ItemStackFunction.item(Items.SHEARS), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.TOOLS)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_BANNER))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.MODERATE, 0.4, ItemStackFunction.item(Items.SHEARS), EnchantmentLevel.HIGH,
                                        EnchantmentType.TOOLS)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.FIELD_MASONED_BANNER_PATTERN))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.BORDURE_INDENTED_BANNER_PATTERN))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.FLOWER_BANNER_PATTERN))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.SKULL_BANNER_PATTERN))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.CREEPER_BANNER_PATTERN))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.4, ItemStackFunction.item(Items.SHEARS), EnchantmentLevel.HIGHEST,
                                        EnchantmentType.TOOLS)
                                .build()));
        /** 화살 제조인 거래 품목 */
        public static final List<OfferInfo> FLETCHER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.FEATHER, 20))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAVEL, 20),
                                        ItemStackFunction.item(Items.FLINT, 20))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.STICK, 50))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.ARROW, 16))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.STRING, 14))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.ARROW, 12),
                                        ItemStackFunction.item(Items.SPECTRAL_ARROW, 12))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.FLINT, 22))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.SPECTRAL_ARROW, 7))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GUNPOWDER, 9))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Blocks.TARGET))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.TRIPWIRE_HOOK, 7))
                                .sellEnchantedItem(Supply.SCARCE, 1.1, ItemStackFunction.item(Items.BOW), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.WEAPONS)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 8),
                                        ItemStackFunction.tippedArrow(Potions.SLOWNESS, 8))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 8),
                                        ItemStackFunction.tippedArrow(Potions.POISON, 8))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 8),
                                        ItemStackFunction.tippedArrow(Potions.WEAKNESS, 8))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.15, ItemStackFunction.item(Items.CROSSBOW),
                                        EnchantmentLevel.MEDIUM, EnchantmentType.WEAPONS)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.STRONG_SLOWNESS, 7))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.LONG_SLOWNESS, 7))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.STRONG_POISON, 7))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.LONG_POISON, 7))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.LONG_WEAKNESS, 7))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARROW, 7),
                                        ItemStackFunction.tippedArrow(Potions.HARMING, 7))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.1, ItemStackFunction.item(Items.BOW), EnchantmentLevel.HIGH,
                                        EnchantmentType.WEAPONS)
                                .sellEnchantedItem(Supply.SCARCE, 1.15, ItemStackFunction.item(Items.CROSSBOW), EnchantmentLevel.HIGH,
                                        EnchantmentType.WEAPONS)
                                .build()));
        /** 사서 거래 품목 */
        public static final List<OfferInfo> LIBRARIAN = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PAPER, 24))
                                .sellItem(Supply.MODERATE, 6, ItemStackFunction.item(Blocks.BOOKSHELF))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.INK_SAC, 11))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.CLOCK, 3))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.BOOK, 3))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LANTERN, 4))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Blocks.CHISELED_BOOKSHELF, 4))
                                .sellEnchantedItem(Supply.MODERATE, 1.3, ItemStackFunction.item(Items.BOOK),
                                        ItemStackFunction.item(Items.BOOK), EnchantmentLevel.LOW)
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.WRITABLE_BOOK))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.COMPASS, 3))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GLOW_INK_SAC, 9))
                                .sellEnchantedItem(Supply.MODERATE, 1.3, ItemStackFunction.item(Items.BOOK),
                                        ItemStackFunction.item(Items.BOOK), EnchantmentLevel.LOW)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Blocks.LECTERN))
                                .sellItem(Supply.MODERATE, 15, ItemStackFunction.item(Items.NAME_TAG))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.3, ItemStackFunction.item(Items.BOOK),
                                        ItemStackFunction.item(Items.BOOK), EnchantmentLevel.MEDIUM)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.3, ItemStackFunction.item(Items.BOOK),
                                        ItemStackFunction.item(Items.BOOK), EnchantmentLevel.HIGH)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.3, ItemStackFunction.item(Items.BOOK),
                                        ItemStackFunction.item(Items.BOOK), EnchantmentLevel.HIGHEST)
                                .build()));
        /** 지도 제작자 거래 품목 */
        public static final List<OfferInfo> CARTOGRAPHER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PAPER, 24))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.MAP))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.GLASS_PANE, 16))
                                .sellItem(Supply.MODERATE, 3, (entity, randomSource) ->
                                        entity.level() instanceof ServerLevel serverLevel
                                                ? MapItem.create(serverLevel, entity.getBlockX(), entity.getBlockZ(), (byte) 0, true,
                                                false)
                                                : null)
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.INK_SAC, 11))
                                .typeSpecific(Map.of(
                                        VillagerType.PLAINS, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.TAIGA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_SWAMP), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_SNOWY), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SNOW, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_SWAMP), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.DESERT, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_JUNGLE), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.JUNGLE, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_SWAMP), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_DESERT), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SAVANNA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_JUNGLE), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_DESERT), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SWAMP, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.MINESHAFT), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.PILLAGER_OUTPOST), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_SNOWY), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 7, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.EXPLORER_JUNGLE), PriceMultiplier.HIGH)
                                                .build()))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.COMPASS))
                                .typeSpecific(Map.of(
                                        VillagerType.PLAINS, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_SAVANNA), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_TAIGA), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.TAIGA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_PLAINS), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_SNOWY), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SNOW, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_TAIGA), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_PLAINS), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.DESERT, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_PLAINS), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_SAVANNA), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.JUNGLE, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_SAVANNA), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_DESERT), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SAVANNA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_PLAINS), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_DESERT), PriceMultiplier.HIGH)
                                                .build(),
                                        VillagerType.SWAMP, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_PLAINS), PriceMultiplier.HIGH)
                                                .sellItem(Supply.SCARCE, 8, ItemStackFunction.item(Items.COMPASS),
                                                        ItemStackFunction.map(MapType.VILLAGE_SNOWY), PriceMultiplier.HIGH)
                                                .build()))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GLOW_INK_SAC, 9))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_BANNER))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_BANNER))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .typeSpecific(Map.of(
                                        VillagerType.PLAINS, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.CHERRY_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.BIRCH_SIGN, 3))
                                                .build(),
                                        VillagerType.TAIGA, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.SPRUCE_SIGN, 3))
                                                .build(),
                                        VillagerType.SNOW, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.SPRUCE_SIGN, 3))
                                                .build(),
                                        VillagerType.DESERT, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.JUNGLE_SIGN, 3))
                                                .build(),
                                        VillagerType.JUNGLE, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.BAMBOO_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.JUNGLE_SIGN, 3))
                                                .build(),
                                        VillagerType.SAVANNA, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.OAK_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.ACACIA_SIGN, 3))
                                                .build(),
                                        VillagerType.SWAMP, OfferInfo.OfferList.builder()
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.MANGROVE_SIGN, 3))
                                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DARK_OAK_SIGN, 3))
                                                .build()))
                                .sellItem(Supply.SCARCE, 9, ItemStackFunction.item(Items.COMPASS),
                                        ItemStackFunction.map(MapType.BURIED_TREASURE), PriceMultiplier.HIGH)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.ITEM_FRAME, 3))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.GLOW_ITEM_FRAME, 2))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.COMPASS),
                                        ItemStackFunction.map(MapType.OCEAN_MONUMENT), PriceMultiplier.HIGH)
                                .sellItem(Supply.SCARCE, 14, ItemStackFunction.item(Items.COMPASS),
                                        ItemStackFunction.map(MapType.WOODLAND_MANSION), PriceMultiplier.HIGH)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.MOJANG_BANNER_PATTERN))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Items.GLOBE_BANNER_PATTERN))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.SCARCE, 15, ItemStackFunction.item(Items.COMPASS),
                                        ItemStackFunction.map(MapType.TRIAL_CHAMBERS), PriceMultiplier.HIGH)
                                .sellItem(Supply.SCARCE, 22, ItemStackFunction.item(Items.COMPASS), ItemStackFunction.map(MapType.ANCIENT_CITY),
                                        PriceMultiplier.HIGH)
                                .build()));
        /** 성직자 거래 품목 */
        public static final List<OfferInfo> CLERIC = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.ROTTEN_FLESH, 27))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.REDSTONE, 6))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.RABBIT_FOOT, 2))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LAPIS_LAZULI, 4))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.GOLD_INGOT, 3))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.GLOWSTONE_DUST, 8))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.TURTLE_SCUTE, 3))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.AMETHYST_SHARD, 3))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.NETHER_WART, 18))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.ENDER_PEARL, 1))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.GLASS_BOTTLE, 8))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.AWKWARD))
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Blocks.CRYING_OBSIDIAN))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.EXPERIENCE_BOTTLE, 3))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.SWIFTNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.LEAPING))
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.NIGHT_VISION))
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.WATER_BREATHING))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.FIRE_RESISTANCE))
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.item(Items.GLASS_BOTTLE),
                                        ItemStackFunction.potion(Potions.REGENERATION))
                                .build()));
        /** 갑옷 제조인 거래 품목 */
        public static final List<OfferInfo> ARMORER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.COAL, 12))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.CHAINMAIL_HELMET))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.RAW_IRON, 5))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.CHAINMAIL_BOOTS))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.LAVA_BUCKET))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.CHAINMAIL_CHESTPLATE))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.IRON_INGOT, 4))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.CHAINMAIL_LEGGINGS))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DIAMOND))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.SCARCE, 1.1, ItemStackFunction.item(Items.IRON_HELMET),
                                                EnchantmentLevel.LOW)
                                        .sellEnchantedItem(Supply.SCARCE, 1.1, ItemStackFunction.item(Items.IRON_BOOTS),
                                                EnchantmentLevel.LOW)
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.ARMOR_STAND))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.SCARCE, 1.15, ItemStackFunction.item(Items.IRON_CHESTPLATE),
                                                EnchantmentLevel.LOW)
                                        .sellEnchantedItem(Supply.SCARCE, 1.15, ItemStackFunction.item(Items.IRON_LEGGINGS),
                                                EnchantmentLevel.LOW)
                                        .build())
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.2, ItemStackFunction.item(Items.DIAMOND_HELMET),
                                        EnchantmentLevel.MEDIUM)
                                .sellEnchantedItem(Supply.SCARCE, 1.2, ItemStackFunction.item(Items.DIAMOND_BOOTS),
                                        EnchantmentLevel.MEDIUM)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.25, ItemStackFunction.item(Items.DIAMOND_CHESTPLATE),
                                        EnchantmentLevel.MEDIUM)
                                .sellEnchantedItem(Supply.SCARCE, 1.25, ItemStackFunction.item(Items.DIAMOND_LEGGINGS),
                                        EnchantmentLevel.MEDIUM)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE))
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE))
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE))
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE))
                                .sellItem(Supply.SCARCE, 12, ItemStackFunction.item(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1.2, ItemStackFunction.item(Items.DIAMOND_HELMET), EnchantmentLevel.HIGH)
                                .sellEnchantedItem(Supply.SCARCE, 1.2, ItemStackFunction.item(Items.DIAMOND_BOOTS), EnchantmentLevel.HIGH)
                                .sellEnchantedItem(Supply.SCARCE, 1.25, ItemStackFunction.item(Items.DIAMOND_CHESTPLATE),
                                        EnchantmentLevel.HIGH)
                                .sellEnchantedItem(Supply.SCARCE, 1.25, ItemStackFunction.item(Items.DIAMOND_LEGGINGS),
                                        EnchantmentLevel.HIGH)
                                .build()));
        /** 무기 대장장이 거래 품목 */
        public static final List<OfferInfo> WEAPONSMITH = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.COAL, 12))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_SWORD))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.RAW_IRON, 5))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_AXE))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.STICK, 50))
                                .sellEnchantedItem(Supply.MODERATE, 0.9, ItemStackFunction.item(Items.IRON_SWORD), EnchantmentLevel.LOW,
                                        EnchantmentType.WEAPONS)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.IRON_INGOT, 4))
                                .sellEnchantedItem(Supply.MODERATE, 0.9, ItemStackFunction.item(Items.IRON_AXE), EnchantmentLevel.LOW,
                                        EnchantmentType.WEAPONS)
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DIAMOND))
                                .sellEnchantedItem(Supply.SCARCE, 0.9, ItemStackFunction.item(Items.IRON_SWORD), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.WEAPONS)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GUNPOWDER, 9))
                                .sellEnchantedItem(Supply.SCARCE, 0.9, ItemStackFunction.item(Items.IRON_AXE), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.WEAPONS)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_SWORD), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.WEAPONS)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_AXE), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.WEAPONS)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.item(Blocks.TNT))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.FIRE_CHARGE, 5))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_SWORD), EnchantmentLevel.HIGH,
                                        EnchantmentType.WEAPONS)
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_AXE), EnchantmentLevel.HIGH,
                                        EnchantmentType.WEAPONS)
                                .build()));
        /** 도구 대장장이 거래 품목 */
        public static final List<OfferInfo> TOOLSMITH = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.COAL, 12))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_HOE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_SHOVEL))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.RAW_IRON, 5))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_AXE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.IRON_PICKAXE))
                                        .build())
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.STICK, 50))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.MODERATE, 0.85, ItemStackFunction.item(Items.IRON_HOE),
                                                EnchantmentLevel.LOW, EnchantmentType.TOOLS)
                                        .sellEnchantedItem(Supply.MODERATE, 0.85, ItemStackFunction.item(Items.IRON_SHOVEL),
                                                EnchantmentLevel.LOW, EnchantmentType.TOOLS)
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.IRON_INGOT, 4))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.MODERATE, 0.9, ItemStackFunction.item(Items.IRON_AXE),
                                                EnchantmentLevel.LOW, EnchantmentType.TOOLS)
                                        .sellEnchantedItem(Supply.MODERATE, 0.9, ItemStackFunction.item(Items.IRON_PICKAXE),
                                                EnchantmentLevel.LOW, EnchantmentType.TOOLS)
                                        .build())
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DIAMOND))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.SCARCE, 0.85, ItemStackFunction.item(Items.IRON_HOE),
                                                EnchantmentLevel.MEDIUM, EnchantmentType.TOOLS)
                                        .sellEnchantedItem(Supply.SCARCE, 0.85, ItemStackFunction.item(Items.IRON_SHOVEL),
                                                EnchantmentLevel.MEDIUM, EnchantmentType.TOOLS)
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.GOLD_INGOT, 3))
                                .group(OfferInfo.OfferList.builder()
                                        .sellEnchantedItem(Supply.SCARCE, 0.9, ItemStackFunction.item(Items.IRON_AXE),
                                                EnchantmentLevel.MEDIUM, EnchantmentType.TOOLS)
                                        .sellEnchantedItem(Supply.SCARCE, 0.9, ItemStackFunction.item(Items.IRON_PICKAXE),
                                                EnchantmentLevel.MEDIUM, EnchantmentType.TOOLS)
                                        .build())
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.95, ItemStackFunction.item(Items.DIAMOND_HOE), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.TOOLS)
                                .sellEnchantedItem(Supply.SCARCE, 0.95, ItemStackFunction.item(Items.DIAMOND_SHOVEL),
                                        EnchantmentLevel.MEDIUM, EnchantmentType.TOOLS)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_AXE), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.TOOLS)
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_PICKAXE), EnchantmentLevel.MEDIUM,
                                        EnchantmentType.TOOLS)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 10, ItemStackFunction.item(Blocks.BELL))
                                .sellItem(Supply.MODERATE, 8, ItemStackFunction.item(Blocks.DAMAGED_ANVIL), ItemStackFunction.item(Blocks.ANVIL))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.95, ItemStackFunction.item(Items.DIAMOND_HOE), EnchantmentLevel.HIGH,
                                        EnchantmentType.TOOLS)
                                .sellEnchantedItem(Supply.SCARCE, 0.95, ItemStackFunction.item(Items.DIAMOND_SHOVEL), EnchantmentLevel.HIGH,
                                        EnchantmentType.TOOLS)
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_AXE), EnchantmentLevel.HIGH,
                                        EnchantmentType.TOOLS)
                                .sellEnchantedItem(Supply.SCARCE, 1, ItemStackFunction.item(Items.DIAMOND_PICKAXE), EnchantmentLevel.HIGH,
                                        EnchantmentType.TOOLS)
                                .build()));
        /** 도살업자 거래 품목 */
        public static final List<OfferInfo> BUTCHER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.RABBIT, 8))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKED_RABBIT, 7))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.MUTTON, 14))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKED_MUTTON, 6))
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.CHICKEN, 14))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.COOKED_CHICKEN, 6))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.SWEET_BERRIES, 14))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.CAMPFIRE))
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PORKCHOP, 14))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.COOKED_PORKCHOP, 5))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BEEF, 14))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.COOKED_BEEF, 5))
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.SMOKER, 2))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.RABBIT_FOOT, 2))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.RABBIT_HIDE, 10))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LEATHER, 4))
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .typeSpecific(Map.of(
                                        VillagerType.PLAINS, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BROWN_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLUE_EGG, 7))
                                                .build(),
                                        VillagerType.TAIGA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLUE_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build(),
                                        VillagerType.SNOW, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLUE_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build(),
                                        VillagerType.DESERT, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BROWN_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build(),
                                        VillagerType.JUNGLE, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BROWN_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build(),
                                        VillagerType.SAVANNA, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BROWN_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build(),
                                        VillagerType.SWAMP, OfferInfo.OfferList.builder()
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLUE_EGG, 7))
                                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.EGG, 7))
                                                .build()))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.RABBIT_STEW))
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.item(Items.ARMADILLO_SCUTE, 3))
                                .build()));
        /** 가죽 세공인 거래 품목 */
        public static final List<OfferInfo> LEATHERWORKER = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LEATHER, 5))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.WHITE_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LIGHT_GRAY_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.GRAY_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLACK_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BROWN_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.RED_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.ORANGE_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.YELLOW_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LIME_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.GREEN_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.CYAN_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.LIGHT_BLUE_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.BLUE_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.PURPLE_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.MAGENTA_BUNDLE))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.PINK_BUNDLE))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.RABBIT_HIDE, 8))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 2, ItemStackFunction.leatherItem(Items.LEATHER_HELMET))
                                        .sellItem(Supply.MODERATE, 2, ItemStackFunction.leatherItem(Items.LEATHER_BOOTS))
                                        .sellItem(Supply.MODERATE, 3, ItemStackFunction.leatherItem(Items.LEATHER_CHESTPLATE))
                                        .sellItem(Supply.MODERATE, 3, ItemStackFunction.leatherItem(Items.LEATHER_LEGGINGS))
                                        .build())
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.FLINT, 22))
                                .sellEnchantedItem(Supply.MODERATE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_HELMET),
                                        EnchantmentLevel.HIGH)
                                .sellEnchantedItem(Supply.MODERATE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_BOOTS),
                                        EnchantmentLevel.HIGH)
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.WATER_BUCKET))
                                .sellEnchantedItem(Supply.MODERATE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_CHESTPLATE),
                                        EnchantmentLevel.HIGH)
                                .sellEnchantedItem(Supply.MODERATE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_LEGGINGS),
                                        EnchantmentLevel.HIGH)
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.TURTLE_SCUTE, 3))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Items.SADDLE))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.WHITE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIGHT_GRAY_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GRAY_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BLACK_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BROWN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.RED_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.ORANGE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.YELLOW_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIME_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.GREEN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.CYAN_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.LIGHT_BLUE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.BLUE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PURPLE_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.MAGENTA_DYE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.PINK_DYE, 16))
                                        .build())
                                .sellEnchantedItem(Supply.SCARCE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_HELMET),
                                        EnchantmentLevel.HIGHEST)
                                .sellEnchantedItem(Supply.SCARCE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_BOOTS),
                                        EnchantmentLevel.HIGHEST)
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 2, ItemStackFunction.leatherItem(Items.LEATHER_HORSE_ARMOR))
                                .sellItem(Supply.MODERATE, 4, ItemStackFunction.leatherItem(Items.TURTLE_HELMET))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_CHESTPLATE),
                                        EnchantmentLevel.HIGHEST)
                                .sellEnchantedItem(Supply.SCARCE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_LEGGINGS),
                                        EnchantmentLevel.HIGHEST)
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.WHITE_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.LIGHT_GRAY_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.GRAY_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.BLACK_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.BROWN_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.RED_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.ORANGE_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.YELLOW_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.LIME_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.GREEN_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.CYAN_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.LIGHT_BLUE_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.BLUE_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.PURPLE_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.MAGENTA_HARNESS))
                                .sellItem(Supply.MODERATE, 3, ItemStackFunction.item(Items.PINK_HARNESS))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellEnchantedItem(Supply.SCARCE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_HELMET),
                                        EnchantmentLevel.HIGHEST)
                                .sellEnchantedItem(Supply.SCARCE, 0.45, ItemStackFunction.leatherItem(Items.LEATHER_BOOTS),
                                        EnchantmentLevel.HIGHEST)
                                .sellEnchantedItem(Supply.SCARCE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_CHESTPLATE),
                                        EnchantmentLevel.HIGHEST)
                                .sellEnchantedItem(Supply.SCARCE, 0.5, ItemStackFunction.leatherItem(Items.LEATHER_LEGGINGS),
                                        EnchantmentLevel.HIGHEST)
                                .build()));
        /** 석공 거래 품목 */
        public static final List<OfferInfo> MASON = List.of(
                new OfferInfo(Level.L1_NOVICE,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Items.CLAY_BALL, 18))
                                .sellItem(Supply.ABUNDANT, 1, ItemStackFunction.item(Items.BRICK, 16))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.STONE, 23))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_TERRACOTTA, 20))
                                        .build())
                                .build()),
                new OfferInfo(Level.L2_APPRENTICE,
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.GRANITE, 18))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.ANDESITE, 18))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.DIORITE, 18))
                                        .build())
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_GRANITE, 25))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_ANDESITE, 25))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_DIORITE, 25))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_TUFF, 25))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .group(OfferInfo.OfferList.builder()
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.CALCITE, 16))
                                        .buyItem(Supply.ABUNDANT, ItemStackFunction.item(Blocks.TUFF, 18))
                                        .build())
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_TERRACOTTA, 20))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_TERRACOTTA, 20))
                                        .build())
                                .build()),
                new OfferInfo(Level.L3_JOURNEYMAN,
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.DEEPSLATE, 22))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.SMOOTH_STONE, 17))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.STONE_BRICKS, 17))
                                        .build())
                                .build(),
                        OfferInfo.OfferList.builder()
                                .buyItem(Supply.MODERATE, ItemStackFunction.item(Items.QUARTZ, 26))
                                .group(OfferInfo.OfferList.builder()
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.QUARTZ_BLOCK, 12))
                                        .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.QUARTZ_BRICKS, 12))
                                        .build())
                                .build()),
                new OfferInfo(Level.L4_EXPERT,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_CONCRETE, 16))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_CONCRETE, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_CONCRETE, 16))
                                .build()),
                new OfferInfo(Level.L5_MASTER,
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_BLACKSTONE, 25))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.POLISHED_BLACKSTONE_BRICKS, 25))
                                .build(),
                        OfferInfo.OfferList.builder()
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.WHITE_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GRAY_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLACK_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BROWN_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.RED_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.ORANGE_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.YELLOW_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIME_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.GREEN_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.CYAN_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.BLUE_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PURPLE_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.MAGENTA_GLAZED_TERRACOTTA, 16))
                                .sellItem(Supply.MODERATE, 1, ItemStackFunction.item(Blocks.PINK_GLAZED_TERRACOTTA, 16))
                                .build()));

        /**
         * 주민 등급.
         */
        @AllArgsConstructor
        private enum Level {
            /** 1레벨 경험치 (원본: 1, 2) */
            L1_NOVICE(1, 2),
            /** 2레벨 경험치 (원본: 5, 10) */
            L2_APPRENTICE(5, 8),
            /** 3레벨 경험치 (원본: 10, 20) */
            L3_JOURNEYMAN(10, 16),
            /** 4레벨 경험치 (원본: 15, 30) */
            L4_EXPERT(15, 24),
            /** 5레벨 경험치 (원본: 30, 30) */
            L5_MASTER(20, 32);

            /** 판매 경험치 */
            private final int sellXP;
            /** 구매 경험치 */
            private final int buyXP;
        }

        /**
         * 거래 품목의 재고 수량.
         */
        @AllArgsConstructor
        private enum Supply {
            /** 많음 (원본: 16) */
            ABUNDANT(10),
            /** 보통 (원본: 12) */
            MODERATE(6),
            /** 적음 (원본: 3) */
            SCARCE(2);

            /** 최대 거래 횟수 */
            private final int maxTradeCount;
        }

        /**
         * 거래 품목의 가격 배수.
         */
        @AllArgsConstructor
        private enum PriceMultiplier {
            /** 낮음 (원본: 0.05) */
            LOW(0.05F),
            /** 높음 (원본: 0.2) */
            HIGH(0.2F);

            /** 가격 배수 값 */
            private final float value;
        }

        /**
         * 마법이 부여된 거래 품목의 마법 부여 레벨. (원본: 5~19).
         */
        @AllArgsConstructor
        private enum EnchantmentLevel {
            /** 낮음 */
            LOW(IntegerRange.of(8, 16), Tag.Enchantments.TRADEABLE),
            /** 중간 */
            MEDIUM(IntegerRange.of(12, 24), Tag.Enchantments.TRADEABLE),
            /** 높음 */
            HIGH(IntegerRange.of(18, 30), Tag.Enchantments.TRADEABLE),
            /** 매우 높음 */
            HIGHEST(IntegerRange.of(24, 30), Tag.Enchantments.TRADEABLE_TREASURE);

            /** 마법 부여 레벨 값 */
            private final IntegerRange value;
            /** 마법 부여 데이터 태그 */
            private final TagKey<Enchantment> enchantmentTagKey;
        }

        /**
         * 마법 부여 유형.
         */
        @AllArgsConstructor
        private enum EnchantmentType {
            /** 전체 */
            ALL((itemStack, enchantmentHolder) -> true),
            /** 도구 */
            TOOLS((itemStack, enchantmentHolder) ->
                    !itemStack.is(ItemTags.AXES) || enchantmentHolder.is(Tag.Enchantments.AXE_TOOL)),
            /** 무기 */
            WEAPONS((itemStack, enchantmentHolder) ->
                    !itemStack.is(ItemTags.AXES) || enchantmentHolder.is(Tag.Enchantments.AXE_WEAPON));

            /** 인첸트 가능 여부 반환 시 실행할 작업 */
            private final BiPredicate<ItemStack, Holder<Enchantment>> canEnchant;
        }

        /**
         * 지도 유형.
         */
        @AllArgsConstructor
        private enum MapType {
            /** 폐광 */
            MINESHAFT(StructureTags.MINESHAFT, "mineshaft", ARGB.color(92, 86, 71)),
            /** 약탈자 전초기지 */
            PILLAGER_OUTPOST(Tag.Structures.PILLAGER_OUTPOST, "pillager_outpost", ARGB.color(107, 56, 18)),
            /** 고대 도시 */
            ANCIENT_CITY(Tag.Structures.ANCIENT_CITY, "ancient_city", ARGB.color(40, 31, 97)),
            /** 사막 탐험 */
            EXPLORER_DESERT(Tag.Structures.ON_DESERT_EXPLORER_MAPS, "explorer_desert",
                    ARGB.color(163, 143, 73)),
            /** 설원 탐험 */
            EXPLORER_SNOWY(Tag.Structures.ON_SNOWY_EXPLORER_MAPS, "explorer_snowy", ARGB.color(89, 154, 189)),
            /** 정글 탐험 */
            EXPLORER_JUNGLE(StructureTags.ON_JUNGLE_EXPLORER_MAPS, "explorer_jungle", MapDecorationTypes.JUNGLE_TEMPLE,
                    ARGB.color(92, 130, 49)),
            /** 늪 탐험 */
            EXPLORER_SWAMP(StructureTags.ON_SWAMP_EXPLORER_MAPS, "explorer_swamp", MapDecorationTypes.SWAMP_HUT,
                    ARGB.color(40, 99, 39)),
            /** 땅에 묻힌 보물 */
            BURIED_TREASURE(StructureTags.ON_TREASURE_MAPS, "buried_treasure", MapDecorationTypes.RED_X,
                    ARGB.color(219, 203, 20)),
            /** 타이가 마을 */
            VILLAGE_TAIGA(StructureTags.ON_TAIGA_VILLAGE_MAPS, "village_taiga", MapDecorationTypes.TAIGA_VILLAGE,
                    ARGB.color(111, 173, 163)),
            /** 설원 마을 */
            VILLAGE_SNOWY(StructureTags.ON_SNOWY_VILLAGE_MAPS, "village_snowy", MapDecorationTypes.SNOWY_VILLAGE,
                    ARGB.color(101, 188, 201)),
            /** 사바나 마을 */
            VILLAGE_SAVANNA(StructureTags.ON_SAVANNA_VILLAGE_MAPS, "village_savanna", MapDecorationTypes.SAVANNA_VILLAGE,
                    ARGB.color(181, 134, 63)),
            /** 평원 마을 */
            VILLAGE_PLAINS(StructureTags.ON_PLAINS_VILLAGE_MAPS, "village_plains", MapDecorationTypes.PLAINS_VILLAGE,
                    ARGB.color(85, 184, 62)),
            /** 사막 마을 */
            VILLAGE_DESERT(StructureTags.ON_DESERT_VILLAGE_MAPS, "village_desert", MapDecorationTypes.DESERT_VILLAGE,
                    ARGB.color(181, 170, 69)),
            /** 바다 폐허 */
            OCEAN_MONUMENT(StructureTags.ON_OCEAN_EXPLORER_MAPS, "monument", MapDecorationTypes.OCEAN_MONUMENT,
                    ARGB.color(28, 57, 145)),
            /** 삼림 대저택 */
            WOODLAND_MANSION(StructureTags.ON_WOODLAND_EXPLORER_MAPS, "mansion", MapDecorationTypes.WOODLAND_MANSION,
                    ARGB.color(140, 87, 22)),
            /** 시련의 회당 */
            TRIAL_CHAMBERS(StructureTags.ON_TRIAL_CHAMBERS_MAPS, "trial_chambers", MapDecorationTypes.TRIAL_CHAMBERS,
                    ARGB.color(196, 99, 24));

            private final TagKey<Structure> destination;
            private final String displayName;
            private final Holder<MapDecorationType> destinationType;
            private final int color;

            MapType(TagKey<Structure> destination, String displayName, int color) {
                this(destination, displayName, MapDecorationTypes.TARGET_POINT, color);
            }

            /**
             * 지도 아이템을 생성하여 반환한다.
             *
             * @param entity 엔티티
             * @return 지도 아이템
             */
            @Nullable
            private ItemStack createMap(@NonNull Entity entity) {
                if (!(entity.level() instanceof ServerLevel serverLevel))
                    return null;

                BlockPos blockPos = serverLevel.findNearestMapStructure(destination, entity.blockPosition(), 100, true);
                if (blockPos == null)
                    return null;

                ItemStack itemstack = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
                itemstack.set(DataComponents.ITEM_NAME, Component.translatable("filled_map." + displayName));

                MapItem.renderBiomePreviewMap(serverLevel, itemstack);
                MapItemSavedData.addTargetDecoration(itemstack, blockPos, "+", destinationType);

                itemstack.set(DataComponents.MAP_COLOR, new MapItemColor(color));

                return itemstack;
            }
        }

        /**
         * 거래에 사용되는 아이템을 생성하는 인터페이스.
         */
        @FunctionalInterface
        private interface ItemStackFunction {
            /**
             * 일반 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param itemLike 아이템
             * @param amount   수량
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction item(@NonNull ItemLike itemLike, int amount) {
                return (entity, randomSource) -> new ItemStack(itemLike, amount);
            }

            /**
             * 일반 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param itemLike 아이템
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction item(@NonNull ItemLike itemLike) {
                return item(itemLike, 1);
            }

            /**
             * 물약 화살 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param potionHolder 물약 홀더 인스턴스
             * @param amount       수량
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction tippedArrow(@NonNull Holder<@NonNull Potion> potionHolder, int amount) {
                return (entity, randomSource) -> {
                    ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW, amount);
                    itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));

                    return itemStack;
                };
            }

            /**
             * 물약 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param potionHolder 물약 홀더 인스턴스
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction potion(@NonNull Holder<@NonNull Potion> potionHolder) {
                return (entity, randomSource) -> PotionContents.createItemStack(Items.POTION, potionHolder);
            }

            /**
             * 지도 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param mapType 지도 유형
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction map(@NonNull MapType mapType) {
                return (entity, randomSource) -> mapType.createMap(entity);
            }

            /**
             * 무작위 색상의 염색 가능한 가죽 아이템을 반환하는 {@link ItemStackFunction}을 생성한다.
             *
             * @param item 염색 가능한 가죽 아이템
             * @return {@link ItemStackFunction}
             */
            @NonNull
            private static ItemStackFunction leatherItem(@NonNull Item item) {
                return (entity, randomSource) -> {
                    ItemStack itemStack = new ItemStack(item);
                    if (!itemStack.is(ItemTags.DYEABLE))
                        return itemStack;

                    ImmutableList.Builder<DyeItem> builder = ImmutableList.builder();

                    builder.add(getRandomDyeItem(randomSource));
                    if (randomSource.nextDouble() > 0.6)
                        builder.add(getRandomDyeItem(randomSource));

                    return DyedItemColor.applyDyes(itemStack, builder.build());
                };
            }

            @NonNull
            private static DyeItem getRandomDyeItem(@NonNull RandomSource randomSource) {
                return DyeItem.byColor(DyeColor.byId(randomSource.nextInt(DyeColor.values().length)));
            }

            /**
             * 아이템을 생성하여 반환한다.
             *
             * @param entity       대상 엔티티 (주민)
             * @param randomSource 랜덤 소스
             * @return 아이템
             */
            @Nullable
            ItemStack apply(@NonNull Entity entity, @NonNull RandomSource randomSource);
        }

        /**
         * 거래 품목 정보 클래스.
         */
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class OfferInfo {
            /** 주민 등급 */
            @NonNull
            private final Level level;
            /** 첫번째 거래 품목 목록 */
            @NonNull
            private final OfferList primary;
            /** 두번째 거래 품목 목록 */
            @NonNull
            private final OfferList secondary;

            @NonNull
            private static ItemCost getItemCost(@NonNull ItemStack itemStack) {
                return new ItemCost(itemStack.getItem(), itemStack.getCount());
            }

            /**
             * 거래 품목 정보를 ItemListing 배열 인스턴스로 변환하여 반환한다.
             *
             * @return ItemListing 배열 인스턴스
             */
            @NonNull
            public VillagerTrades.ItemListing @NonNull [] toArray() {
                return new VillagerTrades.ItemListing[]{primary.itemListingFunction.apply(this), secondary.itemListingFunction.apply(this)};
            }

            /**
             * 거래 품목 목록 인터페이스.
             */
            @AllArgsConstructor
            private static final class OfferList {
                /** ItemListing 반환에 실행할 작업 */
                private final Function<OfferInfo, VillagerTrades.ItemListing> itemListingFunction;

                /**
                 * 빌더 인스턴스를 생성하여 반환한다.
                 *
                 * @return {@link Builder}
                 */
                @NonNull
                private static Builder builder() {
                    return new Builder();
                }

                /**
                 * {@link OfferList}의 빌더 클래스.
                 */
                @AllArgsConstructor
                private static final class Builder {
                    private final ArrayList<Function<OfferInfo, VillagerTrades.ItemListing>> itemListingFunctions = new ArrayList<>();

                    @NonNull
                    private Builder add(@NonNull Function<OfferInfo, VillagerTrades.ItemListing> itemListingFunction) {
                        itemListingFunctions.add(itemListingFunction);
                        return this;
                    }

                    @NonNull
                    private Builder group(@NonNull OfferList offerList) {
                        return add(offerList.itemListingFunction);
                    }

                    /**
                     * 주민 타입(바이옴)에 따른 거래 품목 목록을 추가한다.
                     *
                     * @param offerListMap 주민 타입별 거래 품목 목록
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder typeSpecific(@NonNull Map<@NonNull ResourceKey<VillagerType>, @NonNull OfferList> offerListMap) {
                        return add(offerInfo -> (entity, randomSource) -> {
                            if (!(entity instanceof VillagerDataHolder villagerDataHolder))
                                return null;

                            ResourceKey<VillagerType> resourcekey = villagerDataHolder.getVillagerData().type().unwrapKey().orElse(null);
                            if (resourcekey == null)
                                return null;

                            return offerListMap.get(resourcekey).itemListingFunction.apply(offerInfo).getOffer(entity, randomSource);
                        });
                    }

                    /**
                     * 구매 품목을 추가한다.
                     *
                     * @param supply            수량
                     * @param itemStackFunction 구매 아이템 반환 시 실행할 작업
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder buyItem(@NonNull Supply supply, @NonNull ItemStackFunction itemStackFunction) {
                        return add(offerInfo -> new BuyItem(offerInfo.level, supply, itemStackFunction, PriceMultiplier.LOW));
                    }

                    /**
                     * 판매 품목을 추가한다.
                     *
                     * @param supply                    수량
                     * @param price                     가격
                     * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
                     * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
                     * @param priceMultiplier           가격 배수
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellItem(@NonNull Supply supply, int price, @NonNull ItemStackFunction requiredItemStackFunction,
                                             @NonNull ItemStackFunction itemStackFunction, @NonNull PriceMultiplier priceMultiplier) {
                        return add(offerInfo -> new SellItem(offerInfo.level, supply, price, requiredItemStackFunction, itemStackFunction,
                                priceMultiplier));
                    }

                    /**
                     * 판매 품목을 추가한다.
                     *
                     * @param supply                    수량
                     * @param price                     가격
                     * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
                     * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellItem(@NonNull Supply supply, int price, @NonNull ItemStackFunction requiredItemStackFunction,
                                             @NonNull ItemStackFunction itemStackFunction) {
                        return sellItem(supply, price, requiredItemStackFunction, itemStackFunction, PriceMultiplier.LOW);
                    }

                    /**
                     * 판매 품목을 추가한다.
                     *
                     * @param supply            수량
                     * @param price             가격
                     * @param itemStackFunction 판매 아이템 반환 시 실행할 작업
                     * @param priceMultiplier   가격 배수
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellItem(@NonNull Supply supply, int price, @NonNull ItemStackFunction itemStackFunction,
                                             @NonNull PriceMultiplier priceMultiplier) {
                        return sellItem(supply, price, (entity, randomSource) -> null, itemStackFunction, priceMultiplier);
                    }

                    /**
                     * 판매 품목을 추가한다.
                     *
                     * @param supply            수량
                     * @param price             가격
                     * @param itemStackFunction 판매 아이템 반환 시 실행할 작업
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellItem(@NonNull Supply supply, int price, @NonNull ItemStackFunction itemStackFunction) {
                        return sellItem(supply, price, (entity, randomSource) -> null, itemStackFunction, PriceMultiplier.LOW);
                    }

                    /**
                     * 마법 부여된 아이템 판매 품목을 추가한다.
                     *
                     * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
                     *
                     * @param supply                    수량
                     * @param priceModifier             가격 수정자
                     * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
                     * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
                     * @param enchantmentLevel          마법 부여 레벨
                     * @param enchantmentType           마법 부여 유형
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellEnchantedItem(@NonNull Supply supply, double priceModifier,
                                                      @NonNull ItemStackFunction requiredItemStackFunction,
                                                      @NonNull ItemStackFunction itemStackFunction, @NonNull EnchantmentLevel enchantmentLevel,
                                                      @NonNull EnchantmentType enchantmentType) {
                        return add(offerInfo -> new SellEnchantedItem(offerInfo.level, supply, priceModifier, requiredItemStackFunction,
                                itemStackFunction, enchantmentLevel, enchantmentType, PriceMultiplier.HIGH));
                    }

                    /**
                     * 마법 부여된 아이템 판매 품목을 추가한다.
                     *
                     * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
                     *
                     * @param supply                    수량
                     * @param priceModifier             가격 수정자
                     * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
                     * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
                     * @param enchantmentLevel          마법 부여 레벨
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellEnchantedItem(@NonNull Supply supply, double priceModifier,
                                                      @NonNull ItemStackFunction requiredItemStackFunction,
                                                      @NonNull ItemStackFunction itemStackFunction, @NonNull EnchantmentLevel enchantmentLevel) {
                        return sellEnchantedItem(supply, priceModifier, requiredItemStackFunction, itemStackFunction, enchantmentLevel,
                                EnchantmentType.ALL);
                    }

                    /**
                     * 마법 부여된 아이템 판매 품목을 추가한다.
                     *
                     * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
                     *
                     * @param supply            수량
                     * @param priceModifier     가격 수정자. 0으로 지정 시 {@link EnchantmentLevel#value}와 동일
                     * @param itemStackFunction 판매 아이템 반환 시 실행할 작업
                     * @param enchantmentLevel  마법 부여 레벨
                     * @param enchantmentType   마법 부여 유형
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellEnchantedItem(@NonNull Supply supply, double priceModifier, @NonNull ItemStackFunction itemStackFunction,
                                                      @NonNull EnchantmentLevel enchantmentLevel, @NonNull EnchantmentType enchantmentType) {
                        return sellEnchantedItem(supply, priceModifier, (entity, randomSource) -> null, itemStackFunction,
                                enchantmentLevel, enchantmentType);
                    }

                    /**
                     * 마법 부여된 아이템 판매 품목을 추가한다.
                     *
                     * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
                     *
                     * @param supply            수량
                     * @param priceModifier     가격 수정자. 0으로 지정 시 {@link EnchantmentLevel#value}와 동일
                     * @param itemStackFunction 판매 아이템 반환 시 실행할 작업
                     * @param enchantmentLevel  마법 부여 레벨
                     * @return {@link Builder}
                     */
                    @NonNull
                    private Builder sellEnchantedItem(@NonNull Supply supply, double priceModifier, @NonNull ItemStackFunction itemStackFunction,
                                                      @NonNull EnchantmentLevel enchantmentLevel) {
                        return sellEnchantedItem(supply, priceModifier, (entity, randomSource) -> null, itemStackFunction,
                                enchantmentLevel, EnchantmentType.ALL);
                    }

                    /**
                     * 거래 항목 목록 인스턴스를 생성하여 반환한다.
                     *
                     * @return {@link OfferList}
                     */
                    @NonNull
                    private OfferList build() {
                        return new OfferList(offerInfo -> (entity, randomSource) -> {
                            MerchantOffer offer = null;
                            while (offer == null)
                                offer = itemListingFunctions.get(randomSource.nextInt(itemListingFunctions.size())).apply(offerInfo)
                                        .getOffer(entity, randomSource);

                            return offer;
                        });
                    }
                }
            }

            /**
             * 구매 품목 클래스.
             *
             * @param level             주민 등급
             * @param supply            수량
             * @param itemStackFunction 구매 아이템 반환 시 실행할 작업
             * @param priceMultiplier   가격 배수
             */
            private record BuyItem(@NonNull Level level, @NonNull Supply supply, @NonNull ItemStackFunction itemStackFunction,
                                   @NonNull PriceMultiplier priceMultiplier) implements VillagerTrades.ItemListing {
                @Override
                @Nullable
                public MerchantOffer getOffer(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = itemStackFunction.apply(entity, randomSource);
                    if (itemStack == null)
                        return null;

                    return new MerchantOffer(getItemCost(itemStack), new ItemStack(Items.EMERALD), supply.maxTradeCount, level.buyXP,
                            priceMultiplier.value);
                }
            }

            /**
             * 판매 품목 클래스.
             *
             * @param level                     주민 등급
             * @param supply                    수량
             * @param price                     가격
             * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
             * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
             * @param priceMultiplier           가격 배수
             */
            private record SellItem(@NonNull Level level, @NonNull Supply supply, int price, @NonNull ItemStackFunction requiredItemStackFunction,
                                    @NonNull ItemStackFunction itemStackFunction, @NonNull PriceMultiplier priceMultiplier)
                    implements VillagerTrades.ItemListing {
                @Override
                @Nullable
                public MerchantOffer getOffer(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = itemStackFunction.apply(entity, randomSource);
                    if (itemStack == null)
                        return null;

                    ItemStack requiredItemStack = requiredItemStackFunction.apply(entity, randomSource);

                    return new MerchantOffer(getItemCost(new ItemStack(Items.EMERALD, price)),
                            requiredItemStack == null ? Optional.empty() : Optional.of(getItemCost(requiredItemStack)), itemStack,
                            supply.maxTradeCount, level.sellXP, priceMultiplier.value);
                }
            }

            /**
             * 마법 부여된 아이템 판매 품목 클래스.
             *
             * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
             *
             * @param level                     주민 등급
             * @param supply                    수량
             * @param priceModifier             가격 수정자
             * @param requiredItemStackFunction 추가 요구 아이템 반환 시 실행할 작업
             * @param itemStackFunction         판매 아이템 반환 시 실행할 작업
             * @param enchantmentLevel          마법 부여 레벨
             * @param enchantmentType           마법 부여 유형
             * @param priceMultiplier           가격 배수
             */
            private record SellEnchantedItem(@NonNull Level level, @NonNull Supply supply, double priceModifier,
                                             @NonNull ItemStackFunction requiredItemStackFunction, @NonNull ItemStackFunction itemStackFunction,
                                             @NonNull EnchantmentLevel enchantmentLevel, @NonNull EnchantmentType enchantmentType,
                                             @NonNull PriceMultiplier priceMultiplier) implements VillagerTrades.ItemListing {
                @Override
                @Nullable
                public MerchantOffer getOffer(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = itemStackFunction.apply(entity, randomSource);
                    if (itemStack == null)
                        return null;

                    IntegerRange value = enchantmentLevel.value;
                    int enchantLevel = value.getMinimum() + randomSource.nextInt(value.getMaximum());

                    Registry<Enchantment> enchantmentRegistry = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                    Stream<Holder<Enchantment>> enchantments = enchantmentRegistry
                            .getOrThrow(enchantmentLevel.enchantmentTagKey).stream()
                            .filter(enchantmentHolder -> enchantmentType.canEnchant.test(itemStack, enchantmentHolder));

                    ItemStack enchantedItem = EnchantmentHelper.enchantItem(randomSource, itemStack, enchantLevel, enchantments);

                    double finalPrice = enchantLevel * priceModifier;
                    if (enchantedItem.getEnchantments().keySet().stream()
                            .anyMatch(enchantment -> enchantment.containsTag(EnchantmentTags.DOUBLE_TRADE_PRICE)))
                        finalPrice *= 1.5;

                    ItemStack requiredItemStack = requiredItemStackFunction.apply(entity, randomSource);

                    return new MerchantOffer(getItemCost(new ItemStack(Items.EMERALD, Math.min((int) finalPrice, 64))),
                            requiredItemStack == null ? Optional.empty() : Optional.of(getItemCost(requiredItemStack)), enchantedItem,
                            supply.maxTradeCount, level.sellXP, priceMultiplier.value);
                }
            }
        }
    }
}
