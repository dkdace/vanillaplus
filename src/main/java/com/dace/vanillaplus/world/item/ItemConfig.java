package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentPatch;

/**
 * 아이템의 요소를 수정하는 아이템 설정 클래스.
 *
 * @param dataComponentPatch 아이템 데이터 요소
 * @param components         데이터 요소 목록
 * @see ItemConfigComponentTypes
 */
public record ItemConfig(@NonNull DataComponentPatch dataComponentPatch, @NonNull VPDataComponentMap components) {
    /** JSON 코덱 */
    public static final Codec<ItemConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(DataComponentPatch.CODEC.optionalFieldOf("item_components", DataComponentPatch.EMPTY)
                            .forGetter(ItemConfig::dataComponentPatch),
                    VPDataComponentMap.createCodec(StaticRegistry.ITEM_CONFIG_COMPONENT_TYPE)
                            .optionalFieldOf("components", VPDataComponentMap.EMPTY).forGetter(ItemConfig::components))
            .apply(instance, ItemConfig::new));
}
