package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * 모드에서 사용하는 제작법 디스플레이를 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeDisplayTypes {
    static {
        VPRegistry.RECIPE_DISPLAY.register("brewing", () -> Brewing.TYPE);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Brewing implements RecipeDisplay {
        private static final MapCodec<Brewing> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(SlotDisplay.CODEC.fieldOf("base").forGetter(brewing -> brewing.base),
                        SlotDisplay.CODEC.fieldOf("ingredient").forGetter(brewing -> brewing.ingredient),
                        SlotDisplay.CODEC.fieldOf("result").forGetter(Brewing::result))
                .apply(instance, Brewing::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, Brewing> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC, brewing -> brewing.base,
                SlotDisplay.STREAM_CODEC, brewing -> brewing.ingredient,
                SlotDisplay.STREAM_CODEC, Brewing::result,
                Brewing::new);
        private static final Type<Brewing> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        private static final SlotDisplay CRAFTING_STATION = new SlotDisplay.ItemSlotDisplay(Items.BREWING_STAND);

        private final SlotDisplay base;
        private final SlotDisplay ingredient;
        private final SlotDisplay result;

        public Brewing(@NonNull Item baseItem, @NonNull Holder<Potion> basePotion, @NonNull SlotDisplay ingredient, @NonNull Item resultItem,
                       @NonNull Holder<Potion> resultPotion) {
            this.base = createPotionDisplay(baseItem, basePotion);
            this.ingredient = ingredient;
            this.result = createPotionDisplay(resultItem, resultPotion);
        }

        @NonNull
        private static SlotDisplay createPotionDisplay(@NonNull Item item, @NonNull Holder<Potion> potionHolder) {
            return new SlotDisplay.ItemStackSlotDisplay(PotionContents.createItemStack(item, potionHolder));
        }

        @Override
        @NonNull
        public Type<Brewing> type() {
            return TYPE;
        }

        @Override
        @NonNull
        public SlotDisplay result() {
            return result;
        }

        @Override
        @NonNull
        public SlotDisplay craftingStation() {
            return CRAFTING_STATION;
        }

        @Override
        public boolean isEnabled(@NonNull FeatureFlagSet enabledFeatures) {
            return base.isEnabled(enabledFeatures) && ingredient.isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
        }
    }
}
