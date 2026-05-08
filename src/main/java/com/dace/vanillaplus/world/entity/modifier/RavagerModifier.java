package com.dace.vanillaplus.world.entity.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.monster.Ravager;

import java.util.Optional;

/**
 * {@link Ravager}의 엔티티 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class RavagerModifier extends MobModifier {
    /** 기본값 */
    public static final RavagerModifier DEFAULT = new RavagerModifier(EntityModifier.DEFAULT.getComponents(),
            LivingEntityModifier.DEFAULT.getLivingEntityData(), MobModifier.DEFAULT.getMobData(), Optional.empty());
    /** JSON 코덱 */
    public static final MapCodec<RavagerModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createMobBaseCodec(instance)
                    .and(CodecUtil.secondsToTicks(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("roar_cooldown_seconds")
                            .forGetter(ravagerModifier -> ravagerModifier.roarCooldown))
                    .apply(instance, RavagerModifier::new));

    /** 포효 쿨타임 */
    @NonNull
    private final Optional<Integer> roarCooldown;

    private RavagerModifier(@NonNull VPDataComponentMap components, @NonNull LivingEntityModifier.Data livingEntityData, @NonNull Data mobData,
                            @NonNull Optional<Integer> roarCooldown) {
        super(components, livingEntityData, mobData);
        this.roarCooldown = roarCooldown;
    }

    @Override
    @NonNull
    public MapCodec<? extends MobModifier> getCodec() {
        return CODEC;
    }
}
