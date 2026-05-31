package com.dace.vanillaplus.world;

import com.dace.vanillaplus.world.item.enchantment.Described;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * 상태 효과의 값을 관리하는 클래스.
 */
@EqualsAndHashCode
public final class MobEffectValues {
    /** JSON 코덱 */
    public static final Codec<MobEffectValues> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(Identifier.CODEC, Described.TYPED_CODEC.codec()).fieldOf("values")
                    .forGetter(mobEffectValues -> mobEffectValues.describedMap))
            .apply(instance, MobEffectValues::new));
    /** 기본값 */
    public static final MobEffectValues EMPTY = new MobEffectValues(Collections.emptyMap());

    /** 식별자별 레벨 기반 값 목록 (식별자 : 레벨 기반 값) */
    @NonNull
    private final TreeMap<Identifier, Described> describedMap;

    private MobEffectValues(@NonNull Map<Identifier, Described> describedMap) {
        this.describedMap = new TreeMap<>(Comparator.comparing(describedMap::get));
        this.describedMap.putAll(describedMap);
    }

    /**
     * 상태 효과 값 목록을 반환한다.
     *
     * @return 상태 효과 값 목록
     */
    @NonNull
    @UnmodifiableView
    public Collection<Described> getValues() {
        return Collections.unmodifiableCollection(describedMap.values());
    }

    /**
     * 지정한 이름에 해당하는 상태 효과 값을 계산하여 반환한다.
     *
     * @param name      이름
     * @param amplifier 효과 레벨
     * @return 계산된 값
     */
    @NonNull
    public Optional<Float> calculate(@NonNull Identifier name, int amplifier) {
        Described described = describedMap.get(name);
        return described == null ? Optional.empty() : Optional.of(described.calculate(amplifier + 1));
    }
}
