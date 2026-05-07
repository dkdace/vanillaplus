package com.dace.vanillaplus.world.entity.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Collections;
import java.util.List;

/**
 * {@link LivingEntity}의 엔티티 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class LivingEntityModifier extends EntityModifier {
    /** 기본값 */
    public static final LivingEntityModifier DEFAULT = new LivingEntityModifier(EntityModifier.DEFAULT.getComponents(), Collections.emptyList());
    /** JSON 코덱 */
    public static final MapCodec<LivingEntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createLivingEntityBaseCodec(instance).apply(instance, LivingEntityModifier::new));

    /** 엔티티 속성 목록 */
    @NonNull
    private final List<AttributeInstance.Packed> attributes;

    LivingEntityModifier(@NonNull VPDataComponentMap components, @NonNull List<AttributeInstance.Packed> attributes) {
        super(components);
        this.attributes = attributes;
    }

    @NonNull
    static <T extends LivingEntityModifier> Products.P2<RecordCodecBuilder.Mu<T>, VPDataComponentMap, List<AttributeInstance.Packed>> createLivingEntityBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return createBaseCodec(instance)
                .and(AttributeInstance.Packed.LIST_CODEC.optionalFieldOf("attributes", DEFAULT.attributes)
                        .forGetter(LivingEntityModifier::getAttributes));
    }

    @Override
    @NonNull
    public MapCodec<? extends LivingEntityModifier> getCodec() {
        return CODEC;
    }
}
