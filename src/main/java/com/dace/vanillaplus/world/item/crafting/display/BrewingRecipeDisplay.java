package com.dace.vanillaplus.world.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * 물약 양조법 디스플레이 클래스.
 *
 * @param base            기반 아이템
 * @param ingredient      재료 아이템
 * @param result          출력 아이템
 * @param craftingStation 작업 블록
 */
public record BrewingRecipeDisplay(@NonNull SlotDisplay base, @NonNull SlotDisplay ingredient, @NonNull SlotDisplay result,
                                   @NonNull SlotDisplay craftingStation) implements RecipeDisplay {
    private static final MapCodec<BrewingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(SlotDisplay.CODEC.fieldOf("base").forGetter(brewingRecipeDisplay -> brewingRecipeDisplay.base),
                    SlotDisplay.CODEC.fieldOf("ingredient").forGetter(brewingRecipeDisplay -> brewingRecipeDisplay.ingredient),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(BrewingRecipeDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(BrewingRecipeDisplay::craftingStation))
            .apply(instance, BrewingRecipeDisplay::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC, brewingRecipeDisplay -> brewingRecipeDisplay.base,
            SlotDisplay.STREAM_CODEC, brewingRecipeDisplay -> brewingRecipeDisplay.ingredient,
            SlotDisplay.STREAM_CODEC, BrewingRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC, BrewingRecipeDisplay::craftingStation,
            BrewingRecipeDisplay::new);
    /** 디스플레이 타입 */
    public static final Type<BrewingRecipeDisplay> DISPLAY_TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    @NonNull
    public Type<BrewingRecipeDisplay> type() {
        return DISPLAY_TYPE;
    }

    @Override
    public boolean isEnabled(@NonNull FeatureFlagSet enabledFeatures) {
        return base.isEnabled(enabledFeatures) && ingredient.isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
    }
}
