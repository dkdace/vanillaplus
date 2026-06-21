package com.dace.vanillaplus.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 같은 흉조 레벨을 가진 아이템 둘을 합쳐 흉조 레벨을 증가시키는 조합법을 나타내는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BadOmenUpgradingRecipe extends CustomRecipe {
    /** JSON 코덱 */
    private static final MapCodec<BadOmenUpgradingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Ingredient.CODEC.fieldOf("material")
                            .forGetter(badOmenUpgradingRecipe -> badOmenUpgradingRecipe.material),
                    ExtraCodecs.intRange(0, Byte.MAX_VALUE).fieldOf("max_amplifier")
                            .forGetter(badOmenUpgradingRecipe -> badOmenUpgradingRecipe.maxAmplifier))
            .apply(instance, BadOmenUpgradingRecipe::new));
    /** 스트림 코덱 */
    private static final StreamCodec<RegistryFriendlyByteBuf, BadOmenUpgradingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, badOmenUpgradingRecipe -> badOmenUpgradingRecipe.material,
            ByteBufCodecs.INT, badOmenUpgradingRecipe -> badOmenUpgradingRecipe.maxAmplifier,
            BadOmenUpgradingRecipe::new);
    /** 직렬화 처리기 */
    public static final RecipeSerializer<BadOmenUpgradingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    /** 재료 아이템 */
    private final Ingredient material;
    /** 최대 흉조 레벨 */
    private final int maxAmplifier;

    @Override
    @NonNull
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(@NonNull CraftingInput input, @NonNull Level level) {
        if (input.ingredientCount() != 2)
            return false;

        OminousBottleAmplifier badOmenAmplifier = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack = input.getItem(i);

            if (!itemStack.isEmpty()) {
                if (!material.test(itemStack))
                    return false;

                OminousBottleAmplifier ominousBottleAmplifier = itemStack.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
                if (ominousBottleAmplifier == null || ominousBottleAmplifier.value() >= maxAmplifier)
                    return false;

                if (badOmenAmplifier == null)
                    badOmenAmplifier = ominousBottleAmplifier;
                else if (!badOmenAmplifier.equals(ominousBottleAmplifier))
                    return false;
            }
        }

        return true;
    }

    @Override
    @NonNull
    public ItemStack assemble(@NonNull CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack = input.getItem(i);

            if (!itemStack.isEmpty()) {
                OminousBottleAmplifier ominousBottleAmplifier = itemStack.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);

                if (ominousBottleAmplifier != null) {
                    itemStack = itemStack.copyWithCount(1);
                    itemStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(ominousBottleAmplifier.value() + 1));

                    return itemStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
