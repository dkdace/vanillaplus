package com.dace.vanillaplus.world.block.modifier;

import com.dace.vanillaplus.data.CodecComponent;
import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;

/**
 * 블록의 요소를 수정하는 블록 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@Getter
public class BlockModifier implements CodecComponent<BlockModifier> {
    /** 기본값 */
    public static final BlockModifier DEFAULT = new BlockModifier(VPDataComponentMap.EMPTY);
    /** JSON 코덱 */
    public static final MapCodec<BlockModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, BlockModifier::new));

    /** 데이터 요소 목록 */
    @NonNull
    private final VPDataComponentMap components;

    @NonNull
    static <T extends BlockModifier> Products.P1<RecordCodecBuilder.Mu<T>, VPDataComponentMap> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(VPDataComponentMap.createCodec(StaticRegistry.BLOCK_MODIFIER_COMPONENT_TYPE)
                .optionalFieldOf("components", DEFAULT.components).forGetter(BlockModifier::getComponents));
    }

    @Override
    @NonNull
    public MapCodec<? extends BlockModifier> getCodec() {
        return CODEC;
    }
}
