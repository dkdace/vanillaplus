package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;

import java.util.Optional;

/**
 * {@link CrossbowItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param shootingPowerFireworkRocket 폭죽 발사 속력
 */
public record CrossbowConfig(@NonNull Optional<Float> shootingPowerFireworkRocket) {
    /** 기본값 */
    public static final CrossbowConfig DEFAULT = new CrossbowConfig(Optional.empty());
    /** JSON 코덱 */
    public static final Codec<CrossbowConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power_firework_rocket")
                    .forGetter(CrossbowConfig::shootingPowerFireworkRocket))
            .apply(instance, CrossbowConfig::new));

    @NonNull
    public static CrossbowConfig get() {
        return VPItem.cast(Items.CROSSBOW).getConfigComponents().getOrDefault(ItemConfigComponentTypes.CROSSBOW, DEFAULT);
    }
}
