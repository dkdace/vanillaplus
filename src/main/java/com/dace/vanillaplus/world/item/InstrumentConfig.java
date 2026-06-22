package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;

import java.util.Optional;

/**
 * {@link InstrumentItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param useDuration 사용 시간
 */
public record InstrumentConfig(@NonNull Optional<Integer> useDuration) {
    /** JSON 코덱 */
    public static final Codec<InstrumentConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).optionalFieldOf("use_duration_seconds")
                    .forGetter(InstrumentConfig::useDuration))
            .apply(instance, InstrumentConfig::new));
    /** 기본값 */
    private static final InstrumentConfig DEFAULT = new InstrumentConfig(Optional.empty());

    /**
     * @param item 대상 아이템
     * @return {@link InstrumentConfig}
     */
    @NonNull
    public static InstrumentConfig get(@NonNull Item item) {
        return VPItem.cast(item).getConfigComponents().getOrDefault(ItemConfigComponentTypes.INSTRUMENT, DEFAULT);
    }
}
