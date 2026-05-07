package com.dace.vanillaplus.world.entity.modifier;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Ravager;

import java.util.List;
import java.util.Optional;

/**
 * {@link Ravager}의 엔티티 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class RavagerModifier extends LivingEntityModifier {
    /** 기본값 */
    public static final RavagerModifier DEFAULT = new RavagerModifier(EntityModifier.DEFAULT.getComponents(),
            LivingEntityModifier.DEFAULT.getAttributes(), Optional.empty());
    /** JSON 코덱 */
    public static final MapCodec<RavagerModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            createLivingEntityBaseCodec(instance)
                    .and(CodecUtil.secondsToTicks(ExtraCodecs.NON_NEGATIVE_FLOAT).optionalFieldOf("roar_cooldown_seconds")
                            .forGetter(ravagerModifier -> ravagerModifier.roarCooldown))
                    .apply(instance, RavagerModifier::new));

    /** 포효 쿨타임 */
    @NonNull
    private final Optional<Integer> roarCooldown;

    private RavagerModifier(@NonNull VPDataComponentMap components, @NonNull List<AttributeInstance.Packed> packedAttributes,
                            @NonNull Optional<Integer> roarCooldown) {
        super(components, packedAttributes);
        this.roarCooldown = roarCooldown;
    }

    @Override
    @NonNull
    public MapCodec<? extends LivingEntityModifier> getCodec() {
        return CODEC;
    }
}
