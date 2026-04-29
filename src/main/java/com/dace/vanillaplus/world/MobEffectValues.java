package com.dace.vanillaplus.world;

import com.dace.vanillaplus.world.item.enchantment.Described;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * 상태 효과의 값을 관리하는 클래스.
 */
@EqualsAndHashCode
public final class MobEffectValues {
    /** JSON 코덱 */
    public static final Codec<MobEffectValues> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(Codec.STRING, Described.TYPED_CODEC.codec()).fieldOf("values")
                    .forGetter(mobEffectValues -> mobEffectValues.describedMap))
            .apply(instance, MobEffectValues::new));
    /** 이름별 레벨 기반 값 목록 (이름 : 레벨 기반 값) */
    @NonNull
    private final TreeMap<String, Described> describedMap;

    private MobEffectValues(@NonNull Map<String, Described> describedMap) {
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
     * @throws NullPointerException 해당하는 상태 효과 값이 존재하지 않으면 발생
     */
    public float calculate(@NonNull String name, int amplifier) {
        return Objects.requireNonNull(describedMap.get(name)).calculate(amplifier + 1);
    }
}
