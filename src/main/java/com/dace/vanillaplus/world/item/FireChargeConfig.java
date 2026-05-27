package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Items;

/**
 * {@link FireChargeItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param enableThrowing 투척 활성화 여부
 */
public record FireChargeConfig(boolean enableThrowing) {
    /** 기본값 */
    private static final FireChargeConfig DEFAULT = new FireChargeConfig(false);
    /** JSON 코덱 */
    public static final Codec<FireChargeConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("enable_throwing", DEFAULT.enableThrowing).forGetter(FireChargeConfig::enableThrowing))
            .apply(instance, FireChargeConfig::new));

    /**
     * @return {@link FireChargeConfig}
     */
    @NonNull
    public static FireChargeConfig get() {
        return VPItem.cast(Items.FIRE_CHARGE).getConfigComponents().getOrDefault(ItemConfigComponentTypes.FIRE_CHARGE, DEFAULT);
    }
}
