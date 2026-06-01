package com.dace.vanillaplus.world.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/**
 * 엔티티에게 상태 효과를 적용하는 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
public final class MobEffectModifier extends ConditionalModifier<LivingEntity> {
    /** JSON 코덱 */
    public static final Codec<MobEffectModifier> CODEC = RecordCodecBuilder.create(instance -> createCodec(instance)
            .and(instance.group(MobEffect.CODEC.fieldOf("effect").forGetter(mobEffectInfo -> mobEffectInfo.mobEffectHolder),
                    ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0)
                            .forGetter(mobEffectInfo -> mobEffectInfo.amplifier)))
            .apply(instance, MobEffectModifier::new));

    /** 상태 효과 홀더 인스턴스 */
    @NonNull
    private final Holder<MobEffect> mobEffectHolder;
    /** 효과 레벨 */
    private final int amplifier;

    public MobEffectModifier(@NonNull Optional<LootItemCondition> condition, @NonNull Holder<MobEffect> mobEffectHolder, int amplifier) {
        super(condition);

        this.mobEffectHolder = mobEffectHolder;
        this.amplifier = amplifier;
    }

    @Override
    @NonNull
    protected LivingEntity run(@NonNull LivingEntity value, @NonNull LootContext lootContext) {
        value.addEffect(new MobEffectInstance(mobEffectHolder, -1, amplifier));
        return value;
    }
}
