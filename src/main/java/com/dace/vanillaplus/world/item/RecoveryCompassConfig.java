package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecoveryCompassItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param enableTeleport  순간이동 활성화 여부
 * @param teleportEffects 순간이동 시 적용할 상태 효과 목록
 */
public record RecoveryCompassConfig(boolean enableTeleport, @NonNull List<ConsumeEffect> teleportEffects) {
    /** 기본값 */
    private static final RecoveryCompassConfig DEFAULT = new RecoveryCompassConfig(false, Collections.emptyList());
    /** JSON 코덱 */
    public static final Codec<RecoveryCompassConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("enable_teleport", DEFAULT.enableTeleport).forGetter(RecoveryCompassConfig::enableTeleport),
                    ConsumeEffect.CODEC.listOf().optionalFieldOf("teleport_effects", DEFAULT.teleportEffects)
                            .forGetter(RecoveryCompassConfig::teleportEffects))
            .apply(instance, RecoveryCompassConfig::new));

    @NonNull
    public static RecoveryCompassConfig get() {
        return VPItem.cast(Items.RECOVERY_COMPASS).getConfigComponents().getOrDefault(ItemConfigComponentTypes.RECOVERY_COMPASS, DEFAULT);
    }
}
