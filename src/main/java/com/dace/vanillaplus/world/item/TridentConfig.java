package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.ItemConfigComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;

import java.util.Optional;

/**
 * {@link TridentItem}의 아이템 설정 데이터 요소 클래스.
 *
 * @param riptideCooldown    급류 돌진 시 쿨타임
 * @param riptidePiercing    급류 엔티티 관통 여부
 * @param projectilePiercing 던진 삼지창의 엔티티 관통 여부
 */
public record TridentConfig(@NonNull Optional<Integer> riptideCooldown, boolean riptidePiercing, boolean projectilePiercing) {
    /** 기본값 */
    private static final TridentConfig DEFAULT = new TridentConfig(Optional.empty(), false, false);
    /** JSON 코덱 */
    public static final Codec<TridentConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(CodecUtil.secondsToTicks(ExtraCodecs.POSITIVE_FLOAT).optionalFieldOf("riptide_cooldown_seconds")
                            .forGetter(TridentConfig::riptideCooldown),
                    Codec.BOOL.optionalFieldOf("riptide_piercing", DEFAULT.riptidePiercing).forGetter(TridentConfig::riptidePiercing),
                    Codec.BOOL.optionalFieldOf("projectile_piercing", DEFAULT.projectilePiercing).forGetter(TridentConfig::projectilePiercing))
            .apply(instance, TridentConfig::new));

    /**
     * @return {@link TridentConfig}
     */
    @NonNull
    public static TridentConfig get() {
        return VPItem.cast(Items.TRIDENT).getConfigComponents().getOrDefault(ItemConfigComponentTypes.TRIDENT, DEFAULT);
    }
}
