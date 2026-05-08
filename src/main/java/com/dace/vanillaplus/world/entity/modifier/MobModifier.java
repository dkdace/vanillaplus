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
import net.minecraft.world.entity.Mob;

/**
 * {@link Mob}의 엔티티 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
public class MobModifier extends LivingEntityModifier {
    /** 기본값 */
    public static final MobModifier DEFAULT = new MobModifier(EntityModifier.DEFAULT.getComponents(),
            LivingEntityModifier.DEFAULT.getLivingEntityData(), Data.DEFAULT);
    /** JSON 코덱 */
    public static final MapCodec<MobModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createMobBaseCodec(instance)
            .apply(instance, MobModifier::new));

    /** 데이터 */
    @Getter(AccessLevel.PACKAGE)
    private final Data mobData;

    MobModifier(@NonNull VPDataComponentMap components, @NonNull LivingEntityModifier.Data livingEntityData, @NonNull Data mobData) {
        super(components, livingEntityData);
        this.mobData = mobData;
    }

    @NonNull
    static <T extends MobModifier> Products.P3<RecordCodecBuilder.Mu<T>, VPDataComponentMap, LivingEntityModifier.Data, Data> createMobBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return createLivingEntityBaseCodec(instance).and(Data.CODEC.forGetter(MobModifier::getMobData));
    }

    /**
     * @return 목표물이 있을 때 탑승물 승차 방지 여부
     */
    public final boolean preventRidingIfHasTarget() {
        return mobData.preventRidingIfHasTarget;
    }

    /**
     * @return 대상이 높은 곳에 있어 닿지 않을 때 점프 여부
     */
    public final boolean canJumpAtTarget() {
        return mobData.canJumpAtTarget;
    }

    @Override
    @NonNull
    public MapCodec<? extends MobModifier> getCodec() {
        return CODEC;
    }

    /**
     * 데이터 클래스.
     *
     * @param preventRidingIfHasTarget 목표물이 있을 때 탑승물 승차 방지 여부
     * @param canJumpAtTarget          대상이 높은 곳에 있어 닿지 않을 때 점프 여부
     */
    record Data(boolean preventRidingIfHasTarget, boolean canJumpAtTarget) {
        private static final Data DEFAULT = new Data(false, false);

        private static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Codec.BOOL.optionalFieldOf("prevent_riding_if_has_target", DEFAULT.preventRidingIfHasTarget)
                                .forGetter(Data::preventRidingIfHasTarget),
                        Codec.BOOL.optionalFieldOf("can_jump_at_target", DEFAULT.canJumpAtTarget).forGetter(Data::canJumpAtTarget))
                .apply(instance, Data::new));
    }
}
