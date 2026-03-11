package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.Optional;

/**
 * 블록의 요소를 수정하는 블록 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockModifier implements CodecUtil.CodecComponent<BlockModifier> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends BlockModifier>> CODEC_REGISTRY = VPRegistry.BLOCK_MODIFIER.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<BlockModifier> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);
    /** JSON 코덱 */
    private static final MapCodec<BlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, BlockModifier::new));

    static {
        CODEC_REGISTRY.register("block", () -> CODEC);
        CODEC_REGISTRY.register("bell", () -> BellModifier.CODEC);
        CODEC_REGISTRY.register("water_cauldron", () -> WaterCauldronModifier.CODEC);
        CODEC_REGISTRY.register("cake", () -> CakeModifier.CODEC);
        CODEC_REGISTRY.register("anvil", () -> AnvilModifier.CODEC);
    }

    /** 블록 속성 */
    @NonNull
    private final BlockBehaviour.Properties properties;
    /** 드롭 경험치 범위 */
    @NonNull
    private final IntProvider xpRange;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.BLOCK_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    @NonNull
    private static <T extends BlockModifier> Products.P2<RecordCodecBuilder.Mu<T>, BlockBehaviour.Properties, IntProvider> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(BlockBehaviour.Properties.CODEC.optionalFieldOf("properties", BlockBehaviour.Properties.of())
                        .forGetter(BlockModifier::getProperties),
                IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("experience", ConstantInt.of(0)).forGetter(BlockModifier::getXpRange));
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }

    /**
     * {@link BellBlock}의 블록 수정자 클래스.
     */
    public static final class BellModifier extends BlockModifier {
        private static final MapCodec<BellModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("raider_detection_range", 32)
                                        .forGetter(BellModifier::getRaiderDetectionRange),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("glow_range", 48).forGetter(BellModifier::getGlowRange),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("glow_duration_seconds", 3F)
                                        .forGetter(bellModifier -> bellModifier.glowDurationSeconds)))
                        .apply(instance, BellModifier::new));

        /** 습격자 탐지 범위 */
        @Getter
        private final int raiderDetectionRange;
        /** 발광 효과 범위 */
        @Getter
        private final int glowRange;
        /** 발광 효과 지속시간 (초) */
        private final float glowDurationSeconds;

        private BellModifier(@NonNull BlockBehaviour.Properties properties, @NonNull IntProvider xpRange, int raiderDetectionRange, int glowRange,
                             float glowDurationSeconds) {
            super(properties, xpRange);

            this.raiderDetectionRange = raiderDetectionRange;
            this.glowRange = glowRange;
            this.glowDurationSeconds = glowDurationSeconds;
        }

        /**
         * @return 발광 효과 지속시간 (tick)
         */
        public int getGlowDuration() {
            return (int) (glowDurationSeconds * 20.0);
        }

        @Override
        @NonNull
        public MapCodec<? extends BlockModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link Blocks#WATER_CAULDRON}의 블록 수정자 클래스.
     */
    @Getter
    public static final class WaterCauldronModifier extends BlockModifier {
        private static final MapCodec<WaterCauldronModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_potion_types", 3)
                                        .forGetter(WaterCauldronModifier::getMaxPotionTypes),
                                ExtraCodecs.POSITIVE_INT.optionalFieldOf("tipped_arrow_max_count", 8)
                                        .forGetter(WaterCauldronModifier::getMaxTippedArrowCount)))
                        .apply(instance, WaterCauldronModifier::new));

        /** 담을 수 있는 최대 물약 종류 수 */
        private final int maxPotionTypes;
        /** 제작 가능한 물약이 묻은 화살의 최대 개수 */
        private final int maxTippedArrowCount;

        private WaterCauldronModifier(@NonNull BlockBehaviour.Properties properties, @NonNull IntProvider xpRange, int maxPotionTypes,
                                      int maxTippedArrowCount) {
            super(properties, xpRange);

            this.maxPotionTypes = maxPotionTypes;
            this.maxTippedArrowCount = maxTippedArrowCount;
        }

        @Override
        @NonNull
        public MapCodec<? extends BlockModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link CakeBlock}의 블록 수정자 클래스.
     */
    @Getter
    public static final class CakeModifier extends BlockModifier {
        private static final MapCodec<CakeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(FoodProperties.DIRECT_CODEC.optionalFieldOf("food", new FoodProperties.Builder()
                                .nutrition(2).saturationModifier(0.1F).build()).forGetter(CakeModifier::getFoodProperties))
                        .apply(instance, CakeModifier::new));

        /** 음식 속성 */
        @NonNull
        private final FoodProperties foodProperties;

        private CakeModifier(@NonNull BlockBehaviour.Properties properties, @NonNull IntProvider xpRange, @NonNull FoodProperties foodProperties) {
            super(properties, xpRange);
            this.foodProperties = foodProperties;
        }

        @Override
        @NonNull
        public MapCodec<? extends BlockModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link AnvilBlock}의 블록 수정자 클래스.
     */
    @Getter
    public static final class AnvilModifier extends BlockModifier {
        private static final MapCodec<AnvilModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cost_penalty", 1)
                                        .forGetter(AnvilModifier::getCostPenalty),
                                CodecUtil.optional(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max_cost", Optional.of(39))
                                        .forGetter(AnvilModifier::getMaxCost)))
                        .apply(instance, AnvilModifier::new));

        /** 사용 횟수당 작업 가격 증가량 */
        private final int costPenalty;
        /** 최대 작업 가격 */
        @NonNull
        private final Optional<Integer> maxCost;

        private AnvilModifier(@NonNull BlockBehaviour.Properties properties, @NonNull IntProvider xpRange, int costPenalty,
                              @NonNull Optional<Integer> maxCost) {
            super(properties, xpRange);

            this.costPenalty = costPenalty;
            this.maxCost = maxCost;
        }

        @Override
        @NonNull
        public MapCodec<? extends BlockModifier> getCodec() {
            return CODEC;
        }
    }
}
