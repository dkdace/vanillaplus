package com.dace.vanillaplus.world.entity.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
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
public class LivingEntityModifier extends EntityModifier {
    /** 기본값 */
    public static final LivingEntityModifier DEFAULT = new LivingEntityModifier(EntityModifier.DEFAULT.getComponents(), Data.DEFAULT);
    /** JSON 코덱 */
    public static final MapCodec<LivingEntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createLivingEntityBaseCodec(instance).apply(instance, LivingEntityModifier::new));

    /** 데이터 */
    @Getter(AccessLevel.PACKAGE)
    private final Data livingEntityData;

    LivingEntityModifier(@NonNull VPDataComponentMap components, @NonNull Data livingEntityData) {
        super(components);
        this.livingEntityData = livingEntityData;
    }

    @NonNull
    static <T extends LivingEntityModifier> Products.P2<RecordCodecBuilder.Mu<T>, VPDataComponentMap, Data> createLivingEntityBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return createBaseCodec(instance).and(Data.CODEC.forGetter(LivingEntityModifier::getLivingEntityData));
    }

    /**
     * @return 엔티티 속성 목록
     */
    @NonNull
    public final List<AttributeInstance.Packed> getAttributes() {
        return livingEntityData.attributes;
    }

    /**
     * @return 투명한 블록 투시 가능 여부
     */
    public final boolean canSeeThroughTransparentBlocks() {
        return livingEntityData.canSeeThroughTransparentBlocks;
    }

    @Override
    @NonNull
    public MapCodec<? extends LivingEntityModifier> getCodec() {
        return CODEC;
    }

    /**
     * 데이터 클래스.
     *
     * @param attributes                     엔티티 속성 목록
     * @param canSeeThroughTransparentBlocks 투명한 블록 투시 가능 여부
     */
    record Data(@NonNull List<AttributeInstance.Packed> attributes, boolean canSeeThroughTransparentBlocks) {
        private static final Data DEFAULT = new Data(Collections.emptyList(), false);

        private static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(AttributeInstance.Packed.LIST_CODEC.optionalFieldOf("attributes", DEFAULT.attributes).forGetter(Data::attributes),
                        Codec.BOOL.optionalFieldOf("can_see_through_transparent_blocks", DEFAULT.canSeeThroughTransparentBlocks)
                                .forGetter(Data::canSeeThroughTransparentBlocks))
                .apply(instance, Data::new));
    }
}
