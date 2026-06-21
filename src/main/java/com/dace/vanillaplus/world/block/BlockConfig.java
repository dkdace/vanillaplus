package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;

/**
 * 블록의 요소를 수정하는 블록 설정 클래스.
 *
 * @param components 데이터 요소 목록
 * @see BlockConfigComponentTypes
 */
public record BlockConfig(@NonNull VPDataComponentMap components) {
    /** 기본값 */
    public static final BlockConfig DEFAULT = new BlockConfig(VPDataComponentMap.EMPTY);
    /** JSON 코덱 */
    public static final Codec<BlockConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(VPDataComponentMap.createCodec(StaticRegistry.BLOCK_CONFIG_COMPONENT_TYPE).optionalFieldOf("components", DEFAULT.components)
                    .forGetter(BlockConfig::components))
            .apply(instance, BlockConfig::new));
}
