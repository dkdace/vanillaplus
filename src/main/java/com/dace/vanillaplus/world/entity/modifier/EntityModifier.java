package com.dace.vanillaplus.world.entity.modifier;

import com.dace.vanillaplus.data.CodecComponent;
import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;

/**
 * 엔티티의 요소를 수정하는 엔티티 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@Getter
public class EntityModifier implements CodecComponent<EntityModifier> {
    /** 기본값 */
    public static final EntityModifier DEFAULT = new EntityModifier(VPDataComponentMap.EMPTY);
    /** JSON 코덱 */
    public static final MapCodec<EntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, EntityModifier::new));

    /** 데이터 요소 목록 */
    @NonNull
    private final VPDataComponentMap components;

    @NonNull
    static <T extends EntityModifier> Products.P1<RecordCodecBuilder.Mu<T>, VPDataComponentMap> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(VPDataComponentMap.createCodec(StaticRegistry.ENTITY_MODIFIER_COMPONENT_TYPE)
                .optionalFieldOf("components", DEFAULT.components).forGetter(EntityModifier::getComponents));
    }

    @Override
    @NonNull
    public MapCodec<? extends EntityModifier> getCodec() {
        return CODEC;
    }
}
