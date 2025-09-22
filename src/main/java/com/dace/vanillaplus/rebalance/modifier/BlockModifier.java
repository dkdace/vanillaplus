package com.dace.vanillaplus.rebalance.modifier;

import com.dace.vanillaplus.VPRegistries;
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
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 블록의 요소를 수정하는 블록 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockModifier implements DataModifier<Block>, CodecUtil.CodecComponent<BlockModifier, BlockModifier.Types> {
    /** 유형별 코덱 */
    private static final Codec<BlockModifier> TYPE_CODEC = CodecUtil.fromCodecComponent(Types.class);
    /** JSON 코덱 */
    private static final MapCodec<BlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, BlockModifier::new));

    /** 블록 속성 */
    @NonNull
    private final BlockBehaviour.Properties properties;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistries.BLOCK_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    @NonNull
    private static <T extends BlockModifier> Products.P1<RecordCodecBuilder.Mu<T>, BlockBehaviour.Properties> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(BlockBehaviour.Properties.CODEC.optionalFieldOf("properties", BlockBehaviour.Properties.of())
                .forGetter(BlockModifier::getProperties));
    }

    @Override
    @NonNull
    public Types getType() {
        return Types.BLOCK;
    }

    /**
     * 블록 수정자의 유형 목록.
     */
    @AllArgsConstructor
    @Getter
    public enum Types implements CodecUtil.CodecComponentType<BlockModifier, Types> {
        BLOCK(CODEC),
        DROP_EXPERIENCE(DropExperienceModifier.CODEC);

        /** JSON 코덱 */
        private final MapCodec<? extends BlockModifier> codec;
    }

    /**
     * {@link DropExperienceBlock}의 블록 수정자 클래스.
     */
    @Getter
    public static final class DropExperienceModifier extends BlockModifier {
        private static final MapCodec<DropExperienceModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
                .and(IntProvider.CODEC.optionalFieldOf("experience", ConstantInt.of(0)).forGetter(DropExperienceModifier::getXpRange))
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
        public Types getType() {
            return Types.DROP_EXPERIENCE;
        }
    }
}
