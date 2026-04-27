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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTIONS);
    }

    @NonNull
    private static SlotDisplay.ItemStackSlotDisplay createPotionDisplay(@NonNull Item item, @NonNull Holder<Potion> potionHolder) {
        return new SlotDisplay.ItemStackSlotDisplay(
                new ItemStackTemplate(item, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder)).build()));
    }

    /**
     * @return 기반 아이템
     */
    @NonNull
    public Ingredient getBase() {
        return Ingredient.of(getPotionContainers());
    }

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
     * 투입 물약과 산출 물약이 1:1로 매핑된 양조법 클래스.
     *
     * @deprecated 확장성을 위해 제거 예정. {@link Mix}로 대체 가능
     */
    @Deprecated
    public static final class Mapped extends BrewingRecipe {
        private static final MapCodec<Mapped> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(CommonInfo.MAP_CODEC.forGetter(mapped -> mapped.commonInfo),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(BrewingRecipe::getIngredient),
                        Codec.unboundedMap(Potion.CODEC, Potion.CODEC).fieldOf("result_map").forGetter(mapped -> mapped.resultPotionMap),
                        Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(BrewingRecipe::getBrewingTime))
                .apply(instance, Mapped::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, Mapped> STREAM_CODEC = StreamCodec.composite(
                CommonInfo.STREAM_CODEC, mapped -> mapped.commonInfo,
                Ingredient.CONTENTS_STREAM_CODEC, BrewingRecipe::getIngredient,
                ByteBufCodecs.map(HashMap::new, Potion.STREAM_CODEC, Potion.STREAM_CODEC), mapped -> mapped.resultPotionMap,
                ByteBufCodecs.INT, BrewingRecipe::getBrewingTime,
                Mapped::new);
        /** 직렬화 처리기 */
        public static final RecipeSerializer<Mapped> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        /** 입력 물약 : 출력 물약 */
        private final Map<Holder<Potion>, Holder<Potion>> resultPotionMap;

        private Mapped(@NonNull CommonInfo commonInfo, @NonNull Ingredient ingredient, @NonNull Map<Holder<Potion>, Holder<Potion>> resultPotionMap,
                       int brewingTime) {
            super(commonInfo, ingredient, brewingTime);
            this.resultPotionMap = resultPotionMap;
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

            return getResult(input.base) != null;
        }

        @Override
        @NonNull
        public ItemStack assemble(@NonNull Input input) {
            ItemStack itemStack = getResult(input.base);
            return itemStack == null ? input.base : itemStack;
        }

        @Nullable
        public ItemStack getResult(@NonNull ItemStack itemStack) {
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

            return potionContents.potion().map(potionHolder -> {
                Holder<Potion> resultPotionHolder = resultPotionMap.get(potionHolder);
                if (resultPotionHolder == null)
                    return null;

                ItemStack resultItemStack = itemStack.copy();
                resultItemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(resultPotionHolder));

                return resultItemStack;
            }).orElse(null);
        }

        @Override
        @NonNull
        public List<RecipeDisplay> display() {
            return getPotionContainers().stream()
                    .flatMap(itemHolder -> resultPotionMap.entrySet().stream().map(entry ->
                            (RecipeDisplay) new BrewingRecipeDisplay(createPotionDisplay(itemHolder.value(), entry.getKey()),
                                    super.ingredient.display(), createPotionDisplay(itemHolder.value(), entry.getValue()), CRAFTING_STATION)))
                    .toList();
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
