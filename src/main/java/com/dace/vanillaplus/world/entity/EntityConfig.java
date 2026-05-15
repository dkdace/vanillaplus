package com.dace.vanillaplus.world.entity;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;

/**
 * 엔티티의 요소를 수정하는 엔티티 설정 클래스.
 *
 * @see EntityConfigComponentTypes
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@Getter
public final class EntityConfig {
    /** JSON 코덱 */
    public static final Codec<EntityConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(VPDataComponentMap.createCodec(StaticRegistry.ENTITY_CONFIG_COMPONENT_TYPE)
                    .optionalFieldOf("components", VPDataComponentMap.EMPTY).forGetter(EntityConfig::getComponents))
            .apply(instance, EntityConfig::new));
    /** 데이터 요소 목록 */
    @NonNull
    private final VPDataComponentMap components;
}
