package com.dace.vanillaplus.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;

import java.util.Optional;
import java.util.function.Function;

/**
 * 마법 부여 효과 {@link EnchantmentEffectComponents#REPAIR_WITH_XP}가 사용하는 아이템 기반 수리 한도 클래스.
 *
 * <p>적용된 아이템에는 수리 한도 막대가 표시된다.</p>
 *
 * @param maxRepairLimitRatio 최대 수리 한도 비율
 * @param requiredItem        필요 아이템
 * @param barColor            수리 한도 막대 색상
 */
public record RepairWithItem(float maxRepairLimitRatio, @NonNull Optional<Holder<Item>> requiredItem, int barColor) {
    /** JSON 코덱 */
    public static final Codec<RepairWithItem> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.floatRange(0, 1).fieldOf("max_repair_limit_ratio").forGetter(RepairWithItem::maxRepairLimitRatio),
                    Item.CODEC.optionalFieldOf("required_item").forGetter(RepairWithItem::requiredItem),
                    ExtraCodecs.RGB_COLOR_CODEC.xmap(ARGB::opaque, Function.identity()).fieldOf("bar_color").forGetter(RepairWithItem::barColor))
            .apply(instance, RepairWithItem::new));
}
