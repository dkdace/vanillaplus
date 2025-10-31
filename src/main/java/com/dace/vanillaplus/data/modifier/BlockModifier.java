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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 블록의 요소를 수정하는 블록 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockModifier implements DataModifier<Block>, CodecUtil.CodecComponent<BlockModifier> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends BlockModifier>> CODEC_REGISTRY = VPRegistry.BLOCK_MODIFIER.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<BlockModifier> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);
    /** JSON 코덱 */
    private static final MapCodec<BlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, BlockModifier::new));

    static {
        CODEC_REGISTRY.register("block", () -> CODEC);
        CODEC_REGISTRY.register("drop_experience", () -> DropExperienceModifier.CODEC);
        CODEC_REGISTRY.register("bell", () -> BellModifier.CODEC);
    }

    /** 블록 속성 */
    @NonNull
    private final BlockBehaviour.Properties properties;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.BLOCK_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    /**
     * 지정한 블록에 해당하는 블록 수정자를 반환한다.
     *
     * @param block 블록
     * @param <T>   {@link BlockModifier}를 상속받는 블록 수정자
     * @return 블록 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends BlockModifier> T fromBlock(@NonNull Block block) {
        return (T) VPRegistry.BLOCK_MODIFIER.getValue(BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    /**
     * 지정한 블록에 해당하는 블록 수정자를 반환한다.
     *
     * @param block 블록
     * @return 블록 수정자
     * @throws IllegalStateException 해당하는 블록 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends BlockModifier> T fromBlockOrThrow(@NonNull Block block) {
        return (T) VPRegistry.BLOCK_MODIFIER.getValueOrThrow(BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    @NonNull
    private static <T extends BlockModifier> Products.P1<RecordCodecBuilder.Mu<T>, BlockBehaviour.Properties> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(BlockBehaviour.Properties.CODEC.optionalFieldOf("properties", BlockBehaviour.Properties.of())
                .forGetter(BlockModifier::getProperties));
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }

    /**
     * {@link DropExperienceBlock}의 블록 수정자 클래스.
     */
    @Getter
    public static final class DropExperienceModifier extends BlockModifier {
        private static final MapCodec<DropExperienceModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("experience", ConstantInt.of(0))
                                .forGetter(DropExperienceModifier::getXpRange))
                        .apply(instance, DropExperienceModifier::new));

        /** 드롭 경험치 범위 */
        @NonNull
        private final IntProvider xpRange;

        private DropExperienceModifier(@NonNull BlockBehaviour.Properties properties, @NonNull IntProvider xpRange) {
            super(properties);
            this.xpRange = xpRange;
        }

        @Override
        @NonNull
        public MapCodec<? extends BlockModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link BellBlock}의 블록 수정자 클래스.
     */
    public static final class BellModifier extends BlockModifier {
        private static final MapCodec<BellModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("raider_detection_range", 32)
                                        .forGetter(BellModifier::getRaiderDetectionRange),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("glow_range", 48)
                                        .forGetter(BellModifier::getGlowRange),
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

        private BellModifier(@NonNull BlockBehaviour.Properties properties, int raiderDetectionRange, int glowRange, float glowDurationSeconds) {
            super(properties);

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
}
