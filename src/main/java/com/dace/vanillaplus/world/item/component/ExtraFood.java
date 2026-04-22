package com.dace.vanillaplus.world.item.component;

import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 음식 아이템의 첨가물을 나타내는 데이터 요소 클래스.
 *
 * @param itemHolder 음식 아이템 홀더 인스턴스. {@link DataComponents#FOOD} 데이터 요소가 있어야 함
 */
public record ExtraFood(@NonNull Holder<Item> itemHolder) implements ConsumableListener {
    /** JSON 코덱 */
    public static final Codec<ExtraFood> CODEC = Item.CODEC.xmap(ExtraFood::new, extraFood -> extraFood.itemHolder);
    /** 스트림 코덱 */
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraFood> STREAM_CODEC = Item.STREAM_CODEC
            .map(ExtraFood::new, extraFood -> extraFood.itemHolder);

    @Override
    public void onConsume(@NonNull Level level, @NonNull LivingEntity livingEntity, @NonNull ItemStack itemStack, @NonNull Consumable consumable) {
        FoodProperties foodProperties = getFoodProperties();
        if (foodProperties != null && livingEntity instanceof Player player)
            player.getFoodData().eat(foodProperties);
    }

    /**
     * 첨가물 아이템의 이름을 반환한다.
     *
     * @return 아이템 이름
     */
    @NonNull
    public Component getNameComponent() {
        return itemHolder.value().components().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }

    /**
     * 첨가물 아이템의 {@link DataComponents#FOOD} 데이터 요소를 반환한다.
     *
     * @return {@link FoodProperties}
     */
    @Nullable
    public FoodProperties getFoodProperties() {
        return itemHolder.value().components().get(DataComponents.FOOD);
    }
}
