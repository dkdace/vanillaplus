package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPDataComponentTypes {
    public static final RegistryObject<DataComponentType<Integer>> REPAIR_LIMIT = create("repair_limit", builder ->
            builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final RegistryObject<DataComponentType<Holder<TrimPattern>>> PROVIDES_TRIM_PATTERN = create("provides_trim_pattern", builder ->
            builder.persistent(TrimPattern.CODEC).networkSynchronized(TrimPattern.STREAM_CODEC).cacheEncoding());
    public static final RegistryObject<DataComponentType<Integer>> ENCHANTMENT_LEVEL_MULTIPLIER = create("enchantment_level_multiplier", builder ->
            builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
    public static final RegistryObject<DataComponentType<Float>> SMELTING_DAMAGE_RATIO = create("smelting_damage_ratio", builder ->
            builder.persistent(ExtraCodecs.floatRange(0, 1)).networkSynchronized(ByteBufCodecs.FLOAT).cacheEncoding());
    public static final RegistryObject<DataComponentType<RepairWithXP>> REPAIR_WITH_XP = create("repair_with_xp", builder ->
            builder.persistent(RepairWithXP.CODEC).networkSynchronized(RepairWithXP.STREAM_CODEC).cacheEncoding());
    public static final RegistryObject<DataComponentType<Long>> SEED = create("seed", builder ->
            builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
    public static final RegistryObject<DataComponentType<ExtraFood>> EXTRA_FOOD = create("extra_food", builder ->
            builder.persistent(ExtraFood.CODEC).networkSynchronized(ExtraFood.STREAM_CODEC).cacheEncoding());

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return VPRegistry.register(VPRegistry.DATA_COMPONENT_TYPE, name, onBuilder.apply(DataComponentType.builder())::build);
    }

    @EqualsAndHashCode
    @Getter
    public static final class RepairWithXP {
        public static final RepairWithXP DEFAULT = new RepairWithXP(0.4F,
                Optional.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.LAPIS_LAZULI)), ARGB.color(50, 50, 255));

        private static final Codec<RepairWithXP> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(ExtraCodecs.floatRange(0, 1).fieldOf("max_repair_limit_ratio")
                                .forGetter(RepairWithXP::getMaxRepairLimitRatio),
                        Item.CODEC.optionalFieldOf("required_item").forGetter(RepairWithXP::getRequiredItem),
                        ExtraCodecs.RGB_COLOR_CODEC.fieldOf("bar_color").forGetter(RepairWithXP::getBarColor))
                .apply(instance, RepairWithXP::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, RepairWithXP> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, RepairWithXP::getMaxRepairLimitRatio,
                ByteBufCodecs.optional(Item.STREAM_CODEC), RepairWithXP::getRequiredItem,
                ByteBufCodecs.RGB_COLOR, RepairWithXP::getBarColor,
                RepairWithXP::new);

        private final float maxRepairLimitRatio;
        @NonNull
        private final Optional<Holder<Item>> requiredItem;
        private final int barColor;

        private RepairWithXP(float maxRepairLimitRatio, @NonNull Optional<Holder<Item>> requiredItem, int barColor) {
            this.maxRepairLimitRatio = maxRepairLimitRatio;
            this.requiredItem = requiredItem;
            this.barColor = ARGB.opaque(barColor);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static final class ExtraFood implements ConsumableListener {
        private static final Codec<ExtraFood> CODEC = Item.CODEC.xmap(ExtraFood::new, extraFood -> extraFood.itemHolder);
        private static final StreamCodec<RegistryFriendlyByteBuf, ExtraFood> STREAM_CODEC = Item.STREAM_CODEC
                .map(ExtraFood::new, extraFood -> extraFood.itemHolder);

        private final Holder<Item> itemHolder;

        @Override
        public void onConsume(@NonNull Level level, @NonNull LivingEntity livingEntity, @NonNull ItemStack itemStack, @NonNull Consumable consumable) {
            if (getFoodProperties() != null && livingEntity instanceof Player player)
                player.getFoodData().eat(getFoodProperties());
        }

        @NonNull
        public Component getNameComponent() {
            return itemHolder.value().getName();
        }

        @Nullable
        public FoodProperties getFoodProperties() {
            return itemHolder.value().components().get(DataComponents.FOOD);
        }
    }
}
