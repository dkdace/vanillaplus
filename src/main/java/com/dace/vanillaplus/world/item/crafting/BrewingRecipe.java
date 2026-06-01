package com.dace.vanillaplus.world.item.crafting;

import com.dace.vanillaplus.data.registryobject.VPRecipeBookCategories;
import com.dace.vanillaplus.data.registryobject.VPRecipeTypes;
import com.dace.vanillaplus.world.item.crafting.display.BrewingRecipeDisplay;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 양조기를 사용하는 물약 양조법을 나타내는 클래스.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BrewingRecipe implements Recipe<BrewingRecipe.Input> {
    /** 재료 목록 리소스 키 */
    public static final ResourceKey<RecipePropertySet> INGREDIENT_SET = VPRecipeTypes.createPropertySet("brewing_ingredient");
    /** 기본 양조 시간 */
    private static final int DEFAULT_BREWING_TIME = 400;
    /** 작업 블록 슬롯 디스플레이 */
    private static final SlotDisplay CRAFTING_STATION = new SlotDisplay.ItemSlotDisplay(Items.BREWING_STAND);

    /** 기본 정보 */
    protected final CommonInfo commonInfo;
    /** 재료 아이템 */
    @NonNull
    @Getter
    private final Ingredient ingredient;
    /** 양조 시간 */
    @Getter
    private final int brewingTime;
    /** 배치 정보 */
    @Nullable
    private PlacementInfo placementInfo;

    /**
     * 모든 물약 용기 아이템을 반환한다.
     *
     * @return 물약 용기 아이템 홀더 목록
     */
    @NonNull
    public static HolderSet<Item> getPotionContainers() {
        return BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTIONS_BOTTLE);
    }

    /**
     * @return 기반 아이템
     */
    @NonNull
    public abstract Ingredient getBase();

    @Override
    @MustBeInvokedByOverriders
    public boolean matches(@NonNull Input input, @NonNull Level level) {
        return getBase().test(input.base) && ingredient.test(input.ingredient);
    }

    @Override
    public boolean showNotification() {
        return commonInfo.showNotification();
    }

    @Override
    @NonNull
    public String group() {
        return "";
    }

    @Override
    @NonNull
    public abstract RecipeSerializer<? extends BrewingRecipe> getSerializer();

    @Override
    @NonNull
    public final RecipeType<? extends BrewingRecipe> getType() {
        return VPRecipeTypes.BREWING.get();
    }

    @Override
    @NonNull
    public final PlacementInfo placementInfo() {
        if (placementInfo == null)
            placementInfo = PlacementInfo.create(List.of(getBase(), ingredient));

        return placementInfo;
    }

    @Override
    @NonNull
    public final RecipeBookCategory recipeBookCategory() {
        return VPRecipeBookCategories.BREWING.get();
    }

    /**
     * 물약을 다른 물약으로 바꾸는 양조법 클래스.
     */
    public static final class Mix extends BrewingRecipe {
        private static final MapCodec<Mix> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(CommonInfo.MAP_CODEC.forGetter(mix -> mix.commonInfo),
                        Potion.CODEC.fieldOf("base").forGetter(mix -> mix.basePotion),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(BrewingRecipe::getIngredient),
                        Potion.CODEC.fieldOf("result").forGetter(mix -> mix.resultPotion),
                        Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(BrewingRecipe::getBrewingTime))
                .apply(instance, Mix::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, Mix> STREAM_CODEC = StreamCodec.composite(
                CommonInfo.STREAM_CODEC, mix -> mix.commonInfo,
                Potion.STREAM_CODEC, mix -> mix.basePotion,
                Ingredient.CONTENTS_STREAM_CODEC, BrewingRecipe::getIngredient,
                Potion.STREAM_CODEC, mix -> mix.resultPotion,
                ByteBufCodecs.INT, BrewingRecipe::getBrewingTime,
                Mix::new);
        /** 직렬화 처리기 */
        public static final RecipeSerializer<Mix> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        /** 입력 물약 홀더 인스턴스 */
        private final Holder<Potion> basePotion;
        /** 출력 물약 홀더 인스턴스 */
        private final Holder<Potion> resultPotion;

        private Mix(@NonNull CommonInfo commonInfo, @NonNull Holder<Potion> basePotion, @NonNull Ingredient ingredient,
                    @NonNull Holder<Potion> resultPotion, int brewingTime) {
            super(commonInfo, ingredient, brewingTime);

            this.basePotion = basePotion;
            this.resultPotion = resultPotion;
        }

        @NonNull
        private static SlotDisplay.ItemStackSlotDisplay createPotionDisplay(@NonNull Item item, @NonNull Holder<Potion> potionHolder) {
            return new SlotDisplay.ItemStackSlotDisplay(
                    new ItemStackTemplate(item, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder)).build()));
        }

        @Override
        @NonNull
        public RecipeSerializer<? extends BrewingRecipe> getSerializer() {
            return SERIALIZER;
        }

        @Override
        public boolean matches(@NonNull Input input, @NonNull Level level) {
            if (!super.matches(input, level))
                return false;

            PotionContents potionContents = input.base.get(DataComponents.POTION_CONTENTS);
            return potionContents != null && potionContents.is(basePotion);
        }

        @Override
        @NonNull
        public Ingredient getBase() {
            return Ingredient.of(getPotionContainers());
        }

        @Override
        @NonNull
        public ItemStack assemble(@NonNull Input input) {
            ItemStack itemStack = input.base;
            itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(resultPotion));

            return itemStack;
        }

        @Override
        @NonNull
        public List<RecipeDisplay> display() {
            return getPotionContainers().stream()
                    .map(itemHolder -> (RecipeDisplay) new BrewingRecipeDisplay(createPotionDisplay(itemHolder.value(), basePotion),
                            super.ingredient.display(), createPotionDisplay(itemHolder.value(), resultPotion), CRAFTING_STATION))
                    .toList();
        }
    }

    /**
     * 물약의 용기를 다른 아이템으로 바꾸는 양조법 클래스.
     */
    public static final class Transmute extends BrewingRecipe {
        private static final MapCodec<Transmute> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(CommonInfo.MAP_CODEC.forGetter(transmute -> transmute.commonInfo),
                        Ingredient.CODEC.fieldOf("base").forGetter(BrewingRecipe::getBase),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(BrewingRecipe::getIngredient),
                        ItemStackTemplate.CODEC.fieldOf("result").forGetter(transmute -> transmute.result),
                        Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(BrewingRecipe::getBrewingTime))
                .apply(instance, Transmute::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, Transmute> STREAM_CODEC = StreamCodec.composite(
                CommonInfo.STREAM_CODEC, transmute -> transmute.commonInfo,
                Ingredient.CONTENTS_STREAM_CODEC, BrewingRecipe::getBase,
                Ingredient.CONTENTS_STREAM_CODEC, BrewingRecipe::getIngredient,
                ItemStackTemplate.STREAM_CODEC, transmute -> transmute.result,
                ByteBufCodecs.INT, BrewingRecipe::getBrewingTime,
                Transmute::new);
        /** 직렬화 처리기 */
        public static final RecipeSerializer<Transmute> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        /** 기반 아이템 */
        private final Ingredient base;
        /** 출력 아이템 */
        private final ItemStackTemplate result;

        private Transmute(@NonNull CommonInfo commonInfo, @NonNull Ingredient base, @NonNull Ingredient ingredient, @NonNull ItemStackTemplate result,
                          int brewingTime) {
            super(commonInfo, ingredient, brewingTime);

            this.base = base;
            this.result = result;
        }

        @Override
        @NonNull
        public RecipeSerializer<? extends BrewingRecipe> getSerializer() {
            return SERIALIZER;
        }

        @Override
        @NonNull
        public Ingredient getBase() {
            return base;
        }

        @Override
        @NonNull
        public ItemStack assemble(@NonNull Input input) {
            return result.apply(input.base.getComponentsPatch());
        }

        @Override
        @NonNull
        public List<RecipeDisplay> display() {
            return List.of(new BrewingRecipeDisplay(new SlotDisplay.WithAnyPotion(base.display()), super.ingredient.display(),
                    new SlotDisplay.WithAnyPotion(new SlotDisplay.ItemStackSlotDisplay(result)), CRAFTING_STATION));
        }
    }

    /**
     * 양조법의 입력을 관리하는 클래스.
     */
    @AllArgsConstructor
    public static final class Input implements RecipeInput {
        /** 기반 아이템 */
        private final ItemStack base;
        /** 재료 아이템 */
        private final ItemStack ingredient;

        @Override
        @NonNull
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> base;
                case 1 -> ingredient;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }
}
