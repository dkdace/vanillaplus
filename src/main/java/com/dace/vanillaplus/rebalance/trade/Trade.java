package com.dace.vanillaplus.rebalance.trade;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.apache.commons.lang3.IntegerRange;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 주민 거래 정보를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Trade {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<Trade>> CODEC = RegistryFixedCodec.create(VPRegistries.TRADE.getRegistryKey());
    /** JSON 코덱 */
    private static final Codec<Trade> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(OfferList.CODEC.listOf().xmap(Trade::fromListToMap, Trade::fromMapToList).fieldOf("trades")
                    .forGetter(trade -> trade.offerListMap))
            .apply(instance, Trade::new));

    /* 주민 등급별 거래 품목 목록 (주민 등급 : 거래 품목 목록) */
    private final Map<OfferList.Level, OfferList> offerListMap;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistries.TRADE.getRegistryKey(), DIRECT_CODEC);
    }

    /**
     * 주민 거래 품목을 {@link Map}에서 {@link List}로 변환한다.
     *
     * @param offerInfoMap 주민 등급별 거래 품목 목록
     * @return {@link List}
     */
    @NonNull
    private static List<OfferList> fromMapToList(@NonNull Map<OfferList.Level, OfferList> offerInfoMap) {
        return new ArrayList<>(offerInfoMap.values());
    }

    /**
     * 주민 거래 품목을 {@link List}에서 {@link Map}으로 변경한다.
     *
     * @param offerLists 주민 거래 품목 목록
     * @return {@link Map}
     */
    @NonNull
    private static Map<OfferList.Level, OfferList> fromListToMap(@NonNull List<OfferList> offerLists) {
        return offerLists.stream().collect(Collectors.toMap(offerList -> offerList.level, Function.identity()));
    }

    /**
     * 지정한 주민 등급에 해당하는 거래 품목 목록을 반환한다.
     *
     * @param level 주민 등급. 1~5 사이의 값
     * @return 거래 품목 목록
     * @throws IllegalArgumentException 인자값이 유효하지 않으면 발생
     * @see OfferList.Level
     */
    @NonNull
    public OfferList getOfferInfo(int level) {
        return offerListMap.get(OfferList.Level.fromInt(level));
    }

    /**
     * 거래 품목 목록 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OfferList {
        /** JSON 코덱 */
        private static final Codec<OfferList> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Level.CODEC.fieldOf("level").forGetter(offerList -> offerList.level),
                        OfferItem.CODEC.listOf().fieldOf("offers").forGetter(offerList -> offerList.offerItems))
                .apply(instance, OfferList::new));

        /** 주민 등급 */
        private final Level level;
        /** 거래 품목 목록 */
        private final List<OfferItem> offerItems;

        /**
         * 거래 품목 목록을 ItemListing 배열 인스턴스로 변환하여 반환한다.
         *
         * @return ItemListing 배열 인스턴스
         */
        @NonNull
        public VillagerTrades.ItemListing @NonNull [] toItemListings() {
            return offerItems.stream().map(offerItemList -> offerItemList.getItemListing(this))
                    .toArray(VillagerTrades.ItemListing[]::new);
        }

        /**
         * 주민 등급.
         */
        @AllArgsConstructor
        private enum Level {
            /** 1레벨 경험치 (원본: 1, 2) */
            L1_NOVICE(7, 14),
            /** 2레벨 경험치 (원본: 5, 10) */
            L2_APPRENTICE(14, 28),
            /** 3레벨 경험치 (원본: 10, 20) */
            L3_JOURNEYMAN(21, 42),
            /** 4레벨 경험치 (원본: 15, 30) */
            L4_EXPERT(28, 56),
            /** 5레벨 경험치 (원본: 30, 30) */
            L5_MASTER(35, 70);

            /** JSON 코덱 */
            private static final Codec<Level> CODEC = Codec.INT.xmap(Level::fromInt, Level::toInt);
            /** 판매 경험치 */
            private final int sellXP;
            /** 구매 경험치 */
            private final int buyXP;

            /**
             * 주민 등급을 {@code int}에서 {@link Level}로 변환한다.
             *
             * @param level 주민 등급 값
             * @return {@link Level}
             * @throws IllegalArgumentException 인자값이 유효하지 않으면 발생
             */
            @NonNull
            private static Level fromInt(int level) {
                Level[] levels = values();
                Validate.inclusiveBetween(1, levels.length, level, "1 <= level <= %d (%d)", levels.length, level);

                return levels[level - 1];
            }

            /**
             * 주민 등급을 {@link Level}에서 {@code int}로 변환한다.
             *
             * @return 주민 등급 값
             */
            private int toInt() {
                return ordinal() + 1;
            }
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

            /** JSON 코덱 */
            private static final Codec<Supply> CODEC = CodecUtil.fromEnum(Supply.class);

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

            /** JSON 코덱 */
            private static final Codec<PriceMultiplier> CODEC = CodecUtil.fromEnum(PriceMultiplier.class);

            /** 가격 배수 값 */
            private final float value;
        }

        /**
         * 마법이 부여된 거래 품목의 마법 부여 레벨. (원본: 5~19).
         */
        @AllArgsConstructor
        private enum EnchantmentLevel {
            /** 낮음 */
            LOW(IntegerRange.of(5, 12), VPTags.Enchantments.TRADEABLE),
            /** 중간 */
            MEDIUM(IntegerRange.of(12, 20), VPTags.Enchantments.TRADEABLE),
            /** 높음 */
            HIGH(IntegerRange.of(20, 30), VPTags.Enchantments.TRADEABLE),
            /** 최고 */
            HIGHEST(IntegerRange.of(25, 35), VPTags.Enchantments.TRADEABLE_TREASURE);

            /** JSON 코덱 */
            private static final Codec<EnchantmentLevel> CODEC = CodecUtil.fromEnum(EnchantmentLevel.class);

            /** 마법 부여 레벨 값 */
            private final IntegerRange value;
            /** 마법 부여 데이터 태그 */
            private final TagKey<Enchantment> enchantmentTagKey;
        }

        /**
         * 거래 품목의 세부 요소 유형을 관리하는 인터페이스.
         *
         * @param <T> {@link OfferComponentType}을 상속받는 열거형 타입
         * @param <U> {@link OfferComponent}을 상속받는 타입
         */
        private interface OfferComponentType<T extends Enum<T> & OfferComponentType<T, U>, U extends OfferComponent<T, U>> {
            /**
             * 세부 요소의 JSON 코덱을 반환한다.
             *
             * @return JSON 코덱
             */
            @NonNull
            MapCodec<? extends U> getCodec();
        }

        /**
         * 거래 품목의 세부 요소를 나타내는 인터페이스.
         *
         * @param <T> {@link OfferComponentType}을 상속받는 열거형 타입
         * @param <U> {@link OfferComponent}을 상속받는 타입
         */
        private interface OfferComponent<T extends Enum<T> & OfferComponentType<T, U>, U extends OfferComponent<T, U>> {
            /**
             * @return 세부 요소 유형
             */
            @NonNull
            T getType();
        }

        /**
         * 거래 품목 클래스.
         */
        private interface OfferItem extends OfferComponent<OfferItem.Types, OfferItem> {
            /** JSON 코덱 */
            Codec<OfferItem> CODEC = CodecUtil.fromEnum(Types.class).dispatch(OfferItem::getType, Types::getCodec);

            @NonNull
            private static ItemCost getItemCost(@NonNull ItemStack itemStack) {
                return new ItemCost(itemStack.getItem(), itemStack.getCount());
            }

            /**
             * 거래 품목을 ItemListing 인스턴스로 변환하여 반환한다.
             *
             * @return ItemListing 인스턴스
             */
            @NonNull
            VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerList);

            /**
             * 거래 품목의 유형 목록.
             */
            @AllArgsConstructor
            @Getter
            enum Types implements OfferComponentType<Types, OfferItem> {
                BUY(Buy.CODEC),
                SELL(Sell.CODEC),
                SELL_ENCHANTED(SellEnchanted.CODEC),
                RANDOM(RandomOffer.CODEC),
                TYPE_SPECIFIC(TypeSpecificOffer.CODEC);

                /** JSON 코덱 */
                private final MapCodec<? extends OfferItem> codec;
            }

            /**
             * 구매 품목 클래스.
             *
             * @param itemStackGenerator 구매 아이템 생성 처리기
             * @param priceMultiplier    가격 배수
             */
            record Buy(@NonNull ItemStackGenerator itemStackGenerator, @NonNull PriceMultiplier priceMultiplier) implements OfferItem {
                private static final MapCodec<Buy> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                        .group(ItemStackGenerator.CODEC.fieldOf("price").forGetter(Buy::itemStackGenerator),
                                PriceMultiplier.CODEC.optionalFieldOf("price_multiplier", PriceMultiplier.LOW).forGetter(Buy::priceMultiplier))
                        .apply(instance, Buy::new));

                @Override
                @NonNull
                public Types getType() {
                    return Types.BUY;
                }

                @Override
                @NonNull
                public VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerList) {
                    return (entity, randomSource) -> {
                        ItemStack itemStack = itemStackGenerator.create(entity, randomSource);
                        if (itemStack == null)
                            return null;

                        return new MerchantOffer(getItemCost(itemStack), new ItemStack(Items.EMERALD), Supply.ABUNDANT.maxTradeCount,
                                offerList.level.buyXP, priceMultiplier.value);
                    };
                }
            }

            /**
             * 판매 품목 클래스.
             *
             * @param supply          수량
             * @param price           가격
             * @param required        추가 요구 아이템 생성 처리기
             * @param product         판매 아이템 생성 처리기
             * @param priceMultiplier 가격 배수
             */
            record Sell(@NonNull Supply supply, int price, @NonNull ItemStackGenerator required, @NonNull ItemStackGenerator product,
                        @NonNull PriceMultiplier priceMultiplier) implements OfferItem {
                private static final MapCodec<Sell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                        .group(Supply.CODEC.fieldOf("supply").forGetter(Sell::supply),
                                ExtraCodecs.POSITIVE_INT.fieldOf("price").forGetter(Sell::price),
                                ItemStackGenerator.CODEC.optionalFieldOf("required_item", ItemStackGenerator.EmptyGenerator.instance)
                                        .forGetter(Sell::required),
                                ItemStackGenerator.CODEC.fieldOf("product").forGetter(Sell::product),
                                PriceMultiplier.CODEC.optionalFieldOf("price_multiplier", PriceMultiplier.LOW).forGetter(Sell::priceMultiplier))
                        .apply(instance, Sell::new));

                @Override
                @NonNull
                public Types getType() {
                    return Types.SELL;
                }

                @Override
                @NonNull
                public VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerList) {
                    return (entity, randomSource) -> {
                        ItemStack productItemStack = product.create(entity, randomSource);
                        if (productItemStack == null)
                            return null;

                        ItemStack requiredItemStack = required.create(entity, randomSource);

                        return new MerchantOffer(getItemCost(new ItemStack(Items.EMERALD, price)),
                                requiredItemStack == null ? Optional.empty() : Optional.of(getItemCost(requiredItemStack)), productItemStack,
                                supply.maxTradeCount, offerList.level.sellXP * price, priceMultiplier.value);
                    };
                }
            }

            /**
             * 마법이 부여된 아이템의 판매 품목 클래스.
             *
             * <p>최종 가격은 {@link EnchantmentLevel#value}×{@code priceModifier}이다.</p>
             *
             * @param supply            수량
             * @param priceModifier     가격 수정자
             * @param required          추가 요구 아이템 생성 처리기
             * @param product           판매 아이템 생성 처리기
             * @param enchantmentLevel  마법 부여 레벨
             * @param enchantmentTagKey 마법 부여 데이터 태그
             * @param priceMultiplier   가격 배수
             */
            record SellEnchanted(@NonNull Supply supply, float priceModifier, @NonNull ItemStackGenerator required,
                                 @NonNull ItemStackGenerator product, @NonNull EnchantmentLevel enchantmentLevel,
                                 @NonNull TagKey<Enchantment> enchantmentTagKey, @NonNull PriceMultiplier priceMultiplier) implements OfferItem {
                private static final MapCodec<SellEnchanted> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                        .group(Supply.CODEC.fieldOf("supply").forGetter(SellEnchanted::supply),
                                ExtraCodecs.POSITIVE_FLOAT.fieldOf("price_modifier").forGetter(SellEnchanted::priceModifier),
                                ItemStackGenerator.CODEC.optionalFieldOf("required_item", ItemStackGenerator.EmptyGenerator.instance)
                                        .forGetter(SellEnchanted::required),
                                ItemStackGenerator.CODEC.fieldOf("product").forGetter(SellEnchanted::product),
                                EnchantmentLevel.CODEC.fieldOf("enchantment_level").forGetter(SellEnchanted::enchantmentLevel),
                                TagKey.hashedCodec(Registries.ENCHANTMENT).optionalFieldOf("enchantment_tag", VPTags.Enchantments.TRADEABLE)
                                        .forGetter(SellEnchanted::enchantmentTagKey),
                                PriceMultiplier.CODEC.optionalFieldOf("price_multiplier", PriceMultiplier.HIGH)
                                        .forGetter(SellEnchanted::priceMultiplier))
                        .apply(instance, SellEnchanted::new));

                @Override
                @NonNull
                public Types getType() {
                    return Types.SELL_ENCHANTED;
                }

                @Override
                @NonNull
                public VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerList) {
                    return (entity, randomSource) -> {
                        ItemStack productItemStack = product.create(entity, randomSource);
                        if (productItemStack == null)
                            return null;

                        IntegerRange value = enchantmentLevel.value;
                        int enchantLevel = randomSource.nextIntBetweenInclusive(value.getMinimum(), value.getMaximum());

                        Registry<Enchantment> enchantmentRegistry = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                        Stream<Holder<Enchantment>> enchantments = enchantmentRegistry.getOrThrow(enchantmentLevel.enchantmentTagKey).stream()
                                .filter(enchantmentHolder -> enchantmentHolder.is(enchantmentTagKey));

                        ItemStack enchantedItem = EnchantmentHelper.enchantItem(randomSource, productItemStack, enchantLevel, enchantments);

                        double price = enchantLevel * priceModifier;
                        if (enchantedItem.getEnchantments().keySet().stream()
                                .anyMatch(enchantment -> enchantment.containsTag(EnchantmentTags.DOUBLE_TRADE_PRICE)))
                            price *= 1.5;

                        int finalPrice = Math.min((int) price, 64);
                        ItemStack requiredItemStack = required.create(entity, randomSource);

                        return new MerchantOffer(getItemCost(new ItemStack(Items.EMERALD, finalPrice)),
                                requiredItemStack == null ? Optional.empty() : Optional.of(getItemCost(requiredItemStack)), enchantedItem,
                                supply.maxTradeCount, offerList.level.sellXP * finalPrice, priceMultiplier.value);
                    };
                }
            }

            /**
             * 무작위 아이템의 거래 품목 클래스.
             *
             * @param offerItems 거래 품목 목록
             */
            record RandomOffer(@NonNull List<OfferItem> offerItems) implements OfferItem {
                private static final MapCodec<RandomOffer> CODEC = Codec.lazyInitialized(() -> OfferItem.CODEC).listOf().fieldOf("offers")
                        .xmap(RandomOffer::new, RandomOffer::offerItems);

                @Override
                @NonNull
                public Types getType() {
                    return Types.RANDOM;
                }

                @Override
                @NonNull
                public VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerListInfo) {
                    return (entity, randomSource) -> {
                        MerchantOffer offer = null;
                        while (offer == null)
                            offer = offerItems.get(randomSource.nextInt(offerItems.size())).getItemListing(offerListInfo)
                                    .getOffer(entity, randomSource);

                        return offer;
                    };
                }
            }

            /**
             * 주민 타입(바이옴)에 따른 거래 품목 클래스.
             *
             * @param offerItemMap 주민 타입별 거래 품목 목록
             */
            record TypeSpecificOffer(@NonNull Map<Holder<VillagerType>, OfferItem> offerItemMap) implements OfferItem {
                private static final MapCodec<TypeSpecificOffer> CODEC = Codec.unboundedMap(VillagerType.CODEC, Codec.lazyInitialized(() -> OfferItem.CODEC))
                        .fieldOf("map").xmap(TypeSpecificOffer::new, TypeSpecificOffer::offerItemMap);

                @Override
                @NonNull
                public Types getType() {
                    return Types.TYPE_SPECIFIC;
                }

                @Override
                @NonNull
                public VillagerTrades.ItemListing getItemListing(@NonNull OfferList offerList) {
                    return (entity, randomSource) -> {
                        if (!(entity instanceof VillagerDataHolder villagerDataHolder))
                            return null;

                        Holder<VillagerType> villagerTypeHolder = villagerDataHolder.getVillagerData().type();
                        OfferItem offerItem = offerItemMap.get(villagerTypeHolder);
                        if (offerItem == null)
                            return null;

                        return offerItem.getItemListing(offerList).getOffer(entity, randomSource);
                    };
                }
            }
        }

        /**
         * 거래 품목에 사용되는 아이템 생성 처리기 인터페이스.
         */
        private interface ItemStackGenerator extends OfferComponent<ItemStackGenerator.Types, ItemStackGenerator> {
            /** JSON 코덱 */
            Codec<ItemStackGenerator> CODEC = CodecUtil.fromEnum(Types.class).dispatch(ItemStackGenerator::getType, Types::getCodec);

            /**
             * 아이템을 생성하여 반환한다.
             *
             * @param entity       대상 엔티티
             * @param randomSource 랜덤 소스
             * @return 아이템
             */
            @Nullable
            ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource);

            /**
             * 아이템 생성 처리기의 유형 목록.
             */
            @AllArgsConstructor
            @Getter
            enum Types implements OfferComponentType<Types, ItemStackGenerator> {
                EMPTY(EmptyGenerator.CODEC),
                ITEMSTACK(DirectGenerator.CODEC),
                RANDOM(RandomGenerator.CODEC),
                SUSPICIOUS_STEW(SuspiciousStewGenerator.CODEC),
                TROPICAL_FISH_BUCKET(TropicalFishBucketGenerator.CODEC),
                AXOLOTL_BUCKET(AxolotlBucketGenerator.CODEC),
                POTION(PotionGenerator.CODEC),
                TIPPED_ARROW(TippedArrowGenerator.CODEC),
                MAP(MapGenerator.CODEC),
                STRUCTURE_MAP(StructureMapGenerator.CODEC),
                LEATHER_ITEM(LeatherItemGenerator.CODEC),
                GOAT_HORN(GoatHornItemGenerator.CODEC);

                /** JSON 코덱 */
                private final MapCodec<? extends ItemStackGenerator> codec;
            }

            /**
             * {@code null}을 반환하는 아이템 생성 처리기 클래스.
             */
            @NoArgsConstructor
            final class EmptyGenerator implements ItemStackGenerator {
                private static final EmptyGenerator instance = new EmptyGenerator();
                private static final MapCodec<EmptyGenerator> CODEC = MapCodec.unit(instance);

                @Override
                @NonNull
                public Types getType() {
                    return Types.EMPTY;
                }

                @Override
                @Nullable
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return null;
                }
            }

            /**
             * 지정된 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param itemStack 아이템
             */
            record DirectGenerator(@NonNull ItemStack itemStack) implements ItemStackGenerator {
                private static final MapCodec<DirectGenerator> CODEC = ItemStack.MAP_CODEC.xmap(DirectGenerator::new, DirectGenerator::itemStack);

                @Override
                @NonNull
                public Types getType() {
                    return Types.ITEMSTACK;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return itemStack;
                }
            }

            /**
             * 무작위 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param itemStackGenerators 아이템 생성 처리기 목록
             */
            record RandomGenerator(@NonNull List<ItemStackGenerator> itemStackGenerators) implements ItemStackGenerator {
                private static final MapCodec<RandomGenerator> CODEC = Codec.lazyInitialized(() -> ItemStackGenerator.CODEC).listOf()
                        .fieldOf("items").xmap(RandomGenerator::new, RandomGenerator::itemStackGenerators);

                @Override
                @NonNull
                public Types getType() {
                    return Types.RANDOM;
                }

                @Override
                @Nullable
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return itemStackGenerators.get(randomSource.nextInt(itemStackGenerators.size())).create(entity, randomSource);
                }
            }

            /**
             * 수상한 스튜 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param effects 효과 목록
             */
            record SuspiciousStewGenerator(@NonNull List<SuspiciousStewEffects.Entry> effects) implements ItemStackGenerator {
                private static final MapCodec<SuspiciousStewGenerator> CODEC = SuspiciousStewEffects.Entry.CODEC.listOf().fieldOf("effects")
                        .xmap(SuspiciousStewGenerator::new, SuspiciousStewGenerator::effects);

                @Override
                @NonNull
                public Types getType() {
                    return Types.SUSPICIOUS_STEW;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
                    itemStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS,
                            new SuspiciousStewEffects(Collections.singletonList(effects.get(randomSource.nextInt(effects.size())))));

                    return itemStack;
                }
            }

            /**
             * 열대어가 담긴 양동이 아이템을 반환하는 아이템 생성 처리기 클래스.
             */
            @NoArgsConstructor
            final class TropicalFishBucketGenerator implements ItemStackGenerator {
                private static final TropicalFishBucketGenerator instance = new TropicalFishBucketGenerator();
                private static final MapCodec<TropicalFishBucketGenerator> CODEC = MapCodec.unit(instance);

                @Override
                @NonNull
                public Types getType() {
                    return Types.TROPICAL_FISH_BUCKET;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(Items.TROPICAL_FISH_BUCKET);

                    List<TropicalFish.Variant> variants = TropicalFish.COMMON_VARIANTS;
                    TropicalFish.Variant variant = variants.get(randomSource.nextInt(variants.size()));

                    itemStack.set(DataComponents.TROPICAL_FISH_PATTERN, variant.pattern());
                    itemStack.set(DataComponents.TROPICAL_FISH_BASE_COLOR, variant.baseColor());
                    itemStack.set(DataComponents.TROPICAL_FISH_PATTERN_COLOR, variant.patternColor());

                    return itemStack;
                }
            }

            /**
             * 아홀로틀이 담긴 양동이 아이템을 반환하는 아이템 생성 처리기 클래스.
             */
            @NoArgsConstructor
            final class AxolotlBucketGenerator implements ItemStackGenerator {
                private static final AxolotlBucketGenerator instance = new AxolotlBucketGenerator();
                private static final MapCodec<AxolotlBucketGenerator> CODEC = MapCodec.unit(instance);

                @Override
                @NonNull
                public Types getType() {
                    return Types.AXOLOTL_BUCKET;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(Items.AXOLOTL_BUCKET);
                    itemStack.set(DataComponents.AXOLOTL_VARIANT, Axolotl.Variant.getCommonSpawnVariant(randomSource));

                    return itemStack;
                }
            }

            /**
             * 물약 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param potionHolder 물약 홀더 인스턴스
             */
            record PotionGenerator(@NonNull Holder<Potion> potionHolder) implements ItemStackGenerator {
                private static final MapCodec<PotionGenerator> CODEC = Potion.CODEC.fieldOf("potion")
                        .xmap(PotionGenerator::new, PotionGenerator::potionHolder);

                @Override
                @NonNull
                public Types getType() {
                    return Types.POTION;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return PotionContents.createItemStack(Items.POTION, potionHolder);
                }
            }

            /**
             * 물약 화살 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param potionHolder 물약 홀더 인스턴스
             * @param count        수량
             */
            record TippedArrowGenerator(@NonNull Holder<Potion> potionHolder, int count) implements ItemStackGenerator {
                private static final MapCodec<TippedArrowGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                        .group(Potion.CODEC.fieldOf("potion").forGetter(TippedArrowGenerator::potionHolder),
                                ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1)
                                        .forGetter(TippedArrowGenerator::count))
                        .apply(instance, TippedArrowGenerator::new));

                @Override
                @NonNull
                public Types getType() {
                    return Types.TIPPED_ARROW;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW, count);
                    itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));

                    return itemStack;
                }
            }

            /**
             * 작성된 지도 아이템을 반환하는 아이템 생성 처리기 클래스.
             */
            @NoArgsConstructor
            final class MapGenerator implements ItemStackGenerator {
                private static final MapGenerator instance = new MapGenerator();
                private static final MapCodec<MapGenerator> CODEC = MapCodec.unit(instance);

                @Override
                @NonNull
                public Types getType() {
                    return Types.MAP;
                }

                @Override
                @Nullable
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return entity.level() instanceof ServerLevel serverLevel
                            ? MapItem.create(serverLevel, entity.getBlockX(), entity.getBlockZ(), (byte) 0, true,
                            false)
                            : null;
                }
            }

            /**
             * 구조물 지도 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param structureMapHolder 구조물 지도 홀더 인스턴스
             */
            record StructureMapGenerator(@NonNull Holder<StructureMap> structureMapHolder) implements ItemStackGenerator {
                private static final MapCodec<StructureMapGenerator> CODEC = StructureMap.CODEC.fieldOf("structure_map")
                        .xmap(StructureMapGenerator::new, StructureMapGenerator::structureMapHolder);

                @Override
                @NonNull
                public Types getType() {
                    return Types.STRUCTURE_MAP;
                }

                @Override
                @Nullable
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    return structureMapHolder.value().createMap(entity);
                }
            }

            /**
             * 무작위 색상의 염색 가능한 가죽 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param itemHolder 아이템 홀더 인스턴스
             */
            record LeatherItemGenerator(@NonNull Holder<Item> itemHolder) implements ItemStackGenerator {
                private static final MapCodec<LeatherItemGenerator> CODEC = Item.CODEC.fieldOf("id")
                        .xmap(LeatherItemGenerator::new, LeatherItemGenerator::itemHolder);

                @NonNull
                private static DyeItem getRandomDyeItem(@NonNull RandomSource randomSource) {
                    return DyeItem.byColor(DyeColor.byId(randomSource.nextInt(DyeColor.values().length)));
                }

                @Override
                @NonNull
                public Types getType() {
                    return Types.LEATHER_ITEM;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(itemHolder.value());
                    if (!itemStack.is(ItemTags.DYEABLE))
                        return itemStack;

                    ImmutableList.Builder<DyeItem> builder = ImmutableList.builder();

                    builder.add(getRandomDyeItem(randomSource));
                    if (randomSource.nextDouble() > 0.6)
                        builder.add(getRandomDyeItem(randomSource));

                    return DyedItemColor.applyDyes(itemStack, builder.build());
                }
            }

            /**
             * 염소 뿔 아이템을 반환하는 아이템 생성 처리기 클래스.
             *
             * @param instrumentHolder 염소 뿔 종류 홀더 인스턴스
             */
            record GoatHornItemGenerator(@NonNull Holder<Instrument> instrumentHolder) implements ItemStackGenerator {
                private static final MapCodec<GoatHornItemGenerator> CODEC = Instrument.CODEC.fieldOf("instrument")
                        .xmap(GoatHornItemGenerator::new, GoatHornItemGenerator::instrumentHolder);

                @Override
                @NonNull
                public Types getType() {
                    return Types.GOAT_HORN;
                }

                @Override
                @NonNull
                public ItemStack create(@NonNull Entity entity, @NonNull RandomSource randomSource) {
                    ItemStack itemStack = new ItemStack(Items.GOAT_HORN);
                    itemStack.set(DataComponents.INSTRUMENT, new InstrumentComponent(instrumentHolder));

                    return itemStack;
                }
            }
        }
    }
}
