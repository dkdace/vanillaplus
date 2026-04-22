package com.dace.vanillaplus.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;

import java.util.Optional;
import java.util.function.Function;

/**
 * 마법 부여 효과 {@link EnchantmentEffectComponents#REPAIR_WITH_XP}의 수리 한도를 나타내는 데이터 요소 클래스.
 *
 * <p>적용된 아이템에는 수리 한도 막대가 표시된다.</p>
 *
 * @param maxRepairLimitRatio 최대 수리 한도 비율
 * @param requiredItem        필요 아이템
 * @param barColor            수리 한도 막대 색상
 */
public record RepairWithXP(float maxRepairLimitRatio, @NonNull Optional<Holder<Item>> requiredItem, int barColor) {
    /** JSON 코덱 */
    public static final Codec<RepairWithXP> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.floatRange(0, 1).fieldOf("max_repair_limit_ratio")
                            .forGetter(RepairWithXP::maxRepairLimitRatio),
                    Item.CODEC.optionalFieldOf("required_item").forGetter(RepairWithXP::requiredItem),
                    ExtraCodecs.RGB_COLOR_CODEC.xmap(ARGB::opaque, Function.identity()).fieldOf("bar_color").forGetter(RepairWithXP::barColor))
            .apply(instance, RepairWithXP::new));
    /** 스트림 코덱 */
    public static final StreamCodec<RegistryFriendlyByteBuf, RepairWithXP> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, RepairWithXP::maxRepairLimitRatio,
            ByteBufCodecs.optional(Item.STREAM_CODEC), RepairWithXP::requiredItem,
            ByteBufCodecs.RGB_COLOR, RepairWithXP::barColor,
            RepairWithXP::new);

    /** 기본값 */
    public static final RepairWithXP DEFAULT = new RepairWithXP(0.4F,
            Optional.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.LAPIS_LAZULI)), ARGB.color(50, 50, 255));
}
